package com.lzl.fbsse;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

/**
 * @author eren.liao
 * @version v1.0
 * @description
 * @date 2025/11/20 15:13
 **/
@SpringBootApplication
public class FbsseApplication {


    public static void main(String[] args) {
        SpringApplication.run(FbsseApplication.class, args);
    }

    @Bean
    CommandLineRunner ingestTermOfServiceToVectorStore( VectorStore vectorStore,
                                                        @Value("classpath:rag/terms-of-service.txt") Resource termsOfServiceDocs) {

        return args -> {
            vectorStore.write(                                  // 3.写入向量数据库
                    new TokenTextSplitter().transform(          // 2.分隔、向量化
                            new TextReader(termsOfServiceDocs).read())  // 1.读取文本
            );
        };
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore.SimpleVectorStoreBuilder builder = SimpleVectorStore.builder(embeddingModel);
        return builder.build();
    }
}

    
    
    