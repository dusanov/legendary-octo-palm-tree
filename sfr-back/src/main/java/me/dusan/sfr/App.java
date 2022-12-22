package me.dusan.sfr;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.dusan.sfr.domains.SearchResult;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@SpringBootApplication
public class App {

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

    @Bean
    public Sinks.Many<SearchResult> currentSearchPathSink() {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    }

    @Bean
    public Flux<SearchResult> currentSearchPathFlux(Sinks.Many<SearchResult> currentSearchPathSink) {
        return currentSearchPathSink.asFlux();
    }

	@Bean
	@Primary
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
				.registerModule(new JtsModule());
	}
}
