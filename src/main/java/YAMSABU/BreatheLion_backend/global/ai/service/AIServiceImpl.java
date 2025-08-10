package YAMSABU.BreatheLion_backend.global.ai.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import static YAMSABU.BreatheLion_backend.global.ai.prompt.PromptStore.forANSWER;

@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService{

    private final ChatClient chatClient;

    @Override
    @Transactional
    public String ChatAnswer(String message){
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4.1-nano")
                .temperature(0.7)
                .build();

        return chatClient.prompt()
                .system(forANSWER)
                .user(message)
                .options(openAiChatOptions)
                .call()
                .content();
    }

}
