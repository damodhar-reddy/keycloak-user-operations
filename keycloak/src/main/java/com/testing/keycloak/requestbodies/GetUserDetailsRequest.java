package com.testing.keycloak.requestbodies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetUserDetailsRequest {
	
	private String userPersonalNo;
}
