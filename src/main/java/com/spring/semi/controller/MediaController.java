package com.spring.semi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.MediaDao;
import com.spring.semi.dto.MediaDto;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.MediaService;


/**
 * MediaController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/media")
public class MediaController
{
	@Autowired
	private MediaService mediaService;
	@Autowired
	private MediaDao mediaDao;

	@GetMapping("/download")
	public ResponseEntity<ByteArrayResource> download(@RequestParam int mediaNo) throws IOException
	{
		MediaDto mediaDto = mediaDao.selectOne(mediaNo);
		if (mediaDto == null)
			throw new TargetNotfoundException("존재하지 않는 파일");

		ByteArrayResource resource = mediaService.load(mediaNo);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name())
				.header(HttpHeaders.CONTENT_TYPE, mediaDto.getMediaType())
				.contentLength(mediaDto.getMediaSize())
				.header(HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename(mediaDto.getMediaName(), StandardCharsets.UTF_8).build().toString())
				.body(resource);
	}
}
