package com.testing.keycloak.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.testing.keycloak.requestbodies.ChangeActiveStatusRequest;
import com.testing.keycloak.requestbodies.ChangeCredentialsRequest;
import com.testing.keycloak.requestbodies.CreateUserDetailsRequest;
import com.testing.keycloak.requestbodies.GetUserDetailsRequest;
import com.testing.keycloak.requestbodies.UpdateUserDetails;

public interface UserDetailsService {

	public ResponseEntity<Map<String, Object>> createUserDetails(CreateUserDetailsRequest createUserDetails,
			String username);

	public ResponseEntity<Map<String, Object>> updateUserDetails(UpdateUserDetails updateUserDetails, String username);

	public ResponseEntity<Map<String, Object>> getUserDetails(GetUserDetailsRequest getUserDetailsRequest,
			String username);

	public ResponseEntity<Map<String, Object>> deleteUserDetails(GetUserDetailsRequest deleteUserDetailsRequest,
			String username);

	public ResponseEntity<Map<String, Object>> changeActiveStatus(ChangeActiveStatusRequest changeActiveStatusRequest,
			String username);

	public ResponseEntity<Map<String, Object>> changeCredentials(ChangeCredentialsRequest changeCredentialsRequest,
			String username);
}
