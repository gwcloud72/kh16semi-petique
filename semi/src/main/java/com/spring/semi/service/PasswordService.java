package com.spring.semi.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.semi.error.TargetNotfoundException;


/**
 * PasswordService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class PasswordService {


	private final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final String LOWER = "abcdefghijklmnopqrstuvwxyz";
	private final String NUMBER = "0123456789";
	private final String SPECIAL = "!@#$";
	private final String CHARACTERS = UPPER + LOWER + NUMBER + SPECIAL;


	public String passwordGenerator() {
		SecureRandom rnd = new SecureRandom();
		int num = 8 + rnd.nextInt(13);
		return passwordGenerator(num);
	}


	public String passwordGenerator(int length) {
		if(length < 8 || length > 20) {
			throw new TargetNotfoundException();
		}
		SecureRandom rnd = new SecureRandom();
		List<Character> chars = new ArrayList<>();

		chars.add(UPPER.charAt(rnd.nextInt(UPPER.length())));
		chars.add(LOWER.charAt(rnd.nextInt(LOWER.length())));
		chars.add(NUMBER.charAt(rnd.nextInt(NUMBER.length())));
		chars.add(SPECIAL.charAt(rnd.nextInt(SPECIAL.length())));


		for(int i = chars.size(); i < length; i++) {
			chars.add(CHARACTERS.charAt(rnd.nextInt(CHARACTERS.length())));
		}

		Collections.shuffle(chars, rnd);

		StringBuilder sb = new StringBuilder();
		for(char c : chars) {
			sb.append(c);
		}

		return sb.toString();

	}
}
