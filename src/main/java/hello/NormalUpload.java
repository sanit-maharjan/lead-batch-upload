package hello;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NormalUpload {
	
	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private CompanyRepository companyRepository;
	
	//@PostConstruct
	public void upload() {
		List<String> lines = new ArrayList<>();
		String line = null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(getClass().getClassLoader().getResource("MOCK_DATA.csv").getFile())));
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int counter = 0;
		for (String string : lines) {
			System.out.println("writing row " + counter++);
			String[] row = string.split(",");
			Company company = new Company(row[3]);
			company = this.companyRepository.save(company);
			Person person = new Person(row[0], row[1], 
					row[3], company.getId());
			
			this.personRepository.save(person);
		}

	}
	
	
}
