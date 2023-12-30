package com.testing.keycloak.requestbodies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCredentialsRequest {

	private String userPersonalNo;

	private String currentPassword;

	private String newPassword;

	private String confirmNewPassword;
}
