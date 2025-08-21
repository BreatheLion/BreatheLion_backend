package YAMSABU.BreatheLion_backend.domain.record.entity;

import lombok.Getter;

import java.text.Normalizer;
import java.util.Arrays;

@Getter
public enum RecordCategory {
//    언어폭력, 신체폭력, 성희롱, 성폭력, 차별행위, 따돌림, 괴롭힘, 스토킹, 기타
    VERBAL_ABUSE("언어폭력"),
    PHYSICAL_ABUSE("신체폭력"),
    SEXUAL_HARASSMENT("성희롱"),
    SEXUAL_VIOLENCE("성폭력"),
    DISCRIMINATION("차별행위"),
    OSTRACISM("따돌림"),
    BULLYING("괴롭힘"),
    STALKING("스토킹"),
    ETC("기타");

    private final String label;

    RecordCategory(String label) { this.label = label; }

    /** 한글 라벨 -> Enum 매핑 */
    public static RecordCategory fromLabel(String raw) {
        if (raw == null) return null;
        String key = Normalizer.normalize(raw.trim(), Normalizer.Form.NFKC);
        return Arrays.stream(values())
                .filter(c -> c.label.equals(key))
                .findFirst()
                .orElse(null); // 못 찾으면 null (호출부에서 필터/검증)
    }
}
