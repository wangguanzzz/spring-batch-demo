package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

	private static final Logger log = LoggerFactory.getLogger(PersonItemProcessor.class);

	@Override
	public Person process(final Person person) throws Exception {
		final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();
		
		final Person transformedPerson = new Person(firstName, lastName);
		
		if(firstName.equals("JANE")||firstName.equals("JOE")) {
			int temp = 1/0;
		}

		log.info("Converting (" + person + ") into (" + transformedPerson + ")");
		log.info(Thread.currentThread().getName());
		Thread.sleep(2000);
		return transformedPerson;
	}

}
