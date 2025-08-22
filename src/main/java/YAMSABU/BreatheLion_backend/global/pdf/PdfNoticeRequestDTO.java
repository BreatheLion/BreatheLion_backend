package YAMSABU.BreatheLion_backend.global.pdf;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PdfNoticeRequestDTO {
    private String senderName; // 발신인(피해자) 이름
    private String senderAddress; // 발신인 주소 (주소 모를 경우 null)
    private String senderPhone; // 발신인 전화번호 (주소 모를 경우)
    private String receiverName; // 수신인(가해자) 이름
    private String receiverAddress; // 수신인 주소 (주소 모를 경우 null)
    private String receiverPhone; // 수신인 전화번호 (주소 모를 경우)
    private boolean receiverAddressKnown; // true: 주소, false: 전화번호
}
