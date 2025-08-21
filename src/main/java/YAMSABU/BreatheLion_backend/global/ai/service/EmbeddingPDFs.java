package YAMSABU.BreatheLion_backend.global.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmbeddingPDFs implements ApplicationRunner {

    private final VectorStore vectorStore;

    @Value("${app.ingest.dir:classpath:/pdfs/*.pdf}")
    private String ingestPattern;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!isVectorStoreEmpty(vectorStore)) {
            System.out.println("[INGEST] 스킵: VectorStore가 이미 채워져 있습니다.");
            return;
        }

        var resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(ingestPattern);

        if (resources.length == 0) {
            System.out.println("[INGEST] 매칭된 PDF가 없습니다: " + ingestPattern);
            return;
        }

        var splitter = new TokenTextSplitter();
        List<Document> allChunks = new ArrayList<>();

        for (Resource r : resources) {
            try {
                var reader = new ParagraphPdfDocumentReader(r); // ✅ classpath 리소스 지원
                var docs = reader.get();
                var chunks = splitter.apply(docs);
                chunks.forEach(d -> d.getMetadata().put("source", r.getFilename()));
                allChunks.addAll(chunks);
                System.out.println("[INGEST] " + r.getFilename() + " -> " + chunks.size() + " chunks");
            } catch (Exception e) {
                System.out.println("[INGEST][ERR] " + r.getFilename() + " : " + e.getMessage());
            }
        }

        if (!allChunks.isEmpty()) {
            vectorStore.add(allChunks); // ✅ 여기서 자동 임베딩 + 저장
            System.out.println("[INGEST] 총 " + allChunks.size() + " 청크 저장 완료");
        }
    }

    private boolean isVectorStoreEmpty(VectorStore vectorStore) {
        var res = vectorStore.similaritySearch(
                SearchRequest.builder().query("probe").topK(1).build()
        );
        return res.isEmpty();
    }
}
