package YAMSABU.BreatheLion_backend.global.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordCategory;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.global.s3.S3FileService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfExportService {
    private final S3FileService s3FileService;
    // 상담용 PDF
    public byte[] exportConsultPdf(List<Record> records) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            String fontPath = "src/main/resources/fonts/NanumGothic.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 20);
            for (Record record : records) {
                document.newPage();
                document.add(new Paragraph("상담용 기록 자료", titleFont));
                document.add(new Paragraph("제목: " + record.getTitle(), font));
                document.add(new Paragraph("카테고리: " + joinCategories(record), font));
                document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
                document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
                document.add(new Paragraph("심각도: " + record.getSeverity(), font));
                document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
                document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
                document.add(new Paragraph("발생 정황: " + record.getContent(), font));
                document.add(new Paragraph("----------------------", font));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    // 내용증명용 PDF
    public byte[] exportNoticePdf(List<Record> records, PdfNoticeRequestDTO dto) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            String fontPath = "src/main/resources/fonts/NanumGothic.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 20);
            document.add(new Paragraph("발신인(피해자) 이름: " + dto.getSenderName(), font));
            document.add(new Paragraph("발신인 주소: " + dto.getSenderAddress(), font));
            document.add(new Paragraph("수신인(가해자) 이름: " + dto.getReceiverName(), font));
            if (dto.isReceiverAddressKnown()) {
                document.add(new Paragraph("수신인 주소: " + dto.getReceiverAddress(), font));
            } else {
                document.add(new Paragraph("수신인 전화번호: " + dto.getReceiverPhone(), font));
            }
            document.add(new Paragraph("----------------------", font));
            for (Record record : records) {
                document.newPage();
                document.add(new Paragraph("내용증명", titleFont));
                document.add(new Paragraph("제목: " + record.getTitle(), font));
                document.add(new Paragraph("카테고리: " + joinCategories(record), font));
                document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
                document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
                document.add(new Paragraph("심각도: " + record.getSeverity(), font));
                document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
                document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
                document.add(new Paragraph("발생 정황: " + record.getContent(), font));
                document.add(new Paragraph("----------------------", font));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    // 전체 PDF (타임라인 내려받기)
    public byte[] exportAllPdf(List<Record> records, String drawerName) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            String fontPath = "src/main/resources/fonts/NanumGothic.ttf";
            BaseFont baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 20);
            // 서랍 이름 출력
            document.add(new Paragraph(drawerName, titleFont));
            document.add(new Paragraph(" "));
            // 가해자 한 번만 출력
            String assailantNames = records.isEmpty() ? "" : joinNamesByRole(records.get(0), PersonRole.ASSAILANT);
            document.add(new Paragraph("가해자: " + assailantNames, font));
            document.add(new Paragraph(" "));
            // 레코드 출력 (오래된 순, 날짜/제목/카테고리/사건내용)
            for (Record record : records) {
                document.add(new Paragraph("날짜: " + record.getOccurredAt(), font));
                document.add(new Paragraph("제목: " + record.getTitle(), font));
                document.add(new Paragraph("카테고리: " + joinCategories(record), font));
                document.add(new Paragraph("사건내용: " + record.getContent(), font));
                document.add(new Paragraph("----------------------", font));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    private byte[] downloadImageBytes(String urlStr) throws Exception {
        try (InputStream in = new URL(urlStr).openStream()) {
            return in.readAllBytes();
        }
    }

    private String joinCategories(Record r) {
        if (r.getCategories() == null || r.getCategories().isEmpty()) return "";
        return r.getCategories().stream()
                .map(RecordCategory::getLabel)
                .collect(Collectors.joining(", "));
    }

    private String joinNamesByRole(Record r, PersonRole role) {
        if (r.getRecordPersons() == null || r.getRecordPersons().isEmpty()) return "";
        return r.getRecordPersons().stream()
                .filter(rp -> rp.getRole() == role)
                .map(rp -> rp.getPerson().getName())
                .collect(Collectors.joining(", "));
    }
}
