package mapreduce.app.utilities.Exceptions;

public class StorageConnectionException extends RuntimeException {
    public StorageConnectionException(String msg) { 
        super(msg);
    }
}
