package com.testing.keycloak.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testing.keycloak.requestbodies.ChangeActiveStatusRequest;
import com.testing.keycloak.requestbodies.ChangeCredentialsRequest;
import com.testing.keycloak.requestbodies.CreateUserDetailsRequest;
import com.testing.keycloak.requestbodies.GetUserDetailsRequest;
import com.testing.keycloak.requestbodies.UpdateUserDetails;
import com.testing.keycloak.services.UserDetailsService;

@RestController
@RequestMapping("/user")
public class UserDetailsController {
	@Autowired
	private UserDetailsService userDetailsService;
	
	@PostMapping("/create")
	private ResponseEntity<Map<String, Object>> createUserDetails(@RequestBody CreateUserDetailsRequest createUserDetails,@RequestHeader String username){
		return userDetailsService.createUserDetails(createUserDetails,username);
	}
	
	@PutMapping("/update")
	private ResponseEntity<Map<String, Object>> updateUserDetails(@RequestBody UpdateUserDetails updateUserDetails,@RequestHeader String username){
		return userDetailsService.updateUserDetails(updateUserDetails,username);
	}
	
	@GetMapping("/get")
	private ResponseEntity<Map<String, Object>> getUserDetails(@RequestBody GetUserDetailsRequest getUserDetailsRequest,@RequestHeader String username){
		return userDetailsService.getUserDetails(getUserDetailsRequest,username);
	}
	
	@DeleteMapping("/delete")
	private ResponseEntity<Map<String, Object>> deleteUserDetails(@RequestBody GetUserDetailsRequest deleteUserDetailsRequest,@RequestHeader String username){
		return userDetailsService.deleteUserDetails(deleteUserDetailsRequest,username);
	}
	
	@PutMapping("/change/active/status")
	private ResponseEntity<Map<String, Object>> changeActiveStatus(@RequestBody ChangeActiveStatusRequest changeActiveStatusRequest,@RequestHeader String username){
		return userDetailsService.changeActiveStatus(changeActiveStatusRequest,username);
	}
	
	@PutMapping("/change/credentials")
	private ResponseEntity<Map<String, Object>> changeCredentials(@RequestBody ChangeCredentialsRequest changeCredentialsRequest,@RequestHeader String username){
		return userDetailsService.changeCredentials(changeCredentialsRequest,username);
	}
}
