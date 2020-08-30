package com.example.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]

	// tag::readerwriterprocessor[]
	
	@Bean
	@StepScope
	public FlatFileItemReader<Person> reader(@Value("#{jobParameters['inputFlatFile']}") Resource resource) {
		return new FlatFileItemReaderBuilder<Person>()
			.name("personItemReader")
			.resource(resource)
			.delimited()
			.names(new String[]{"firstName", "lastName"})
			.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
				setTargetType(Person.class);
			}})
			.build();
	}

	@Bean
	@StepScope
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}

	@Bean
	@StepScope
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)")
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job demoJob() {
	//(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("demoJob")
			.incrementer(new RunIdIncrementer())
			.listener(new JobCompletionNotificationListener())
			.flow(importUser())
//			.next(dotasklet())
			.end()
			.build();
	}
	
    @Bean
    protected Step dotasklet() {
        return stepBuilderFactory
          .get("dotasklet")
          .tasklet(new DummyTasklet())
          .build();
    }

	@Bean
	public Step importUser() { 
		// for multi thread
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.afterPropertiesSet();
		
		return stepBuilderFactory.get("step1")
			.<Person, Person> chunk(10)
			.reader(reader(null))
			.processor(processor())
			.writer(writer(null))
			.taskExecutor(taskExecutor)
			.build();
	}
	// end::jobstep[]
}
