package kr.io.blankspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BlanksapceAppApplication {
	public static void main(String[] args)
	{ SpringApplication.run(BlanksapceAppApplication.class, args); }
}