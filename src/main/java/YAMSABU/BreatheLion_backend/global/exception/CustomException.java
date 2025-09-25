package YAMSABU.BreatheLion_backend.global.exception;

import YAMSABU.BreatheLion_backend.global.code.GlobalErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final GlobalErrorCode errorCode;

    public CustomException(GlobalErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
    public CustomException(GlobalErrorCode errorCode, String overrideMessage) {
        super(overrideMessage);
        this.errorCode = errorCode;
    }
}
