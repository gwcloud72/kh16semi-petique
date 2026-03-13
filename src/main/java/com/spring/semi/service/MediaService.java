package com.spring.semi.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.spring.semi.dao.MediaDao;
import com.spring.semi.dto.MediaDto;
import com.spring.semi.error.TargetNotfoundException;


/**
 * MediaService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class MediaService
{
	@Autowired
	private MediaDao mediaDao;

	private File home = new File(System.getProperty("user.home"));
	private File upload = new File(home, "upload");

	@Transactional
	public int save(MultipartFile media) throws IllegalStateException, IOException
	{
		int mediaNo = mediaDao.sequence();

		if (upload.exists() == false)
			upload.mkdirs();

		File target = new File(upload, String.valueOf(mediaNo));
		media.transferTo(target);

		MediaDto mediaDto = MediaDto.builder()
				.mediaNo(mediaNo)
				.mediaName(media.getOriginalFilename())
				.mediaType(media.getContentType())
				.mediaSize(media.getSize())
				.build();

		mediaDao.insert(mediaDto);
		return mediaNo;
	}

	public ByteArrayResource load(int mediaNo) throws IOException
	{
		File home = new File(System.getProperty("user.home"));
		File upload = new File(home, "upload");
		File target = new File(upload, String.valueOf(mediaNo));

		if (!target.isFile())
			throw new TargetNotfoundException("존재하지 않는 파일");

		byte[] data = Files.readAllBytes(target.toPath());
		ByteArrayResource resource = new ByteArrayResource(data);
		return resource;
	}

	public void delete(int mediaNo)
	{
		MediaDto mediaDto = mediaDao.selectOne(mediaNo);
		if (mediaDto == null)
			throw new TargetNotfoundException("존재하지 않는 파일");

		File target = new File(upload, String.valueOf(mediaNo));
		target.delete();

		mediaDao.delete(mediaNo);
	}
}
