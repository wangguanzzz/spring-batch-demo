package com.example.batchprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingApplication {

	public static void main(String[] args) throws Exception {
		String [] newArgs = new String[] {"inputFlatFile=/sample-data.csv"};
		
		System.exit(SpringApplication.exit(SpringApplication.run(BatchProcessingApplication.class, newArgs)));
		
	}
}
