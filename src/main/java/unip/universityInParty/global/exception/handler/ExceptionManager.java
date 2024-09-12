package unip.universityInParty.global.exception.handler;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import unip.universityInParty.global.baseResponse.ResponseDto;
import unip.universityInParty.global.exception.custom.CustomException;
import unip.universityInParty.global.exception.errorCode.ErrorCode;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ResponseDto<?>> appExceptionHandler(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        ResponseDto<?> response = ResponseDto.fail(errorCode.getHttpStatus().value(), errorCode.getMessage() + " " + e.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDto<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ResponseDto<Map<String, String>> response = ResponseDto.fail(
            422,
            "유효성 검사에 실패했습니다.",
            errors
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseBody
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("JWT Token has expired");
    }
}