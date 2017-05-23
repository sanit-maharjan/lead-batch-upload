package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<PersonDto, PersonDto> {

	private static final Logger log = LoggerFactory
			.getLogger(PersonItemProcessor.class);

	private String outputType;

	public PersonItemProcessor(String outputType) {
		super();
		this.outputType = outputType;
	}

	@Override
	public PersonDto process(final PersonDto person) throws Exception {
		final String firstName = person.getFirstName().toUpperCase();
		final String lastName = person.getLastName().toUpperCase();
		final String gender = person.getGender().toUpperCase();
		final String company = person.getCompany().toUpperCase();

		final PersonDto transformedPerson = new PersonDto(firstName, lastName,
				gender, company);
		transformedPerson.setEmail(person.getEmail());
		transformedPerson.setAddress(person.getAddress());
		transformedPerson.setPhone(person.getPhone());
		transformedPerson.setPosition(person.getPosition());
		transformedPerson.setOutputType(outputType);

		log.info("Converting (" + person + ") into (" + transformedPerson + ")");

		return transformedPerson;
	}

}
