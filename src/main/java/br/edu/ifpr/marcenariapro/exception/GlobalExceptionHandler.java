
package br.edu.ifpr.marcenariapro.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErrosValidacao(
            MethodArgumentNotValidException ex) {

        Map<String, Object> resposta = new HashMap<>();
        Map<String, String> erros = new HashMap<>();

        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }

        resposta.put("message", "Existem campos inválidos. Verifique os dados informados.");
        resposta.put("erros", erros);

        return ResponseEntity.badRequest().body(resposta);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarErroHttp(
            ResponseStatusException ex) {

        Map<String, String> resposta = new HashMap<>();

        resposta.put(
                "message",
                ex.getReason() != null
                        ? ex.getReason()
                        : "Não foi possível realizar a operação."
        );

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(resposta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> tratarErroInterno(
            Exception ex) {

        log.error("Erro interno na aplicação", ex);

        Map<String, String> resposta = new HashMap<>();

        resposta.put(
                "message",
                "Ocorreu um erro inesperado. Tente novamente ou contate o suporte."
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(resposta);
    }
}
