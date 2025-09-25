package YAMSABU.BreatheLion_backend.global.handler;

import YAMSABU.BreatheLion_backend.global.code.GlobalErrorCode;
import YAMSABU.BreatheLion_backend.global.exception.CustomException;
import YAMSABU.BreatheLion_backend.global.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.BindException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 커스텀 에러 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(CustomException ex) {
        var ec = ex.getErrorCode();
        log.warn("[CustomException] {} : {}", ec.getCode(), ex.getMessage());
        return ResponseEntity.status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ex.getMessage()));
    }

    // 요청 값 검증(@Valid / @Validated) 에러 처리
    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<ErrorResponse> handleValidation(Exception ex) {
        var ec = GlobalErrorCode._INVALID_PARAMETER;
        String msg = "요청 값이 올바르지 않습니다.";
        if (ex instanceof MethodArgumentNotValidException m && m.getBindingResult().hasErrors()) {
            var fe = m.getBindingResult().getFieldErrors().get(0);
            msg = "[" + fe.getField() + "] " + fe.getDefaultMessage();
        } else if (ex instanceof BindException b && b.getBindingResult().hasErrors()) {
            var fe = b.getBindingResult().getFieldErrors().get(0);
            msg = "[" + fe.getField() + "] " + fe.getDefaultMessage();
        }
        return ResponseEntity.status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), msg));
    }

    // [필수 파라미터가 빠졌을 때, 파라미터 타입 불일치, 요청 JSON 파싱 실패] 에러 처리
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        var ec = GlobalErrorCode._BAD_REQUEST;
        return ResponseEntity.status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getMessage()));
    }

    // 예상치 못한 오류, 나머지 오류들 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleEtc(Exception ex) {
        var ec = GlobalErrorCode._INTERNAL_SERVER_ERROR;
        log.error("[Unhandled] {}", ex.getMessage(), ex);
        return ResponseEntity.status(ec.getHttpStatus())
                .body(new ErrorResponse(ec.getCode(), ec.getMessage()));
    }
}
