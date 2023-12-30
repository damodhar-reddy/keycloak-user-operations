package com.testing.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class KeyValue {
	
	@Value("${keycloak.realm}")
	private  String realm;
	
	@Value("${keycloak.auth-server-url}")
	private String authServerUrl;
	
	@Value("${keycloak.resource}")
	private  String clientId;
	
	@Value("${keycloak.credentials.secret}")
	private  String clientSecret;
	
	@Value("${keycloak-users-realm}")
	private  String usersRealm;
	
	@Value("${keycloak-grant-type}")
	private String grantType;
	
	@Value("${keycloak-creadentials-nonadmin}")
	private String nonAdminClientSecret;
}
