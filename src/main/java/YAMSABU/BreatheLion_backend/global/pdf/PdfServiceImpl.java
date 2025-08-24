package YAMSABU.BreatheLion_backend.global.pdf;

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
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfServiceImpl implements PdfService {

    private final RecordRepository recordRepository;
    private final ChatService chatService;

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
            BaseFont baseFont = getSafeFont();
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 21);
            Font redFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.RED);

            // 심각도 숫자 -> 높음, 보통, 낮음 변환
            int severity = record.getSeverity();
            String severityChange;
            if(severity == 0)
                severityChange = "낮음";
            else if(severity == 1)
                severityChange = "보통";
            else severityChange = "높음";


            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));
            document.add(new Paragraph("상담용 기록 자료", titleFont));
            document.add(new Paragraph("제목: " + record.getTitle(), font));
            document.add(new Paragraph("카테고리: " + joinCategory(record), font));
            document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
            document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
            if (severity == 2) {
                Paragraph p = new Paragraph("심각도: ", font);
                p.add(new Chunk(severityChange, redFont));
                document.add(p);
            } else {
                document.add(new Paragraph("심각도: " + severityChange, font));
            }
            document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
            document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
            document.add(new Paragraph("발생 정황: " + record.getContent(), font));
            document.add(new Paragraph(" "));

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
            BaseFont baseFont = getSafeFont();
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 21);
            Font redFont = new Font(baseFont, 12, Font.NORMAL, BaseColor.RED);

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
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));
            document.add(new Paragraph("내용증명", titleFont));
            document.add(new Paragraph("제목: " + record.getTitle(),font));
            document.add(new Paragraph("카테고리: " + joinCategory(record), font));
            document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
            document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
            if (severity == 2) {
                Paragraph p = new Paragraph("심각도: ", font);
                p.add(new Chunk(severityChange, redFont));
                document.add(p);
            } else {
                document.add(new Paragraph("심각도: " + severityChange, font));
            }
            document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
            document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
            document.add(new Paragraph("발생 정황: " + record.getContent(), font));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // 채팅 보기 출력
            ChatMessageListDTO chatListDTO = chatService.getChattingList(record.getId());
            List<ChatMessageResponseDTO> chatList = chatListDTO.getMessages();
            if (chatList != null && !chatList.isEmpty()) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("채팅 내역", titleFont));
                for (ChatMessageResponseDTO chat : chatList) {
                    String prefix = chat.getRole() == ChatRole.assistant ? "챗봇 : " : "사용자 : ";
                    document.add(new Paragraph(prefix, font));
                    document.add(new Paragraph(new Phrase(chat.getContent(), font)));
                    // 이미지 첨부파일 출력
                    if (chat.getEvidences() != null) {
                        for (EvidenceResponseDTO evidence : chat.getEvidences()) {
                            if (evidence.getType() == EvidenceType.IMAGE) {
                                try {
                                    Image img = Image.getInstance(evidence.getUrl());
                                    img.scaleToFit(170, 170); // 6cm x 6cm
                                    document.add(img);
                                } catch (Exception ex) {
                                    document.add(new Paragraph("이미지 첨부 오류", font));
                                }
                            }
                        }
                    }
                    // 줄바꿈
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
    @Transactional
    public byte[] exportAllPdf(List<Record> records, String drawerName) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            BaseFont baseFont = getSafeFont();
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 21);

            // 서랍 이름 출력
            document.add(new Paragraph(drawerName, titleFont));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // 가해자 한 번만 출력
            String assailantNames = records.isEmpty() ? "" : joinNamesByRole(records.get(0), PersonRole.ASSAILANT);
            document.add(new Paragraph("가해자: " + assailantNames, font));
            document.add(new Paragraph(" "));

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

    private BaseFont getSafeFont() throws Exception {
        // 1) classpath에서 바로 바이트로 읽기
        try (InputStream is = getClass().getResourceAsStream("/fonts/NanumGothic.ttf")) {
            if (is == null) {
                throw new IllegalStateException("폰트 파일을 찾을 수 없습니다: classpath:/fonts/NanumGothic.ttf");
            }
            byte[] ttf = is.readAllBytes();

            // 2) 바이트 배열로 직접 생성 (파일경로 X)
            //    IDENTITY_H = 유니코드 세로쓰기 아님(가로쓰기) / 한글 출력용
            return BaseFont.createFont(
                    "NanumGothic",            // 내부 식별명(아무 문자열 가능)
                    BaseFont.IDENTITY_H,      // 유니코드 인코딩
                    BaseFont.EMBEDDED,        // 폰트 임베딩
                    true,                     // cached
                    ttf,                      // ttf bytes
                    null                      // pfb (Type1용, TTF면 null)
            );
        }
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
