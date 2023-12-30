package com.testing.keycloak.utilities;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public class ResponseHandler {
	
	public static ResponseEntity<Map<String,Object>> response(String message, Boolean status,Object data){
		Map<String, Object> map = new HashMap<>();
		map.put("message", message);
		map.put("status", status);
		map.put("data", data);
		return ResponseEntity.ok(map);
	}

}
