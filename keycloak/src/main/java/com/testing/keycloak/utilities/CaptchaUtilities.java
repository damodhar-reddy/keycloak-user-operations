package com.testing.keycloak.utilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.SquigglesBackgroundProducer;
import cn.apiclub.captcha.gimpy.ShearGimpyRenderer;
import cn.apiclub.captcha.noise.StraightLineNoiseProducer;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
public class CaptchaUtilities {
	
	public static final int CAPTCHA_WIDTH = 230;
	public static final int CAPTCHA_HEIGHT = 115;
	private SecureRandom random = new SecureRandom();
	private Map<String, String> captcha = new HashMap<>();
	
	
	private String nextCaptchId() {
		return new BigInteger(125, random).toString();
	}
	
	public String[] generateCaptchaImage() {
		Captcha captcha = new Captcha.Builder(CAPTCHA_WIDTH, CAPTCHA_HEIGHT)
				.addBackground(new SquigglesBackgroundProducer())
				.addNoise(new StraightLineNoiseProducer(Color.MAGENTA, 3))
				.addText().gimp(new ShearGimpyRenderer(Color.LIGHT_GRAY)).addBorder()
				.build();
		String captchaPngImage = null;
		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
		BufferedImage buffer = captcha.getImage();
		try {
			ImageIO.write(buffer, "png", byteArray);
			byteArray.flush();
			byte[] imageBytes = byteArray.toByteArray();
			byteArray.close();
			captchaPngImage = new String(Base64.getEncoder().encode(imageBytes), StandardCharsets.UTF_8);

		} catch (Exception e) {
			log.info("Exception occured while generating captch image.");
		}
		String captchaId = this.nextCaptchId();
		String[] image = { captchaPngImage, captchaId, captcha.getAnswer() };
		storeAnswerInMap(captchaId, captcha.getAnswer());
		log.info("captcha : " + captcha.getAnswer());
		return image;

	}

	public Map<String, String> storeAnswerInMap(String captchaId, String answer) {
		if (captchaId != null && answer != null) {
			captcha.put(captchaId, answer);
		}
		return captcha;
	}
}
