package YAMSABU.BreatheLion_backend.domain.person.controller;

import YAMSABU.BreatheLion_backend.global.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import YAMSABU.BreatheLion_backend.domain.person.dto.EvidenceDTO.EvidencePresignedUrlRequestDTO;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/evidence")
public class EvidenceController {
    private final S3FileService s3FileService;

    // presigned url 발급 (파일 업로드용, POST, 서버에서 UUID 파일명 생성)
    @PostMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@RequestBody EvidencePresignedUrlRequestDTO request) {
        String ct = request.getContentType();
        String ext = ct != null && ct.contains("/") ? ct.substring(ct.lastIndexOf('/') + 1) : "bin";
        S3FileService.PresignedUrlResponse result =
                s3FileService.getPutPreSignedUrl(request.getPrefix(), ct, request.getContentLength(), ext);
        return ResponseEntity.ok(Map.of("url", result.presignedUrl, "s3Key", result.s3Key));
    }

    // Presigned GET URL 발급 (조회용)
    @GetMapping("/presigned-url/read")
    public ResponseEntity<Map<String, String>> getReadUrl(@RequestParam String s3Key,
                                                          @RequestParam(defaultValue = "10") int minutes) {
        String url = s3FileService.getGetPreSignedUrlByKey(s3Key, minutes);
        return ResponseEntity.ok(Map.of("url", url));
    }

    // key로 S3 파일 삭제
    @PostMapping("/delete-by-key")
    public ResponseEntity<String> deleteEvidenceByKey(@RequestParam String s3Key) {
        s3FileService.deleteByKey(s3Key);
        return ResponseEntity.ok("삭제 완료");
    }
}
