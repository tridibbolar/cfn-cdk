package ${groupId}.domain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "${resource}")
public class ${resource} implements Serializable {
	@Id
	//@GeneratedValue
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "PASSPORT_NUMBER", nullable = false)
	private String passportNumber;
	
	public ${resource}() {
		super();
	}

	public ${resource}(Long id, String name, String passportNumber) {
		super();
		this.id = id;
		this.name = name;
		this.passportNumber = passportNumber;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassportNumber() {
		return passportNumber;
	}
	public void setPassportNumber(String passportNumber) {
		this.passportNumber = passportNumber;
	}
		
}
