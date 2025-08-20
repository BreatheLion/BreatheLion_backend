package YAMSABU.BreatheLion_backend.domain.record.entity;

import java.text.Normalizer;
import java.util.Arrays;

public enum RecordDistrict {
    GANGNAM("강남구"),
    GANGDONG("강동구"),
    GANGBUK("강북구"),
    GANGSEO("강서구"),
    GWANAK("관악구"),
    GWANGJIN("광진구"),
    GURO("구로구"),
    GEUMCHEON("금천구"),
    NOWON("노원구"),
    DOBONG("도봉구"),
    DONGDAEMUN("동대문구"),
    DONGJAK("동작구"),
    MAPO("마포구"),
    SEODAEMUN("서대문구"),
    SEOCHO("서초구"),
    SEONGDONG("성동구"),
    SEONGBUK("성북구"),
    SONGPA("송파구"),
    YANGCHEON("양천구"),
    YEONGDEUNGPO("영등포구"),
    YONGSAN("용산구"),
    EUNPYEONG("은평구"),
    JONGNO("종로구"),
    JUNG("중구"),
    JUNGRANG("중랑구");

    private final String label;
    RecordDistrict(String label) { this.label = label; }
    public String getLabel() { return label; }

    /** 한글 라벨 -> Enum 매핑 */
    public static RecordDistrict fromLabel(String raw) {
        if (raw == null) return null;
        String key = Normalizer.normalize(raw.trim(), Normalizer.Form.NFKC);
        return Arrays.stream(values())
                .filter(d -> d.label.equals(key))
                .findFirst()
                .orElse(null);
    }
}
