package YAMSABU.BreatheLion_backend.global.pdf;

import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.Evidence;
import YAMSABU.BreatheLion_backend.domain.evidence.dto.EvidenceDTO.EvidenceResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk.ChunkChoice;
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

import javax.print.Doc;
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
            Font titleBoldFont = kFont(21f, Font.BOLD);

            // 심각도 숫자 -> 높음, 보통, 낮음 변환2
            int severity = record.getSeverity();
            String severityChange;
            if(severity == 0)
                severityChange = "낮음";
            else if(severity == 1)
                severityChange = "보통";
            else severityChange = "높음";

            //document.add(new Paragraph("상담용 기록 자료", titleBoldFont));
            // 수정
            Paragraph title = new Paragraph("상담용 기록 자료", titleBoldFont);
            title.setAlignment(Element.ALIGN_CENTER);  // 가운데 정렬
            document.add(title);
            // 여기까지 수정
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

// 여기서부터 수정 들어감
            addLabelAndValue(document, "제목", record.getTitle());
            addLabelAndValue(document, "카테고리",joinCategory(record));
            addLabelAndValue(document, "가해자", joinNamesByRole(record, PersonRole.ASSAILANT));
            addLabelAndValue(document, "목격자", joinNamesByRole(record, PersonRole.WITNESS));
            addLabelAndValue(document, "심각도", severityChange);
            addLabelAndValue(document, "발생 일시", record.getOccurredAt().format(PDF_DATETIME_FMT));
            addLabelAndValue(document, "발생 장소", record.getLocation());
            addLabelAndValue(document, "발생 정황", record.getContent());
            document.add(new Paragraph(" "));


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

                    // 여기서부터
                    Paragraph chatLine = new Paragraph();

                    chatLine.add(new Chunk(tsStr , kFont(12f, Font.NORMAL)));     // 시간 Normal
                    chatLine.add(new Chunk(roleText + "  ", kFont(12f, Font.BOLD)));    // 사용자 / AI 챗봇 라벨 Bold
                    chatLine.add(new Chunk(msg, kFont(12f, Font.NORMAL)));              // 메시지 Normal

                    document.add(chatLine);

                    //여기까지 수정
                    // 한 줄로 추가
//                    String line = String.format("%s  %s  %s", tsStr, roleText, msg);
//                    document.add(new Paragraph(line, font));

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
            Font boldFont = kFont(12f, Font.BOLD);
            Font titleBoldFont = kFont(21f, Font.BOLD);

            //document.add(new Paragraph("내용 증명", titleBoldFont));
            // 여기부터 수정
            Paragraph title = new Paragraph("내용 증명", titleBoldFont);
            title.setAlignment(Element.ALIGN_CENTER);  // 가운데 정렬
            document.add(title);

            // 여기까지 수정
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // 심각도 숫자 -> 높음, 보통, 낮음 변환
            int severity = record.getSeverity();
            String severityChange;
            if(severity == 0)
                severityChange = "낮음";
            else if(severity == 1)
                severityChange = "보통";
            else severityChange = "높음";

            // 여기서부터 수정 들어감
            addLabelAndValue(document, "발신인(피해자) 이름", dto.getSenderName());
            addLabelAndValue(document, "수신인(가해자) 이름", dto.getReceiverName());
            if (dto.getSenderAddress() == null || dto.getReceiverAddress() == null) {
                addLabelAndValue(document, "발신인(피해자) 전화번호", dto.getSenderPhone());
                addLabelAndValue(document, "수신인(가해자) 전화번호", dto.getReceiverPhone());
            } else {
                addLabelAndValue(document, "발신인(피해자) 주소", dto.getSenderAddress());
                addLabelAndValue(document, "수신인(가해자) 주소", dto.getReceiverAddress());
            }
            document.add(new Paragraph(" "));

            // 여기까지
