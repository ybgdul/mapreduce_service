package mapreduce.app.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Exceptions.JobStorageServiceException;
import mapreduce.app.utilities.Interfaces.StorageService;

@Service
public class InMemoryStorageService implements StorageService {

    private final Path root = Paths.get("");

    public InMemoryStorageService( ) throws IOException{
        Files.createDirectories(root);
    }

    @Override
    public StorageFileDto storeFile(MultipartFile file, Long jobId) {
        Path jobDirectory = root.resolve(jobId.toString());
        long size = 0;
        Path inputFile = null;
        try { 
            Files.createDirectory(jobDirectory);

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeMapResult'");
    }

    @Override
    public StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Long sequence, Object result) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeMapResult'");
    }

    @Override
    public StorageFileDto storeReduceResult(Long jobId, Long reduceId, byte[] data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeReduceResult'");
    }

    @Override
    public StorageFileDto storeReduceResult(Long jobId, Long reduceId, Object result) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeReduceResult'");
    }

    @Override
    public InputStream loadFile(Long jobId, Long offsetStart, Long offsetEnd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadFile'");
    }

    @Override
    public InputStream loadFile(Long jobId) {
        
    }

    @Override
    public InputStream loadMapResult(Long jobId, Long mapTaskId, Long sequence) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadMapResult'");
    }

    @Override
    public void delete(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    
}
