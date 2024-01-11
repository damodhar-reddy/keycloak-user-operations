package com.testing.keycloak.services.serviceImpl;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.tomcat.util.json.JSONFilter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.testing.keycloak.services.CaptchaService;
import com.testing.keycloak.utilities.CaptchaUtilities;
import com.testing.keycloak.utilities.ResponseHandler;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {

	@Autowired
	private CaptchaUtilities captchaUtilities;

	@Override
	public ResponseEntity<Map<String, Object>> getCaptcha() {
		String[] data = captchaUtilities.generateCaptchaImage();
		Base64.Encoder enc = Base64.getEncoder();
		String encode = enc.encodeToString(data[2].getBytes());
		Map<String, Object> obj = new HashMap<>();
		obj.put("captchaImage", data[0]);
		obj.put("captchaId", data[1]);
		obj.put("captchaEncoded", encode);
		return ResponseHandler.response("Capatcha generated successfully.", true, obj);
	}

	@Override
	public ResponseEntity<Map<String, Object>> validateCaptcha(String captchaData) {
		System.out.println(captchaData);
		JSONObject request = new JSONObject(captchaData);
		if (request.get("captchaId") == null) {
			return ResponseHandler.response("Please Provide Captcha Id", false, null);
		}
		if (request.get("captchaAnswer") == null) {
			return ResponseHandler.response("Please Provide Captcha Answer", false, null);
		}
		Map<String, String> captcha = captchaUtilities.storeAnswerInMap(null, null);
		String finalCaptcha = captcha.get(request.get("captchaId")).toString();
		if (finalCaptcha != null) {
			log.debug("Login to authenticate..." + finalCaptcha);
			if (finalCaptcha.equals(request.get("captchaAnswer"))) {
				captcha.remove(request.get("captchaId"));
				return ResponseHandler.response("Validated Successfully", true, null);
			} else {
				captcha.remove(request.get("captchaId"));
				return ResponseHandler.response("Please Provide Valid Captcha", false, null);
			}
		} else {
			captcha.remove(request.get("captchaId"));
			return ResponseHandler.response("Please Provide new Captcha", false, null);
		}
	}

//	@Scheduled(cron = "0 30 * * * *")

}
