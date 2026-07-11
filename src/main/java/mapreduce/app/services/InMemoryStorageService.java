package mapreduce.app.services;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import mapreduce.app.utilities.DTOs.StorageFileDto;
import mapreduce.app.utilities.Interfaces.StorageService;

@Service
public class InMemoryStorageService implements StorageService {

    @Override
    public String store(MultipartFile file) {
        
    }

    @Override
    public InputStream load(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }

    @Override
    public void delete(String key) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public StorageFileDto storeFile(MultipartFile file, Long jobId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeFile'");
    }

    @Override
    public StorageFileDto storeMapResult(Long jobId, Long mapTaskId, byte[] data) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeMapResult'");
    }

    @Override
    public StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Object result) {
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
    public InputStream loadMapResults(Long jobId, Long sequenceStart, Long sequenceEnd) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'loadMapResults'");
    }
    
}
