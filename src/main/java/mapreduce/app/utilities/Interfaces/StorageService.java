package mapreduce.app.utilities.Interfaces;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import mapreduce.app.utilities.DTOs.StorageFileDto;

public interface StorageService {
    StorageFileDto storeFile(MultipartFile file, Long jobId);
    StorageFileDto storeMapResult(Long jobId, Long mapTaskId, byte[] data);
    StorageFileDto storeMapResult(Long jobId, Long mapTaskId, Object result);
    StorageFileDto storeReduceResult(Long jobId, Long reduceId, byte[] data);
    StorageFileDto storeReduceResult(Long jobId, Long reduceId, Object result);

    InputStream loadFile(Long jobId, Long offsetStart, Long offsetEnd);
    InputStream loadMapResults(Long jobId, Long sequenceStart, Long sequenceEnd);
    void delete(String key);
    
}
