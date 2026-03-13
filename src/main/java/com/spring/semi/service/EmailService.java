package com.spring.semi.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.spring.semi.dao.CertDao;
import com.spring.semi.dao.MemberDao;
import com.spring.semi.dto.CertDto;
import com.spring.semi.dto.MemberDto;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


/**
 * EmailService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class EmailService {
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private CertDao certDao;
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private PasswordService passwordService;

	public void sendEmail(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		sender.send(message);
	}

	public void sendCertNumber(String email) {
		Random r = new Random();
		int number = r.nextInt(100000);
		DecimalFormat df = new DecimalFormat("00000");
		String certNumber = df.format(number);


		CertDto certDto = certDao.selectOne(email);
		if(certDto == null) {
			certDao.insert(CertDto.builder()
					.certEmail(email).certNumber(certNumber)
					.build());
		}
		else {
			certDao.update(CertDto.builder()
					.certEmail(email).certNumber(certNumber)
					.build());
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("[KH PETIQUE] 인증번호를 확인하세요");
		message.setText("인증번호는 ["+certNumber+"] 입니다");
		sender.send(message);

	}

	public void sendWelcomeMail(MemberDto memberDto) throws MessagingException, IOException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

		helper.setTo(memberDto.getMemberEmail());
		helper.setSubject("[KH PETIQUE] 가입을 환영합니다!");

		ClassPathResource resource = new ClassPathResource("templates/joinEmail.html");
		File target = resource.getFile();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(target));
		while(true) {
			String line = reader.readLine();
			if(line == null) break;
			buffer.append(line);
		}
		reader.close();

		Document document = Jsoup.parse(buffer.toString());
		Element targetId = document.selectFirst("#target");
		Element targetLink = document.selectFirst("#link");
		targetId.text(memberDto.getMemberNickname());

		String url = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/")
				.build().toUriString();
		targetLink.attr("href", url);

		helper.setText(document.toString(), true);

		sender.send(message);
	}

	public void sendEmailForFindId(MemberDto memberDto) throws MessagingException, IOException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

		helper.setTo(memberDto.getMemberEmail());
		helper.setSubject("[KH PETIQUE] 아이디 찾기 결과");

		ClassPathResource resource = new ClassPathResource("templates/findId.html");
		File target = resource.getFile();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(target));
		while(true) {
			String line = reader.readLine();
			if(line == null) break;
			buffer.append(line);
		}
		reader.close();

		Document document = Jsoup.parse(buffer.toString());
		Element targetId = document.selectFirst("#target");
		Element targetLink = document.selectFirst("#link");
		targetId.text(memberDto.getMemberId());

		String url = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/")
				.build().toUriString();
		targetLink.attr("href", url);

		helper.setText(document.toString(), true);

		sender.send(message);
	}

	public void sendEmailForFindPw(MemberDto memberDto) throws MessagingException, IOException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

		helper.setTo(memberDto.getMemberEmail());
		helper.setSubject("[KH PETIQUE] 비밀번호 재설정 결과");

		ClassPathResource resource = new ClassPathResource("templates/findPw.html");
		File target = resource.getFile();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(target));
		while(true) {
			String line = reader.readLine();
			if(line == null) break;
			buffer.append(line);
		}
		reader.close();

		String newPassword = passwordService.passwordGenerator();
		memberDao.updateForUserPassword(newPassword, memberDto.getMemberId());

		Document document = Jsoup.parse(buffer.toString());
		Element targetPw = document.selectFirst("#target");
		Element targetLink = document.selectFirst("#link");
		targetPw.text(newPassword);

		String url = ServletUriComponentsBuilder
				.fromCurrentContextPath()
				.path("/")
				.build().toUriString();
		targetLink.attr("href", url);

		helper.setText(document.toString(), true);

		sender.send(message);
	}

}
