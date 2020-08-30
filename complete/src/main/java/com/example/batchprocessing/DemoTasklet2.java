package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DemoTasklet2 implements Tasklet {
	private static final Logger log = LoggerFactory.getLogger(DemoTasklet2.class);
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		// TODO Auto-generated method stub
		log.info("this is a tasklet 2");
		return RepeatStatus.FINISHED;
	}

}
