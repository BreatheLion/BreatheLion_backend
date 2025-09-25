package YAMSABU.BreatheLion_backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {
    _OK(HttpStatus.OK, "200", "OK");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
