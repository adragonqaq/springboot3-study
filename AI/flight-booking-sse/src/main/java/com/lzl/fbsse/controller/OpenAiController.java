package com.lzl.fbsse.controller;

import com.lzl.fbsse.services.BookingTools;
import com.lzl.fbsse.services.LoggingAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


/**
 * @author wx:程序员徐庶
 * @version 1.0
 * @description: 智能航空助手:需要一对一解答关注wx: 程序员徐庶
 */
@RestController
@CrossOrigin
public class OpenAiController {

    private final ChatClient chatClient;

    public OpenAiController(ChatClient.Builder chatClientBuilder,
                            VectorStore vectorStore,
                            ChatMemory chatMemory,
                            BookingTools bookingTools,
                            // mcp tools
                            ToolCallbackProvider mcpTools) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
					    您是“图灵”航空公司的客户聊天支持代理。请以友好、乐于助人且愉快的方式来回复。
                       您正在通过在线聊天系统与客户互动。
                       在提供有关预订或取消预订的信息之前，您必须始终从用户处获取以下信息：预订号、客户姓名。
                       请讲中文。
                       今天的日期是 {current_date}.
                        在更改或退订function-call前，请先获取预订信息并且一定要等用户回复"确定"之后才进行更改或退订的function-call。 
					""")
                .defaultAdvisors(
                        new PromptChatMemoryAdvisor(chatMemory),
                        new LoggingAdvisor())
                .defaultTools(bookingTools)
                .defaultTools(mcpTools)
				 //.defaultFunctions("getBookingDetails", "changeBooking", "cancelBooking") // FUNCTION CALLING
				.build();
	}

    @Autowired
    private VectorStore vectorStore;

    @CrossOrigin
    @GetMapping(value = "/ai/generateStreamAsString", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStreamAsString(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {

        Flux<String> content = chatClient.prompt()
                .system(s -> s.param("current_date", LocalDate.now().toString()))
                //.advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .advisors(a -> a.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .user(message)
                .advisors(new QuestionAnswerAdvisor(vectorStore,

                        SearchRequest.builder().query(message)
                                .similarityThreshold(0.6)
                                .build()))
                .stream()
                .content();

        return  content
                .concatWith(Flux.just("[complete]"));

    }






}
