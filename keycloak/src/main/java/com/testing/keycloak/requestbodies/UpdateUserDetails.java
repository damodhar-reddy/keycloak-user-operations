package com.testing.keycloak.requestbodies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateUserDetails {
	
	private String userPersonalNo;

	private String firstName;

	private String lastName;

	private String email;

	private String gender;

	private String middleName;
}
