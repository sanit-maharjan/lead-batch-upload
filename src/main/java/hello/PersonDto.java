package hello;

public class PersonDto {
	public String lastName;
	public String firstName;
	public String email;
	public String gender;
	public String company;
	public String position;
	public String phone;
	public String address;
	public String outputType;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
	
	

}
