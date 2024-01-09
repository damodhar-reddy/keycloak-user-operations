package com.testing.keycloak.utilities;

import java.util.Map;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.testing.keycloak.KeyValue;
import com.testing.keycloak.requestbodies.CredentialDTO;
import com.testing.keycloak.requestbodies.UserDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeycloakUtilities {

	private static final String AUTHORIZATION = "Authorization";
	private static final String BEARER = "Bearer ";

	private final RestTemplate restTemplate;

	@Autowired
	private KeyValue keyValue;

	@Autowired
	public KeycloakUtilities(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public Map<String, String> getAdminToken() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(keyValue.getClientId(), keyValue.getClientSecret());
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		HttpEntity<String> request = new HttpEntity<>("grant_type=" + keyValue.getGrantType(), headers);
		String accessTokenURL = keyValue.getAuthServerUrl() + "/realms/master/protocol/openid-connect/token";
		ResponseEntity<Object> response = restTemplate.exchange(accessTokenURL, HttpMethod.POST, request, Object.class,
				keyValue.getRealm());
		return (Map<String, String>) response.getBody();
	}

	public Boolean checkPersonalNoInKeycloak(String username, String adminToken) {
		UserRepresentation[] users = fetchKeycloakUsers(adminToken);
		if (users != null && users.length > 0) {
			for (UserRepresentation user : users) {
				if (username.equals(user.getUsername())) {
					log.info(user.toString());
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public Boolean checkEmailIdInKeycloak(String emailId, String adminToken) {
		UserRepresentation[] users = fetchKeycloakUsers(adminToken);
		if (users != null && users.length > 0) {
			for (UserRepresentation user : users) {
				if (emailId.equals(user.getEmail())) {
					log.info("KeycloakEmailIdFetcher--------------------->" + user.getEmail());
					return true;
				}
			}
			return false;
		}
		return false;
	}

	public ResponseEntity<Map<String, Object>> keycloakUserCreator(String username, String password, String firstName,
			String lastName, String email, boolean active, String adminToken) {
		String url = "/" + keyValue.getUsersRealm() + "/users";
		final String KEYCLOAK_USERS_URL = keyValue.getAuthServerUrl() + "/admin/realms" + url;
		final String ADMIN_TOKEN = adminToken;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(AUTHORIZATION, BEARER + ADMIN_TOKEN);
		boolean user = checkPersonalNoInKeycloak(username, ADMIN_TOKEN);
		boolean emailUser = checkEmailIdInKeycloak(email, ADMIN_TOKEN);
		if (!user) {
			if (!emailUser) {
				UserDTO userDTO = new UserDTO();
				userDTO.setUsername(username);
				CredentialDTO credentialDTO = new CredentialDTO();
				credentialDTO.setType("password");
				credentialDTO.setValue(password);
				credentialDTO.setTemporary(true);
				userDTO.setCredentials(new CredentialDTO[] { credentialDTO });
				userDTO.setEnabled(active);
				userDTO.setFirstName(firstName);
				userDTO.setLastName(lastName);
				userDTO.setEmail(email);
				HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

				ResponseEntity<String> response = restTemplate.exchange(KEYCLOAK_USERS_URL, HttpMethod.POST, request,
						String.class, keyValue.getUsersRealm());

				if (response.getStatusCode().is2xxSuccessful()) {
					return ResponseHandler.response("User created successfully", true, null);

				} else {
					return ResponseHandler.response("Failed to create user. Response: " + response.getBody(), false,
							null);
				}
			} else {
				return ResponseHandler.response("EmailId already taken", false, null);
			}
		} else {
			return ResponseHandler.response("Personal No. already taken", false, null);
		}
	}

	public UserRepresentation fetchUserDataInKc(String username, String adminToken) {
		UserRepresentation[] users = fetchKeycloakUsers(adminToken);
		if (users != null && users.length > 0) {
			for (UserRepresentation user : users) {
				if (username.equals(user.getUsername())) {
					log.info(user.toString());
					return user;
				}
			}
		}
		return null;
	}

	public UserRepresentation[] fetchKeycloakUsers(String adminToken) {
		log.info("admintoken=======>" + adminToken);
		String url = "/" + keyValue.getUsersRealm() + "/users";
		final String KEYCLOAK_USERS_URL = keyValue.getAuthServerUrl() + "/admin/realms" + url + "?first=0&max=10000000";
		log.info("KEYCLOAK_USERS_URL======" + KEYCLOAK_USERS_URL);

		final String ADMIN_TOKEN = adminToken;

		HttpHeaders headers = new HttpHeaders();
		headers.set(AUTHORIZATION, BEARER + ADMIN_TOKEN);
		ResponseEntity<UserRepresentation[]> response = restTemplate.exchange(KEYCLOAK_USERS_URL, HttpMethod.GET,
				new HttpEntity<>(headers), UserRepresentation[].class, keyValue.getUsersRealm());
		if (response.getStatusCode().is2xxSuccessful()) {
			UserRepresentation[] users = response.getBody();
			if (users != null && users.length > 0) {
				return users;
			} else {
				return null;
			}
		} else {
			log.info("Failed to fetch users. Response: " + response.getBody());
		}
		return null;
	}

	public ResponseEntity<Map<String, Object>> keycloakUserUpdater(String username, String firstName, String lastName,
			String email, boolean status, String adminToken) {
		UserRepresentation user = fetchUserDataInKc(username, adminToken);
		String url = "/" + keyValue.getUsersRealm() + "/users/" + user.getId();
		final String keycloakUrl = keyValue.getAuthServerUrl() + "/admin/realms" + url;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(AUTHORIZATION, BEARER + adminToken);
		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName(firstName);
		userDTO.setLastName(lastName);
		userDTO.setEnabled(status);
		if (user != null && !user.getEmail().equals(email)) {
			boolean emailUser = checkEmailIdInKeycloak(email, adminToken);
			if (!emailUser) {
				userDTO.setEmail(email);
			} else {
				return ResponseHandler.response("Email Id already taken by other user", false, null);
			}
		}
		HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.PUT, request, String.class,
				keyValue.getUsersRealm(), user.getId());

		if (response.getStatusCode().is2xxSuccessful()) {
			log.info("User updated successfully");
			return ResponseHandler.response("User updated successfully", true, null);
		} else {
			log.info("Failed to update in keycloak" + response.getBody());
			return ResponseHandler.response("Failed to update in keycloak cause : " + response.getBody(), false, null);

		}

	}

	public ResponseEntity<Map<String, Object>> keycloakUserDelete(String username, String adminToken) {
		UserRepresentation users = fetchUserDataInKc(username.toLowerCase(), adminToken);
		String url = "/" + keyValue.getUsersRealm() + "/users/" + users.getId();
		final String keycloakUrl = keyValue.getAuthServerUrl() + "/admin/realms" + url;

		final String ADMIN_TOKEN = adminToken;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(AUTHORIZATION, BEARER + ADMIN_TOKEN);
		HttpEntity<UserDTO> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.DELETE, request, String.class,
				keyValue.getUsersRealm(), users.getId());

		if (response.getStatusCode().is2xxSuccessful()) {
			log.info("User Deleted successfully");
			return ResponseHandler.response("User Deleted successfully", true, null);
		} else {
			log.info("Failed to delete user. Response: " + response.getBody());
			return ResponseHandler.response("Failed to delete user. Response: " + response.getBody(), false, null);

		}
	}

	public ResponseEntity<Map<String, Object>> changeActiveStatus(String username, boolean status, String adminToken) {
		UserRepresentation users = fetchUserDataInKc(username.toLowerCase(), adminToken);
		String url = "/" + keyValue.getUsersRealm() + "/users/" + users.getId();
		final String keycloakUrl = keyValue.getAuthServerUrl() + "/admin/realms" + url;

		final String ADMIN_TOKEN = adminToken;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(AUTHORIZATION, BEARER + ADMIN_TOKEN);
		UserDTO userDTO = new UserDTO();
		userDTO.setEnabled(status);
		HttpEntity<UserDTO> request = new HttpEntity<>(userDTO, headers);
		ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.PUT, request, String.class,
				keyValue.getUsersRealm(), users.getId());

		if (response.getStatusCode().is2xxSuccessful()) {
			log.info("User Deleted successfully");
			return ResponseHandler.response("Status Changed successfully", true, null);
		} else {
			log.info("Failed to delete user. Response: " + response.getBody());
			return ResponseHandler.response("Failed to delete user. Response: " + response.getBody(), false, null);

		}
	}

	public ResponseEntity<Map<String, Object>> changeCredentials(String username, String currentPassword,
			String newPassword, String adminToken) {
		if (validateCredentials(username, currentPassword)) {
			UserRepresentation users = fetchUserDataInKc(username.toLowerCase(), adminToken);
			String url = "/" + keyValue.getUsersRealm() + "/users/" + users.getId();
			final String keycloakUrl = keyValue.getAuthServerUrl() + "/admin/realms" + url + "/reset-password";
			System.out.println(keycloakUrl);
			final String ADMIN_TOKEN = adminToken;
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set(AUTHORIZATION, BEARER + ADMIN_TOKEN);

			CredentialRepresentation passwordDTO = new CredentialRepresentation();
			passwordDTO.setType("password");
			passwordDTO.setValue(newPassword);
			passwordDTO.setTemporary(false);
			HttpEntity<CredentialRepresentation> request = new HttpEntity<>(passwordDTO, headers);
			try {
				ResponseEntity<String> response = restTemplate.exchange(keycloakUrl, HttpMethod.PUT, request,
						String.class, keyValue.getUsersRealm(), users.getId());
				if (response.getStatusCode().is2xxSuccessful()) {
					return ResponseHandler.response("Password reset successfully", true, null);
				} else {
					return ResponseHandler.response("Failed to reset password. Response: " + response.getBody(), false,
							null);
				}
			} catch (HttpClientErrorException e) {
				if (e.toString().contains("invalidPasswordMinUpperCaseCharsMessage")) {
					return ResponseHandler.response("PasssWord Must Contains Atleast One UpperCase Character.", false,
							null);
				} else if (e.toString().contains("invalidPasswordMinLowerCaseCharsMessage")) {
					return ResponseHandler.response("PasssWord Must Contains Atleast One LowerCase Character.", false,
							null);
				} else if (e.toString().contains("invalidPasswordMinSpecialCharsMessage")) {
					return ResponseHandler.response("PasssWord Must Contains Atleast One Special Character.", false,
							null);
				} else if (e.toString().contains("invalidPasswordMinLengthMessage")) {
					return ResponseHandler.response("Password Should Contains Minimum Length of 8 Letters.", false,
							null);
				} else if (e.toString().contains("invalidPasswordMinDigitsMessage")) {
					return ResponseHandler.response("PasssWord Must Contains Atleast One Digit.", false, null);
				} else if (e.toString().contains("invalidPasswordMaxLengthMessage")) {
					return ResponseHandler.response("PasssWord Maximum Length Should be 14 Letters.", false, null);
				} else if (e.toString().contains("invalidPasswordHistoryMessage")) {

					return ResponseHandler.response("Password Should Not be Previous 3 Passwords.", false, null);
				} else {
					return ResponseHandler.response("Password Should Not username/email id.", false, null);
				}

			}
		} else {
			return ResponseHandler.response("Please Enter Valid Current Password.", false, null);
		}
	}

	public boolean validateCredentials(String username, String password) {
		String nonAdminUrl = keyValue.getAuthServerUrl() + "/realms/" + keyValue.getUsersRealm()
				+ "/protocol/openid-connect/token";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
		bodyParams.add("grant_type", "password");
		bodyParams.add("client_id", keyValue.getClientId());
		bodyParams.add("client_secret", keyValue.getNonAdminClientSecret());
		bodyParams.add("username", username);
		bodyParams.add("password", password);

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyParams, headers);
		ResponseEntity<String> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(nonAdminUrl, HttpMethod.POST, requestEntity, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("responseEntity=-===============>" + responseEntity);
		return responseEntity.getStatusCode() == HttpStatus.OK;
	}

}
