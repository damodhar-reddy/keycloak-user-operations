package com.testing.keycloak.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.testing.keycloak.services.CaptchaService;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
	
	@Autowired
	private CaptchaService captchaService;
	
	@GetMapping("/get")
	private ResponseEntity<Map<String, Object>> getCaptcha(){
		return captchaService.getCaptcha();
	}
}
