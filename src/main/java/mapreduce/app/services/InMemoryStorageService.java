package mapreduce.app.services;

import java.io.InputStream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    
}
