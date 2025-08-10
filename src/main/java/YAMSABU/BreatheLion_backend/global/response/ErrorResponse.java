package YAMSABU.BreatheLion_backend.global.response;

import YAMSABU.BreatheLion_backend.global.code.GlobalErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final Boolean isSuccess;
    private final String code;
    private final String message;

    public ErrorResponse(GlobalErrorCode globalErrorCode) {
        this.isSuccess = false;
        this.code = globalErrorCode.getCode();
        this.message = globalErrorCode.getMessage();
    }

    public ErrorResponse(String code, String message) {
        this.isSuccess = false;
        this.code = code;
        this.message = message;
    }
}