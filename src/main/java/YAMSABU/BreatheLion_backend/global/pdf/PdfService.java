package YAMSABU.BreatheLion_backend.global.pdf;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;

import java.util.List;

public interface PdfService  {
    byte[] exportConsultPdf(Long recordId);
    byte[] exportNoticePdf(Long recordId, PdfNoticeRequestDTO dto);

    byte[] exportAllPdf(List<Record> records, String drawerName);
}
