package com.testing.keycloak.services.serviceImpl;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.testing.keycloak.entities.UserDetailsEntity;
import com.testing.keycloak.repositories.UserDetailsRepository;
import com.testing.keycloak.requestbodies.ChangeActiveStatusRequest;
import com.testing.keycloak.requestbodies.ChangeCredentialsRequest;
import com.testing.keycloak.requestbodies.CreateUserDetailsRequest;
import com.testing.keycloak.requestbodies.GetUserDetailsRequest;
import com.testing.keycloak.requestbodies.UpdateUserDetails;
import com.testing.keycloak.services.UserDetailsService;
import com.testing.keycloak.utilities.CaptchaUtilities;
import com.testing.keycloak.utilities.KeycloakUtilities;
import com.testing.keycloak.utilities.ResponseHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private KeycloakUtilities keycloakUtilities;

	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private CaptchaUtilities captchaUtilities;
	
	@Override
	@Transactional
	public ResponseEntity<Map<String, Object>> createUserDetails(CreateUserDetailsRequest createUserDetails,
			String username) {
		/* to fetch admin token */
		Map<String, String> adminTokenMap = keycloakUtilities.getAdminToken();
		log.info(adminTokenMap.toString());
		String accessToken = adminTokenMap.get("access_token");
		boolean checkUserId = keycloakUtilities.checkPersonalNoInKeycloak(createUserDetails.getUserPersonalNo(),
				accessToken);
		if (checkUserId) {
			return ResponseHandler.response("Personal No already Present.", false, null);
		}
		boolean checkEmailId = keycloakUtilities.checkEmailIdInKeycloak(createUserDetails.getEmail(), accessToken);
		if (checkEmailId) {
			return ResponseHandler.response("Email Already Taken By Another User. Kindly Use Different Email", false,
					null);
		}
		ResponseEntity<Map<String, Object>> createUserInKC = keycloakUtilities.keycloakUserCreator(
				createUserDetails.getUserPersonalNo(), createUserDetails.getPassword(),
				createUserDetails.getFirstName(), createUserDetails.getLastName(), createUserDetails.getEmail(), true,
				accessToken);

		if (Boolean.valueOf(createUserInKC.getBody().get("status").toString())) {
			try {
				UserDetailsEntity userDetailsEntity = new UserDetailsEntity();
				userDetailsEntity.setUserPersonalNo(createUserDetails.getUserPersonalNo());
				userDetailsEntity.setFirstName(createUserDetails.getFirstName());
				userDetailsEntity.setLastName(createUserDetails.getLastName());
				userDetailsEntity.setMiddleName(createUserDetails.getMiddleName());
				userDetailsEntity.setGender(createUserDetails.getGender());
				userDetailsEntity.setUpdatedBy(username);
				userDetailsEntity.setUpdatedDate(Date.from(Instant.now()));
				userDetailsEntity.setActiveFlag(true);
				userDetailsEntity.setCreatedBy(username);
				userDetailsEntity.setCreatedDate(Date.from(Instant.now()));
				userDetailsEntity.setEmail(createUserDetails.getEmail());
				userDetailsRepository.save(userDetailsEntity);
				return ResponseHandler.response("User Created Successfully.", true, null);
			} catch (Exception e) {
				GetUserDetailsRequest deleteRequest = new GetUserDetailsRequest();
				deleteRequest.setUserPersonalNo(createUserDetails.getUserPersonalNo());
				deleteUserDetails(deleteRequest, accessToken);
				return ResponseHandler.response("Exception Occured : " + e.getLocalizedMessage(), false, null);
			}
		} else {
			return ResponseHandler.response(
					"Failed To Create User. Cause : " + createUserInKC.getBody().get("message").toString(), false,
					null);
		}

	}

	@Override
	public ResponseEntity<Map<String, Object>> updateUserDetails(UpdateUserDetails updateUserDetails, String username) {
		log.info("update user started..");
		UserDetailsEntity userDetailsEntity = userDetailsRepository
				.findActiveUsersByUserPersonalNo(updateUserDetails.getUserPersonalNo());
		if (userDetailsEntity != null) {
			Map<String, String> adminToken = keycloakUtilities.getAdminToken();
			String token = adminToken.get("access_token");
			UserRepresentation user = keycloakUtilities
					.fetchUserDataInKc(updateUserDetails.getUserPersonalNo().toLowerCase(), token);
			if (user == null) {
				return ResponseHandler.response("personal No not found", false, null);
			}
			ResponseEntity<Map<String, Object>> keycloak = keycloakUtilities.keycloakUserUpdater(
					updateUserDetails.getUserPersonalNo().toLowerCase(), updateUserDetails.getFirstName(),
					updateUserDetails.getLastName(), updateUserDetails.getEmail().toLowerCase(), true, token);
			if (!Boolean.valueOf(keycloak.getBody().get("status").toString())) {
				return ResponseHandler.response("keyCloak Updation Failed", false, keycloak.getBody().get("message"));
			}
			userDetailsEntity.setEmail(updateUserDetails.getEmail());
			userDetailsEntity.setFirstName(updateUserDetails.getFirstName());
			userDetailsEntity.setGender(updateUserDetails.getGender());
			userDetailsEntity.setLastName(updateUserDetails.getLastName());
			userDetailsEntity.setMiddleName(updateUserDetails.getMiddleName());
			userDetailsEntity.setUpdatedBy(username);
			userDetailsEntity.setUpdatedDate(Date.from(Instant.now()));
			userDetailsRepository.save(userDetailsEntity);
			return ResponseHandler.response("User Details Updated Successfully.", true, null);
		}
		return ResponseHandler.response("Personal No Not Found", false, null);
	}

	@Override
	public ResponseEntity<Map<String, Object>> getUserDetails(GetUserDetailsRequest getUserDetailsRequest,
			String username) {
		UserDetailsEntity userDetails = userDetailsRepository
				.findByUserPersonalNo(getUserDetailsRequest.getUserPersonalNo());
		if (userDetails != null) {
			Map<String, Object> response = new HashMap<>();
			response.put("userPersonalNo", userDetails.getUserPersonalNo());
			response.put("activeStatus", userDetails.getActiveFlag() ? "Active" : "In Active");
			response.put("firstName", userDetails.getFirstName());
			response.put("middleName", userDetails.getMiddleName());
			response.put("lastName", userDetails.getLastName());
			response.put("email", userDetails.getEmail());
			response.put("gender", userDetails.getGender());
			return ResponseHandler.response("User Details Fetched Successfully", true, response);
		} else {
			return ResponseHandler.response("User Details Not Found For Given Personal No.", false, null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> deleteUserDetails(GetUserDetailsRequest deleteUserDetailsRequest,
			String username) {
		UserDetailsEntity userDetails = userDetailsRepository
				.findByUserPersonalNo(deleteUserDetailsRequest.getUserPersonalNo());
		if (userDetails != null) {
			try {
				Map<String, String> adminToken = keycloakUtilities.getAdminToken();
				String token = adminToken.get("access_token");
				ResponseEntity<Map<String, Object>> kcDeleteResponse = keycloakUtilities
						.keycloakUserDelete(deleteUserDetailsRequest.getUserPersonalNo(), token);
				if (Boolean.valueOf(kcDeleteResponse.getBody().get("status").toString())) {
					userDetailsRepository.delete(userDetails);
					return ResponseHandler.response("User Deleted Successfully.", true, null);
				} else {
					return ResponseHandler.response(kcDeleteResponse.getBody().get("message").toString(), false, null);
				}
			} catch (Exception e) {
				return ResponseHandler.response("Exception Occured : " + e.getLocalizedMessage(), false, null);
			}
		} else {
			return ResponseHandler.response("User Details Not Found For Given Personal No.", false, null);
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> changeActiveStatus(ChangeActiveStatusRequest changeActiveStatusRequest,
			String username) {
		UserDetailsEntity userDetails = userDetailsRepository
				.findByUserPersonalNo(changeActiveStatusRequest.getUserPersonalNo());
		if (userDetails == null) {
			return ResponseHandler.response("User Details Not Found for given personal No.", false, null);
		}

		Map<String, String> tokenMap = keycloakUtilities.getAdminToken();
		String accessToken = tokenMap.get("access_token");
		ResponseEntity<Map<String, Object>> kcActiveResponse = keycloakUtilities.changeActiveStatus(
				changeActiveStatusRequest.getUserPersonalNo(), changeActiveStatusRequest.isStatus(), accessToken);
		if (Boolean.valueOf(kcActiveResponse.getBody().get("status").toString())) {
			userDetails.setActiveFlag(changeActiveStatusRequest.isStatus());
			userDetails.setUpdatedBy(username);
			userDetails.setUpdatedDate(Date.from(Instant.now()));
			userDetailsRepository.save(userDetails);
			if (changeActiveStatusRequest.isStatus()) {
				return ResponseHandler.response("User Activated Successfully", true, null);
			} else {
				return ResponseHandler.response("User De Activated Successfully", true, null);
			}
		} else {
			if (changeActiveStatusRequest.isStatus()) {
				return ResponseHandler.response("Failed to Activate User", false,
						kcActiveResponse.getBody().get("message").toString());
			} else {
				return ResponseHandler.response("Failed to De Activate User", false,
						kcActiveResponse.getBody().get("message").toString());
			}
		}
	}

	@Override
	public ResponseEntity<Map<String, Object>> changeCredentials(ChangeCredentialsRequest changeCredentialsRequest,
			String username) {
		if (!changeCredentialsRequest.getNewPassword().equals(changeCredentialsRequest.getConfirmNewPassword())) {
			return ResponseHandler.response("New Password and Confirm Passwords are not Matching.", false, null);
		}
		UserDetailsEntity userDetails = userDetailsRepository
				.findByUserPersonalNo(changeCredentialsRequest.getUserPersonalNo());
		if (userDetails == null) {
			return ResponseHandler.response("User Details Not Found for given personal No.", false, null);
		}
		Map<String, String> tokenMap = keycloakUtilities.getAdminToken();
		String adminToken = tokenMap.get("access_token");
		ResponseEntity<Map<String, Object>> kcChangeCredentials = keycloakUtilities.changeCredentials(
				changeCredentialsRequest.getUserPersonalNo(), changeCredentialsRequest.getCurrentPassword(),
				changeCredentialsRequest.getNewPassword(), adminToken);
		if (Boolean.valueOf(kcChangeCredentials.getBody().get("status").toString())) {
			return ResponseHandler.response("Password Updated Successfully", true, null);
		} else {
			return ResponseHandler.response(kcChangeCredentials.getBody().get("message").toString(), false, null);
		}
	}
}
