package YAMSABU.BreatheLion_backend.global.handler;


import YAMSABU.BreatheLion_backend.global.code.GlobalErrorCode;
import YAMSABU.BreatheLion_backend.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RequiredArgsConstructor
@RestController
public class CustomErrorController implements ErrorController {

    //발생한 에러의 상세정보를 꺼낼 수 있는 컴포넌트
    private final ErrorAttributes errorAttributes;

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handle(HttpServletRequest request) {
        var webRequest = new ServletWebRequest(request);
        var attrs = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.defaults()
        );
        int status = (int) attrs.getOrDefault("status", 500);

        if (status == 404) {
            var ec = GlobalErrorCode._URL_NOT_FOUND;
            return ResponseEntity.status(ec.getHttpStatus())
                    .body(new ErrorResponse(ec.getCode(), ec.getMessage()));
        }
        var ec = GlobalErrorCode._INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(new ErrorResponse(ec.getCode(), ec.getMessage()));
    }
}