package mapreduce.app.utilities.Interfaces;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import mapreduce.app.utilities.DTOs.StorageFileDto;

public interface StorageService {
    StorageFileDto storeFile(MultipartFile file, Long jobId);
    StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Long sequence, byte[] data);
    StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Long sequence, Object result);
    StorageFileDto storeReduceResult(Long jobId, Long reduceId, byte[] data);
    StorageFileDto storeReduceResult(Long jobId, Long reduceId, Object result);

    InputStream loadFile(Long jobId, Long offsetStart, Long offsetEnd);
    InputStream loadFile(Long jobId);
    InputStream loadMapResult(Long jobId, Long mapTaskId, Long sequence);
    void delete(String key);
    
}
