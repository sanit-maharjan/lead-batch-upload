package hello;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "person")
public class Person {
	private Integer id;
    private String lastName;
    private String firstName;
    private String gender;
    private Integer comapnyId;

    public Person() {

    }

    public Person(String firstName, String lastName, String gender, Integer companyId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.comapnyId = companyId;
    }

    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "first_name")
    public String getFirstName() {
        return firstName;
    }

    @Column(name = "last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    
    @Column(name = "gender")
    public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	@Column(name = "company_id")
	public Integer getComapnyId() {
		return comapnyId;
	}

	public void setComapnyId(Integer comapnyId) {
		this.comapnyId = comapnyId;
	}

	@Override
    public String toString() {
        return "firstName: " + firstName + ", lastName: " + lastName;
    }

}
