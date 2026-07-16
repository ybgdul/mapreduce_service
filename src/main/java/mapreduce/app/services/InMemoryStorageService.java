package mapreduce.app.services;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Exceptions.JobStorageServiceException;
import mapreduce.app.utilities.Exceptions.MapTaskStorageServiceException;
import mapreduce.app.utilities.Exceptions.ReduceTaskStorageServiceException;
import mapreduce.app.utilities.Interfaces.StorageService;

@Service
public class InMemoryStorageService implements StorageService {

    private final ObjectMapper objectMapper;
    private final Path root = Paths.get("");

    public InMemoryStorageService( ObjectMapper objectMapper) throws IOException{
        Files.createDirectories(root);
        this.objectMapper = objectMapper;
    }

    @Override
    public StorageFileDto storeFile(MultipartFile file, Long jobId) {
        Path jobDirectory = root.resolve(jobId.toString());
        long size = 0;
        Path inputFile = null;
        try { 
            Files.createDirectories(jobDirectory);
            Files.createDirectories(jobDirectory.resolve("map"));
            Files.createDirectories(jobDirectory.resolve("reduce"));

            inputFile = jobDirectory.resolve("input.txt");

            try (InputStream in = file.getInputStream()) {
                Files.copy(in, inputFile, StandardCopyOption.REPLACE_EXISTING);
            }

            size = Files.size(inputFile);
        } catch (IOException e) {
            throw new JobStorageServiceException("Storing incoming job file has failed: " + e.getMessage());
        }

        String location = root.relativize(inputFile).toString();
        return new StorageFileDto(location, size);
    }

