package YAMSABU.BreatheLion_backend.global.pdf;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PdfServiceImpl implements PdfService {

    private final RecordRepository recordRepository;


    private static final DateTimeFormatter PDF_DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 / HH시 mm분경");

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
            Font titleFont = kFont(21f);

            // 심각도 숫자 -> 높음, 보통, 낮음 변환
            int severity = record.getSeverity();
            String severityChange;
            if(severity == 0)
                severityChange = "낮음";
            else if(severity == 1)
                severityChange = "보통";
            else severityChange = "높음";


            document.add(new LineSeparator());
            document.add(new Paragraph(" "));
            document.add(new Paragraph("상담용 기록 자료", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());
            document.add(new Paragraph(" "));


            document.add(new Paragraph("제목: " + record.getTitle(), font));
            document.add(new Paragraph("카테고리: " + joinCategory(record), font));
            document.add(new Paragraph("가해자: " + joinNamesByRole(record, PersonRole.ASSAILANT), font));
            document.add(new Paragraph("목격자: " + joinNamesByRole(record, PersonRole.WITNESS), font));
            document.add(new Paragraph("심각도: " + severityChange, font));
            document.add(new Paragraph("발생 일시: " + record.getOccurredAt().format(PDF_DATETIME_FMT), font));
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

            Font font = kFont(12f);
            Font titleFont = kFont(21f);

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
            document.add(new Paragraph("내용증명", titleFont));
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
