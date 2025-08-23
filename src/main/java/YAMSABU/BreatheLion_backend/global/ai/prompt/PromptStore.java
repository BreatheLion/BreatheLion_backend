package YAMSABU.BreatheLion_backend.global.ai.prompt;

public class PromptStore {
    public static final String forANSWER =
            "너는 상담형 AI다. 대화를 통해 사건 관련 정보를 자연스럽게 이끌어내고, 적절히 공감하며 추가 정보를 유도하라.";

    public static final String forRECORDSUMMARY =
    """
    Input:
    - 사건 제목: {title}
    - 사건 관련 정보: {info}

    규칙:
    - 내용을 110자 이내로 요약하라.
    - 불필요한 수식어나 해석은 넣지 말고, 주어진 사실만 정리하라.
    """;

    public static final String forCHATSUMMARY =
    """
    Input:
    - chatting log : {chattings}
    제공된 채팅 로그에서 정보를 추출하여 JSON 스키마에 맞게 값을 채워라.
    
    규칙:
    - severity는 0(낮음), 1(보통), 2(높음) 중 하나만 사용.
    - category는 아래 목록 중 하나만 선택.
        - "V언어폭력, 신체폭력, 성희롱, 성폭력, 차별행위, 따돌림, 괴롭힘, 스토킹, 기타"
    - assailant와 witness는 여러 명 가능. 쉼표나 조사 제거 후 이름만 배열로 채운다.
    - occurred_at: 발생한 사건시간
        - "yyyy.MM.DD (THH:mm)" 스트링 형식으로
    - 모든 정보는 모르겠으면 빈칸("" 또는 [] 또는 null)으로 남긴다.
    - 채팅에 명시된 내용만 추출. 추측 금지.
    - 스키마에 정의된 필드 외 다른 값은 절대 포함하지 마라.
    """;

    public static final String forHELP = """
    당신은 피해 사건을 요약하고, 적절한 지원 기관을 추천하며, 피해자에게 따뜻한 케어 메시지를 제공하는 역할을 합니다.
    Input:
    - summaries: {summaries}
    - organizations: {organizations}
    
    요구사항:
    - summary는 주어진 summaries을 정리합니다.
    - organization_id는 organizations에서 가장 적절한 기관의 번호를 고릅니다. (최대 2개)
    - care_guide는 “'장소'에서의 '피해방식'을 기록해 주신 용기에 깊이 감사드려요.” 형태의 문장으로 '장소','피해의식'을 채워 작성합니다.
    - 아래 스키마에 맞춘 JSON만 출력하십시오. 추가 설명, 코멘트, 마크다운 금지.
    """;

    public static final String forLAWS = """
    다음은 여러 문서의 요약입니다:
    {summaries}
    
    요구사항:
    - summaries에 등장한 사건/용어와 가장 유사한 관련 법률만 선정하세요.
    - 선정 기준: 의미적 유사도(벡터 검색 결과)와 문맥 적합성.
    - 결과는 최소 1개 최대 3개 법률만 반환하세요.
    - 아래 스키마에 맞춘 JSON만 출력하십시오. 추가 설명, 코멘트, 마크다운 금지.
    """;
}
