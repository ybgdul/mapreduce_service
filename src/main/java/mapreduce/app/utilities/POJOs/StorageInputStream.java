package mapreduce.app.utilities.POJOs;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import mapreduce.app.utilities.Exceptions.StorageConnectionException;

public class StorageInputStream extends FilterInputStream {
    
    public StorageInputStream(InputStream input) { 
        super(input);
    }

    @Override
    public int read() throws IOException { 
        try {
            return super.read();
        } catch(IOException e) { 
            throw new StorageConnectionException("Storage connection failed: " + e.toString());
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException { 
        try { 
            return super.read(b, off, len);
        } catch(IOException e) { 
            throw new StorageConnectionException("Storage connection failed: " + e.toString());
        }
    }
    
}
