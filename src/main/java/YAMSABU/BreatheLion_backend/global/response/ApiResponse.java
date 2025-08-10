package YAMSABU.BreatheLion_backend.global.response;

import YAMSABU.BreatheLion_backend.global.code.SuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {

    private final Boolean isSuccess;
    private final String code;
    private final String message;
    private final T data;

    // 성공 시 응답
    public static <T> ApiResponse<T> onSuccess(String message, T result) {
        return new ApiResponse<>(true, SuccessCode._OK.getCode(), message, result);
    }

    // 성공 시 응답
    public static ApiResponse<?> onSuccess(String message) {
        return new ApiResponse<>(true, SuccessCode._OK.getCode(), message, Collections.emptyMap());
    }
}