package br.com.fiap.clientes.api.exceptionhandler;

import br.com.fiap.clientes.domain.exception.ClienteNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class CustomExceptionHandler {

    private final ErrorMessage errorMessage = new ErrorMessage();

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ErrorMessage> clienteNaoEncontrado(ClienteNaoEncontradoException e, HttpServletRequest request){
        var status = HttpStatus.NOT_FOUND;

        errorMessage.setTimestamp(LocalDateTime.now());
        errorMessage.setStatus(status.value());
        errorMessage.setMessage(e.getMessage());
        errorMessage.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(this.errorMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> illegalArgument(IllegalArgumentException e, HttpServletRequest request){
        var status = HttpStatus.BAD_REQUEST;

        errorMessage.setTimestamp(LocalDateTime.now());
        errorMessage.setStatus(status.value());
        errorMessage.setMessage(e.getMessage());
        errorMessage.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(this.errorMessage);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ErrorMessage> dateTimeParseException(DateTimeParseException e, HttpServletRequest request){
        var status = HttpStatus.BAD_REQUEST;

        errorMessage.setTimestamp(LocalDateTime.now());
        errorMessage.setStatus(status.value());
        errorMessage.setMessage(e.getMessage());
        errorMessage.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(this.errorMessage);
    }

}
