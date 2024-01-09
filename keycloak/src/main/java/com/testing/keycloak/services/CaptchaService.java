package com.testing.keycloak.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface CaptchaService {

	public ResponseEntity<Map<String, Object>> getCaptcha();

}
