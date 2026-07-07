package mapreduce.app.utilities.Exceptions;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlers {
    
    @ExceptionHandler(CustomAuthException.class)
    public ResponseEntity<?> handleCustomAuthException(CustomAuthException ex) { 

        return ResponseEntity.status(ex.getStatus()).body(Map.of(
            "status", ex.getStatus().value(),
            "error", ex.getStatus().getReasonPhrase(),
            "message", ex.getMessage()
        ));
    }
}
