package hello;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class MyWriter implements ItemWriter<PersonDto> {

	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private CompanyRepository companyRepository;
	
	@Override
	public void write(List<? extends PersonDto> items) throws Exception {
		for (PersonDto personDto : items) {
			Company company = new Company(personDto.getCompany());
			company = this.companyRepository.save(company);
			Person person = new Person(personDto.getFirstName(), personDto.getLastName(), 
					personDto.getGender(), company.getId());
			
			this.personRepository.save(person);
		}
		
		

	}

}
