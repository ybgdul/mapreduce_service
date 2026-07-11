package mapreduce.app.utilities.Exceptions;

import mapreduce.app.utilities.Interfaces.Retryable;

public class WordCountException extends RuntimeException implements Retryable{
    
    public WordCountException(String msg) { 
        super(msg);
    }
}
