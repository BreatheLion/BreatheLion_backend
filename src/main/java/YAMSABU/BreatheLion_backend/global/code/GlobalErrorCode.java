package YAMSABU.BreatheLion_backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode {

    // 400 Bad Request (요청 자체가 잘못됨)
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON401", "잘못된 파라미터입니다."),

    // 404 Not Found
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "리소스를 찾을 수 없습니다."),
    _URL_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404-1", "존재하지 않는 URL입니다."),

    // User Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "사용자를 찾을 수 없습니다."),

    // Chat Error
    CHAT_MESSAGE_EMPTY(HttpStatus.BAD_REQUEST, "CHAT400", "메시지는 비어있을 수 없습니다."),
    CHAT_EMPTY(HttpStatus.BAD_REQUEST, "CHAT400-1", "요약할 대화가 없습니다."),

    // Record Error
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD404", "기록을 찾을 수 없습니다."),
    RECORD_CREATE_FAILED(HttpStatus.BAD_REQUEST, "RECORD400", "기록 생성에 실패했습니다."),

    // Drawer Error
    DRAWER_NOT_FOUND(HttpStatus.NOT_FOUND, "DRAWER404", "서랍을 찾을 수 없습니다."),
    DRAWER_CREATE_FAILED(HttpStatus.BAD_REQUEST, "DRAWER400", "서랍 생성에 실패했습니다."),
    DRAWER_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "DRAWER400-1", "서랍 수정에 실패했습니다."),
    DRAWER_DELETE_FAILED(HttpStatus.BAD_REQUEST, "DRAWER400-2", "서랍 삭제에 실패했습니다."),
    DRAWER_INVALID_NAME(HttpStatus.BAD_REQUEST, "DRAWER400-3", "서랍 이름의 형식이 올바르지 않습니다."),
    DRAWER_ALREADY_EXISTS(HttpStatus.CONFLICT, "DRAWER409", "이미 존재하는 서랍 이름입니다."),

    // Evidence Error
    EVIDENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "EVIDENCE404", "증거를 찾을 수 없습니다."),
    EVIDENCE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "EVIDENCE400", "증거 업로드에 실패했습니다."),

    // AI Error
    AI_SERVICE_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "AI502", "AI 응답 생성에 실패했습니다."),

    // 500 Internal Server Error
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}