package mapreduce.app.utilities.Interfaces;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import mapreduce.app.utilities.DTOs.StorageFileDto;

public interface StorageService {
    StorageFileDto store(MultipartFile file);
    InputStream load(String key);
    void delete(String key);
}
