package com.agprogramming.seom_v2_bff.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citizens", uniqueConstraints = { @UniqueConstraint(columnNames = {"cuil" }) })
@Data
@NoArgsConstructor
public class Citizen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotBlank
	@Size(max = 50)
	private String firstName;

	@NotBlank
	@Size(max = 50)
	private String lastName;

	@NotNull
	private Date birthdate;

	@NotBlank
	@Size(max = 15)
	private String cuil;

	public Citizen(String firstName, String lastName, Date birthdate, String cuil) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthdate = birthdate;
		this.cuil = cuil;
	}
}
