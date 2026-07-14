package mapreduce.app.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

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
    public InputStream loadFile(Long jobId, Long offsetStart, Long offsetEnd) {
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
    public InputStream loadMapResult(Long jobId, Long mapTaskId, Long sequence) {
        Path resultFile = root.resolve(jobId.toString()).resolve("map").resolve(mapTaskId.toString());

        try {
            return Files.newInputStream(resultFile, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new MapTaskStorageServiceException("Loading map result has failed: "+ e.getMessage());
        }
    }

    @Override
    public InputStream loadReduceResult(Long jobId, Long reduceTaskId, Long sequence) {
        Path resultFile = root.resolve(jobId.toString()).resolve("reduce").resolve(reduceTaskId.toString());

        try {
            return Files.newInputStream(resultFile, StandardOpenOption.READ);
        } catch (IOException e) {
            throw new ReduceTaskStorageServiceException("Loading reduce result has failed: "+ e.getMessage());
        }
    }

    @Override
    public void delete(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    
}
