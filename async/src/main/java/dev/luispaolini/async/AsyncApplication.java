package dev.luispaolini.async;

import dev.luispaolini.async.post.Post;
import dev.luispaolini.async.post.PostClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class AsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncApplication.class, args);
	}

	@Bean
	WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl("https://jsonplaceholder.typicode.com").build();
	}

	@Bean
	CommandLineRunner commandLineRunner(PostClient client, PostClient postClient) {
		return args -> {
			Flux<Post> posts = client.findAll();
			posts.subscribe(System.out::println);
		};
	}



}
