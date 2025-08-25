package YAMSABU.BreatheLion_backend.global.pdf;

import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.Evidence;
import YAMSABU.BreatheLion_backend.domain.evidence.dto.EvidenceDTO.EvidenceResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.text.*;
        import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import YAMSABU.BreatheLion_backend.domain.chat.service.ChatService;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageResponseDTO;
import YAMSABU.BreatheLion_backend.domain.evidence.dto.*;
        import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.EvidenceType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfServiceImpl implements PdfService {

    private final RecordRepository recordRepository;
    private final ChatService chatService;


    private static final DateTimeFormatter PDF_DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 / HH시 mm분경");

    private static final DateTimeFormatter CHAT_TS_FMT =
            DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm]");

    private String roleLabel(ChatRole role) {
        return switch (role) {
            case user -> "사용자";
            case assistant -> "도우미";
        };
    }


    @Override
    @Transactional(readOnly = true)
    public byte[] exportConsultPdf(Long recordId) {
        Record record = recordRepository.findGraphById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));
        return doExportConsultPdf(record); // 기존 PDF 생성 로직
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportNoticePdf(Long recordId, PdfNoticeRequestDTO dto) {
        Record record = recordRepository.findGraphById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));
        return doExportNoticePdf(record, dto); // 기존 PDF 생성 로직
    }


    // 상담용 PDF
    private byte[] doExportConsultPdf(Record record) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font font = kFont(12f);
            Font middleFont = kFont(16f);
            Font titleFont = kFont(21f);
            // 볼드체 추가
            Font boldFont = new Font(kFont(12f));
            boldFont.setStyle(Font.BOLD);
            Font titleboldFont = new Font(kFont(21f));
            boldFont.setStyle(Font.BOLD);

            // 심각도 숫자 -> 높음, 보통, 낮음 변환2
            int severity = record.getSeverity();
            String severityChange;
            if(severity == 0)
                severityChange = "낮음";
            else if(severity == 1)
                severityChange = "보통";
            else severityChange = "높음";


            document.add(new LineSeparator());
            document.add(new Paragraph("상담용 기록 자료", titleFont));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));


            document.add(new Paragraph("제목: " + record.getTitle(), font));
            document.add(new Paragraph("카테고리: " + joinCategory(record), font));
            document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
            document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
            document.add(new Paragraph("심각도: " + severityChange, font));
            document.add(new Paragraph("발생 일시: " + record.getOccurredAt().format(PDF_DATETIME_FMT), font));

            document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
            document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
            document.add(new Paragraph("발생 정황: " + record.getContent(), font));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // 채팅 보기 출력 (상담용 포맷 적용 + 증거 이미지는 메시지 아래에 표시)
            ChatMessageListDTO chatListDTO = chatService.getChattingList(record.getId());
            List<ChatMessageResponseDTO> chatList = (chatListDTO != null) ? chatListDTO.getMessages() : null;

            if (chatList != null && !chatList.isEmpty()) {
                document.add(new Paragraph("대화 내용", middleFont));
                document.add(new Paragraph(" "));

                for (ChatMessageResponseDTO chat : chatList) {
                    // 날짜 + 시간 그대로 출력
                    String tsStr = "[" + chat.getMessageDate() + " / " + chat.getMessageTime() + "]";

                    // 역할 라벨
                    String roleText;
                    try {
                        roleText = roleLabel(chat.getRole());
                    } catch (Exception ignore) {
                        roleText = (chat.getRole() == ChatRole.assistant) ? "AI 챗봇 " : "사용자 ";
                    }

                    // 메시지 내용
                    String msg = (chat.getContent() == null || chat.getContent().isBlank())
                            ? "(내용 없음)"
                            : chat.getContent();

                    // 한 줄로 추가
                    String line = String.format("%s - %s : %s", tsStr, roleText, msg);
                    document.add(new Paragraph(line, font));

                    // 증거 이미지 출력
                    if (chat.getEvidences() != null) {
                        for (EvidenceResponseDTO evidence : chat.getEvidences()) {
                            if (evidence.getType() == EvidenceType.IMAGE && evidence.getUrl() != null) {
                                try {
                                    Image img = Image.getInstance(evidence.getUrl());
                                    img.scaleToFit(170, 170); // 6cm x 6cm
                                    img.setSpacingBefore(4f);
                                    img.setSpacingAfter(4f);
                                    document.add(img);
                                } catch (Exception ex) {
                                    document.add(new Paragraph("이미지 첨부 오류", font));
                                }
                            }
                        }
                    }

                    // 메시지 간 간격
                    document.add(new Paragraph(" "));
                }
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    // 내용증명용 PDF
    private byte[] doExportNoticePdf(Record record, PdfNoticeRequestDTO dto) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font font = kFont(12f);
            Font middleFont = kFont(17f);
            Font titleFont = kFont(21f);

            document.add(new LineSeparator());
            document.add(new Paragraph("내용 증명", titleFont));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));

            // 심각도 숫자 -> 높음, 보통, 낮음 변환
            int severity = record.getSeverity();
            String severityChange;
            if(severity == 0)
                severityChange = "낮음";
            else if(severity == 1)
                severityChange = "보통";
            else severityChange = "높음";

            document.add(new Paragraph("발신인(피해자) 이름: " + dto.getSenderName(), font));
            document.add(new Paragraph("수신인(가해자) 이름: " + dto.getReceiverName(), font));

            if (dto.getSenderAddress() == null || dto.getReceiverAddress() == null) {
                document.add(new Paragraph("발신인 전화번호: " + dto.getSenderPhone(), font));
                document.add(new Paragraph("수신인 전화번호: " + dto.getReceiverPhone(), font));
            } else {
                document.add(new Paragraph("발신인 주소: " + dto.getSenderAddress(), font));
                document.add(new Paragraph("수신인 주소: " + dto.getReceiverAddress(), font));
            }
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));


            document.add(new Paragraph("제목: " + record.getTitle(),font));
            document.add(new Paragraph("카테고리: " + joinCategory(record), font));
            document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
            document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
            document.add(new Paragraph("심각도: " + severityChange, font));
            document.add(new Paragraph("발생 일시: " + record.getOccurredAt().format(PDF_DATETIME_FMT), font));

            document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
            document.add(new Paragraph("발생 정황: " + record.getContent(), font));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // 채팅 보기 출력 (상담용 포맷 적용 + 증거 이미지는 메시지 아래에 표시)
            ChatMessageListDTO chatListDTO = chatService.getChattingList(record.getId());
            List<ChatMessageResponseDTO> chatList = (chatListDTO != null) ? chatListDTO.getMessages() : null;

            if (chatList != null && !chatList.isEmpty()) {
                document.add(new Paragraph("대화 내용", middleFont));
                document.add(new Paragraph(" "));

                for (ChatMessageResponseDTO chat : chatList) {
                    // 날짜 + 시간 그대로 출력
                    String tsStr = "[" + chat.getMessageDate() + " / " + chat.getMessageTime() + "]";

                    // 역할 라벨
                    String roleText;
                    try {
                        roleText = roleLabel(chat.getRole());
                    } catch (Exception ignore) {
                        roleText = (chat.getRole() == ChatRole.assistant) ? "AI 챗봇 " : "사용자 ";
                    }

                    // 메시지 내용
                    String msg = (chat.getContent() == null || chat.getContent().isBlank())
                            ? "(내용 없음)"
                            : chat.getContent();

                    // 한 줄로 추가
                    String line = String.format("%s - %s : %s", tsStr, roleText, msg);
                    document.add(new Paragraph(line, font));

                    // 증거 이미지 출력
                    if (chat.getEvidences() != null) {
                        for (EvidenceResponseDTO evidence : chat.getEvidences()) {
                            if (evidence.getType() == EvidenceType.IMAGE && evidence.getUrl() != null) {
                                try {
                                    Image img = Image.getInstance(evidence.getUrl());
                                    img.scaleToFit(170, 170); // 6cm x 6cm
                                    img.setSpacingBefore(4f);
                                    img.setSpacingAfter(4f);
                                    document.add(img);
                                } catch (Exception ex) {
                                    document.add(new Paragraph("이미지 첨부 오류", font));
                                }
                            }
                        }
                    }

                    // 메시지 간 간격
                    document.add(new Paragraph(" "));
                }
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    // 전체 PDF (타임라인 내려받기)
    @Override
    @Transactional(readOnly = true)
    public byte[] exportAllPdf(List<Record> records, String drawerName) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font font = kFont(12f);
            Font titleFont = kFont(21f);

            // 서랍 이름 출력
            document.add(new Paragraph(drawerName, titleFont));
            document.add(new Paragraph(" "));

            // 가해자 한 번만 출력
            String assailantNames = records.isEmpty() ? "" : joinNamesByRole(records.get(0), PersonRole.ASSAILANT);
            document.add(new Paragraph("가해자: " + assailantNames, font));
            document.add(new Paragraph(" "));

            document.add(new LineSeparator());
            // 레코드 출력 (오래된 순, 날짜/제목/카테고리/사건내용)
            for (Record record : records) {
                int severity = record.getSeverity();
                String severityChange;
                if(severity == 0)
                    severityChange = "낮음";
                else if(severity == 1)
                    severityChange = "보통";
                else
                    severityChange = "높음";

                document.add(new Paragraph(" "));
                document.add(new Paragraph("날짜: " + record.getOccurredAt(), font));
                document.add(new Paragraph("제목: " + record.getTitle(), font));
                document.add(new Paragraph("카테고리: " + joinCategory(record), font));
                document.add(new Paragraph("심각도: " + severityChange, font));
                document.add(new Paragraph("사건내용: " + record.getContent(), font));
                document.add(new Paragraph(" "));
                document.add(new LineSeparator());
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    private BaseFont getBaseFont() {
        try (InputStream in = getClass().getResourceAsStream("/fonts/NanumGothic.ttf")) {
            if (in == null) throw new IllegalStateException("NanumGothic.ttf not found in /fonts");
            Path tmp = Files.createTempFile("NanumGothic", ".ttf");
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            return BaseFont.createFont(tmp.toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("폰트 로드 실패", e);
        }
    }

    /** 지정 크기의 한글 폰트 생성 */
    private Font kFont(float size) {
        return new Font(getBaseFont(), size);
    }


    private String joinCategory(Record r) {
        if (r.getCategory() == null) return "";
        return r.getCategory().getLabel();
    }

    private String joinNamesByRole(Record r, PersonRole role) {
        if (r.getRecordPersons() == null || r.getRecordPersons().isEmpty()) return "";
        return r.getRecordPersons().stream()
                .filter(rp -> rp.getRole() == role)
                .map(rp -> rp.getPerson().getName())
                .collect(Collectors.joining(", "));
    }
}
