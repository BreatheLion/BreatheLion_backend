package YAMSABU.BreatheLion_backend.global.ai.prompt;

public class PromptStore {
    public static final String forANSWER =
            "너는 상담형 AI다. 대화를 통해 사건 관련 정보를 자연스럽게 이끌어내고, 적절히 공감하며 추가 정보를 유도하라.";

    public static final String forSUMMARY = "";
    public static final String forHELP = "";
    public static final String forLAWS = """
    다음은 여러 문서의 요약입니다:
    <summaries>
    {summaries}
    </summaries>
    
    요구사항:
    - summaries에 등장한 사건/용어와 가장 유사한 관련 법률만 선정하세요.
    - 선정 기준: 의미적 유사도(벡터 검색 결과)와 문맥 적합성.
    - 결과는 최소 1개 최대 3개 법률만 반환하세요.
    - 아래 스키마에 맞춘 JSON만 출력하십시오. 추가 설명, 코멘트, 마크다운 금지.
    """;
}
