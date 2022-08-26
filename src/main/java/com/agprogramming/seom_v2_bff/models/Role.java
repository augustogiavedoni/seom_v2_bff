package com.agprogramming.seom_v2_bff.models;

import javax.persistence.*;

import lombok.*;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private SystemRole name;
	
	public Role(SystemRole name) {
		this.name = name;
	}
}
