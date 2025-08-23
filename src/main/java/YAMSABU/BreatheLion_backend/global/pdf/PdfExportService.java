package YAMSABU.BreatheLion_backend.global.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordCategory;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfExportService {
    // 상담용 PDF
    public byte[] exportConsultPdf(List<Record> records) {
        try {
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            BaseFont baseFont = getSafeFont();
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 20);
            boolean first = true;
            for (Record record : records) {
                if (!first) document.newPage();
                first = false;
                document.add(new Paragraph("상담용 기록 자료", titleFont));
                document.add(new Paragraph("제목: " + record.getTitle(), font));
                document.add(new Paragraph("카테고리: " + joinCategory(record), font));
                document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
                document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
                document.add(new Paragraph("심각도: " + record.getSeverity(), font));
                document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
                document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
                document.add(new Paragraph("발생 정황: " + record.getContent(), font));
                document.add(new Paragraph("----------------------------------------------------------------------------------------", font));
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
            if (dto.isReceiverAddressKnown()) {
                if (dto.getSenderAddress() == null || dto.getReceiverAddress() == null) {
                    throw new IllegalArgumentException("주소를 아는 경우, 발신인/수신인 주소는 필수입니다.");
                }
            } else {
                if (dto.getSenderPhone() == null || dto.getReceiverPhone() == null) {
                    throw new IllegalArgumentException("주소를 모르는 경우, 발신인/수신인 전화번호는 필수입니다.");
                }
            }
            Document document = new Document();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();
            BaseFont baseFont = getSafeFont();
            Font font = new Font(baseFont, 12);
            Font titleFont = new Font(baseFont, 20);
            document.add(new Paragraph("발신인(피해자) 이름: " + dto.getSenderName(), font));
            if (dto.isReceiverAddressKnown()) {
                document.add(new Paragraph("발신인 주소: " + dto.getSenderAddress(), font));
                document.add(new Paragraph("수신인(가해자) 이름: " + dto.getReceiverName(), font));
                document.add(new Paragraph("수신인 주소: " + dto.getReceiverAddress(), font));
            } else {
                document.add(new Paragraph("발신인 전화번호: " + dto.getSenderPhone(), font));
                document.add(new Paragraph("수신인(가해자) 이름: " + dto.getReceiverName(), font));
                document.add(new Paragraph("수신인 전화번호: " + dto.getReceiverPhone(), font));
            }
            document.add(new Paragraph("----------------------------------------------------------------------------------------", font));
            boolean first = true;
            for (Record record : records) {
                if (!first) document.newPage();
                first = false;
                document.add(new Paragraph("내용증명", titleFont));
                document.add(new Paragraph("제목: " + record.getTitle(), font));
                document.add(new Paragraph("카테고리: " + joinCategory(record), font));
                document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
                document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
                document.add(new Paragraph("심각도: " + record.getSeverity(), font));
                document.add(new Paragraph("발생 일시: " + record.getOccurredAt(), font));
                document.add(new Paragraph("발생 장소: " + record.getLocation(), font));
                document.add(new Paragraph("발생 정황: " + record.getContent(), font));
                document.add(new Paragraph("----------------------------------------------------------------------------------------", font));
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
            BaseFont baseFont = getSafeFont();
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
                document.add(new Paragraph("카테고리: " + joinCategory(record), font));
                document.add(new Paragraph("사건내용: " + record.getContent(), font));
                document.add(new Paragraph("----------------------------------------------------------------------------------------", font));
            }
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF 생성 실패", e);
        }
    }

    private BaseFont getSafeFont() throws Exception {
        ClassPathResource fontResource = new ClassPathResource("fonts/NanumGothic.ttf");
        //0823수정. 에러 메세지 출력용
        if(!fontResource.exists()) {
            throw new IllegalStateException("폰트 파일을 찾을 수 없습니다: classpath:/fonts/NanumGothic.ttf");
        }
        File tempFont = File.createTempFile("NanumGothic", ".ttf");
        try (InputStream is = fontResource.getInputStream(); FileOutputStream fos = new FileOutputStream(tempFont)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }
        return BaseFont.createFont(tempFont.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
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
