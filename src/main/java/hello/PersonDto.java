package hello;

public class PersonDto {
	private String lastName;
	private String firstName;
	private String gender;
	private String company;

	public PersonDto() {
		super();
	}

	public PersonDto(String firstName, String lastName, String gender,
			String company) {
		super();
		this.lastName = lastName;
		this.firstName = firstName;
		this.gender = gender;
		this.company = company;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

}