//            document.add(new Paragraph("발신인(피해자) 이름: " + dto.getSenderName(), font));
//            document.add(new Paragraph("수신인(가해자) 이름: " + dto.getReceiverName(), font));
//
//            if (dto.getSenderAddress() == null || dto.getReceiverAddress() == null) {
//                document.add(new Paragraph("발신인 전화번호: " + dto.getSenderPhone(), font));
//                document.add(new Paragraph("수신인 전화번호: " + dto.getReceiverPhone(), font));
//            } else {
//                document.add(new Paragraph("발신인 주소: " + dto.getSenderAddress(), font));
//                document.add(new Paragraph("수신인 주소: " + dto.getReceiverAddress(), font));
//            }
//            document.add(new Paragraph(" "));


            // 여기서부터 수정 들어감
            addLabelAndValue(document, "제목", record.getTitle());
            addLabelAndValue(document, "카테고리",joinCategory(record));
            addLabelAndValue(document, "가해자", joinNamesByRole(record, PersonRole.ASSAILANT));
            addLabelAndValue(document, "목격자", joinNamesByRole(record, PersonRole.WITNESS));
            addLabelAndValue(document, "심각도", severityChange);
            addLabelAndValue(document, "발생 일시", record.getOccurredAt().format(PDF_DATETIME_FMT));
            addLabelAndValue(document, "발생 장소", record.getLocation());
            addLabelAndValue(document, "발생 정황", record.getContent());
            document.add(new Paragraph(" "));

// 여기까지 수정

//            document.add(new Paragraph("제목: " + record.getTitle(),font));
//            document.add(new Paragraph("카테고리: " + joinCategory(record), font));
//            document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
//            document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
//            document.add(new Paragraph("심각도: " + severityChange, font));
//            document.add(new Paragraph("발생 일시: " + record.getOccurredAt().format(PDF_DATETIME_FMT), font));
//
//            document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
//            document.add(new Paragraph("발생 정황: " + record.getContent(), font));
//            document.add(new Paragraph(" "));


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
                    // 여기서부터
                    Paragraph chatLine = new Paragraph();

                    chatLine.add(new Chunk(tsStr , kFont(12f, Font.NORMAL)));     // 시간 Normal
                    chatLine.add(new Chunk(roleText + "  ", kFont(12f, Font.BOLD)));    // 사용자 / AI 챗봇 라벨 Bold
                    chatLine.add(new Chunk(msg, kFont(12f, Font.NORMAL)));              // 메시지 Normal

                    document.add(chatLine);

                    // 여기까지 수정
//                    String line = String.format("%s  %s  %s", tsStr, roleText, msg);
//                    document.add(new Paragraph(line, font));

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
            //Font titleFont = kFont(21f);
            Font titleBoldFont = kFont(21f, Font.BOLD);

            // 서랍 이름 출력
            // document.add(new Paragraph(drawerName, titleBoldFont));
            // 여기부터 수정
            Paragraph drawerTitle = new Paragraph(drawerName, titleBoldFont);
            drawerTitle.setAlignment(Element.ALIGN_CENTER);  // 가운데 정렬
            document.add(drawerTitle);
            // 여기까지 수정
            document.add(new Paragraph(" "));

            // 가해자 한 번만 출력
            String assailantNames = records.isEmpty() ? "" : joinNamesByRole(records.get(0), PersonRole.ASSAILANT);
            //document.add(new Paragraph("가해자: " + assailantNames, font));
            // 한줄 수정
            addLabelAndValue(document, "가해자", assailantNames);
            // 여기까지
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
                // 여기부터 수정
                addLabelAndValue(document, "날짜", record.getOccurredAt().format(PDF_DATETIME_FMT));
                addLabelAndValue(document, "제목", record.getTitle());
                addLabelAndValue(document, "카테고리", joinCategory(record));
                addLabelAndValue(document, "심각도", severityChange);
                addLabelAndValue(document, "사건내용", record.getContent());
                // 여기까지 수정
//                document.add(new Paragraph("날짜: " + record.getOccurredAt(), font));
//                document.add(new Paragraph("제목: " + record.getTitle(), font));
//                document.add(new Paragraph("카테고리: " + joinCategory(record), font));
//                document.add(new Paragraph("심각도: " + severityChange, font));
//                document.add(new Paragraph("사건내용: " + record.getContent(), font));
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

    // 볼드용
    private Font kFont(float size, int style) {
        return new Font(getBaseFont(), size, style);
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

    private void addLabelAndValue(Document document, String label, String value) throws DocumentException {
        Font boldFont = kFont(12f, Font.BOLD);
        Font normalFont = kFont(12f, Font.NORMAL);

        Phrase phrase = new Phrase();
        phrase.add(new Chunk(label + " ", boldFont));
        phrase.add(new Chunk(value != null ? value : "", normalFont));

        document.add(new Paragraph(phrase));
    }
}
