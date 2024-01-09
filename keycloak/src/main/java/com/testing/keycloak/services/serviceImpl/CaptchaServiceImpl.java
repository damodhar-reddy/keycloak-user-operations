package com.testing.keycloak.services.serviceImpl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.testing.keycloak.services.CaptchaService;
import com.testing.keycloak.utilities.CaptchaUtilities;
import com.testing.keycloak.utilities.ResponseHandler;

public class CaptchaServiceImpl implements CaptchaService{
	
	@Autowired
	private CaptchaUtilities captchaUtilities;
	
	@Override
	public ResponseEntity<Map<String, Object>> getCaptcha() {
		String[] data =  captchaUtilities.generateCaptchaImage();
		Base64.Encoder enc = Base64.getEncoder();
		String encode = enc.encodeToString(data[2].getBytes());
		Map<String,Object> obj = new HashMap<>();
		obj.put("captchaImage", data[0]);
		obj.put("captchaId", data[1]);
		obj.put("captchaEncoded", encode);
		return ResponseHandler.response("Capatcha generated successfully.", true, obj);
	}
}
