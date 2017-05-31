package hello;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanTest {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
		PersonDto personDto = new PersonDto();
		personDto.setAddress("Kathmandu");
		personDto.setFirstName("John");
		personDto.setLastName("Doe");
		personDto.setGender("Male");
		personDto.setPosition("CEO");
		
		HeaderMapper headerMapper = new HeaderMapper();
		String[] headers = {"firstName", "lastName"};
		
		Class headerMapperClass = headerMapper.getClass();
		Field[] mapperField = headerMapperClass.getDeclaredFields();
		List<String> mappedHeader = new ArrayList<>();
		
		for (String header : headers) {
			for (Field field : mapperField) {
				if(header.equals(field.getName())) {
					System.out.println(field.getName());
				}
						
			}
		}
		
		
		
	
		
	

	}

}
