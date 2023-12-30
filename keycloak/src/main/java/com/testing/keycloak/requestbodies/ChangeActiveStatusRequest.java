package com.testing.keycloak.requestbodies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangeActiveStatusRequest {

	private String userPersonalNo;

	private boolean status;
}