    @Override
    public StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Long sequence, byte[] data) {
        Path resultFile = root.resolve(jobId.toString()).resolve("map").resolve(mapTaskId.toString());

        try { 
            Files.write(resultFile, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            long size = Files.size(resultFile);
            return new StorageFileDto(root.relativize(resultFile).toString(), size);
        } catch (IOException e) { 
            throw new MapTaskStorageServiceException("Storing incoming map result file has failed: " + e.getMessage());
        }
    }

    @Override
    public StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Long sequence, Object result) {
        Path resultFile = root.resolve(jobId.toString()).resolve("map").resolve(mapTaskId.toString() + ".json");

        try { 
            objectMapper.writeValue(resultFile.toFile(), result);
            long size = Files.size(resultFile);
            return new StorageFileDto(root.relativize(resultFile).toString(), size);
        } catch (IOException e) { 
            throw new MapTaskStorageServiceException("Storing incoming map result JSON has failed: " + e.getMessage());
        }
    }

    @Override
    public StorageFileDto storeReduceResult(Long jobId, Long reduceTaskId, byte[] data) {
        Path resultFile = root.resolve(jobId.toString()).resolve("reduce").resolve(reduceTaskId.toString());

        try { 
            Files.write(resultFile, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            long size = Files.size(resultFile);
            return new StorageFileDto(root.relativize(resultFile).toString(), size);
        } catch (IOException e) { 
            throw new ReduceTaskStorageServiceException("Storing incoming reduce result file has failed: " + e.getMessage());
        }
    }

    @Override
    public StorageFileDto storeReduceResult(Long jobId, Long reduceTaskId, Object result) {
        Path resultFile = root.resolve(jobId.toString()).resolve("map").resolve(reduceTaskId.toString() + ".json");

        try { 
            objectMapper.writeValue(resultFile.toFile(), result);
            long size = Files.size(resultFile);
            return new StorageFileDto(root.relativize(resultFile).toString(), size);
        } catch (IOException e) { 
            throw new ReduceTaskStorageServiceException("Storing incoming reduce result JSON has failed: " + e.getMessage());
        }
    }

    @Override
    public StorageFileDto storeResult(Long jobId, byte[] data) { 
        Path resultFile = root.resolve(jobId.toString()).resolve("result.txt");

        try { 
            Files.write(resultFile, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            long size = Files.size(resultFile);
            return new StorageFileDto(root.relativize(resultFile).toString(), size);
        } catch (IOException e) { 
            throw new JobStorageServiceException("Storing incoming final result file has failed: " + e.getMessage());
        }
    }

    @Override
    public StorageFileDto storeResult(Long jobId, Object result) { 
        Path resultFile = root.resolve(jobId.toString()).resolve("result.json");

        try { 
            objectMapper.writeValue(resultFile.toFile(), result);
            long size = Files.size(resultFile);
            return new StorageFileDto(root.relativize(resultFile).toString(), size);
        } catch (IOException e) { 
            throw new JobStorageServiceException("Storing incoming final result JSON has failed: " + e.getMessage());
        }
    }

    @Override
    public InputStream loadFile(Long jobId, Long offsetStart, Long offsetEnd) {
        Path inputFile = root.resolve(jobId.toString()).resolve("input.txt");

        try { 
            RandomAccessFile file = new RandomAccessFile(inputFile.toFile(), "r");
            file.seek(offsetStart);

            long remaining = offsetEnd - offsetStart + 1;

            return new FilterInputStream(new FileInputStream(file.getFD())) { 
                private long remainingBytes = remaining;

                @Override
                public int read() throws IOException { 
                    if(remainingBytes <= 0) return -1;

                    int value = super.read();
                    if(value != -1) remainingBytes--;
                    return value;
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    if(remainingBytes <= 0) return -1;

                    int bytesToRead = (int) Math.min(len, remainingBytes);
                    int read = super.read(b, off, bytesToRead);

                    if(read != -1) remainingBytes--;

                    return read;
                }

                @Override 
                public void close() throws IOException { 
                    super.close();
                    file.close();
                }
            };
        } catch (IOException e) { 
            throw new JobStorageServiceException("Loading file chunk has failed: "  + e.getMessage());
        }
    }

    @Override
    public InputStream loadFile(Long jobId) {
        Path resultFile = root.resolve(jobId.toString()).resolve("result.txt");
        try { 
            return Files.newInputStream(resultFile, StandardOpenOption.READ);
        } catch (IOException e){ 
            throw new JobStorageServiceException("Loading job result has failed: "+ e.getMessage());
        }
    }

    @Override
    public InputStream loadMapResult(Long jobId, Long mapTaskId) {
        Path resultFile = root.resolve(jobId.toString()).resolve("map").resolve(mapTaskId.toString());

        try {
            return Files.newInputStream(resultFile, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new MapTaskStorageServiceException("Loading map result has failed: "+ e.getMessage());
        }
    }

    @Override
    public InputStream loadReduceResult(Long jobId, Long reduceTaskId) {
        Path resultFile = root.resolve(jobId.toString()).resolve("reduce").resolve(reduceTaskId.toString());

        try {
            return Files.newInputStream(resultFile, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new ReduceTaskStorageServiceException("Loading reduce result has failed: "+ e.getMessage());
        }
    }

    @Override
    public void delete(Long jobId, Long taskId, boolean isMap) {
        Path directory = root.resolve(jobId.toString()).resolve( (isMap) ? "map" : "reduce");

        Path file = directory.resolve(taskId.toString());
        Path jsonFile = directory.resolve(taskId.toString() + ".json");

        try {
            Files.deleteIfExists(file);
            Files.deleteIfExists(jsonFile);
        } catch (IOException e) {
            //I think it's not fatal
        }

    }

    @Override 
    public void terminate(Long jobId){ 
        Path jobDirectory = root.resolve(jobId.toString());

        if(!Files.exists(jobDirectory)) { return; }
        
        try {
            Files.walk(jobDirectory).sorted(Comparator.reverseOrder()).forEach(
                path -> {
                    try { 
                        Files.delete(path);
                    } catch (IOException e) { 
                        throw new UncheckedIOException(e);
                    }
                }
            );
        } catch (IOException e) { 

        } catch (UncheckedIOException e) {
            
        }

    }
    
}
