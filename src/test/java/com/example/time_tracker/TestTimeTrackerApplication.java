package com.example.time_tracker;

import org.springframework.boot.SpringApplication;

public class TestTimeTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.from(TimeTrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
