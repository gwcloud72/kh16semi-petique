package com.spring.semi.restcontroller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dto.AnimalDto;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.service.MediaService;

import jakarta.servlet.http.HttpSession;


/**
 * AnimalRestController - 비동기/REST 요청을 처리하는 컨트롤러.
 */
@CrossOrigin
@RestController
@RequestMapping("/rest/animal")
public class AnimalRestController {
	@Autowired
	private AnimalDao animalDao;
	@Autowired
	private MediaService mediaService;

	@PostMapping("/add")
	public int add(
			@ModelAttribute AnimalDto animalDto,
			HttpSession session,
			@RequestParam(required = false) MultipartFile media
			) throws IllegalStateException, IOException {
		String loginId = (String) session.getAttribute("loginId");
		int seq = animalDao.sequence();

		animalDto.setAnimalNo(seq);
		animalDto.setAnimalMaster(loginId);
		animalDao.insert(animalDto);

		if(media != null) {
			int mediaNo = mediaService.save(media);
			animalDao.connect(animalDto.getAnimalNo(), mediaNo);
		}

		return seq;
	}


	@PostMapping("/edit")
	public void edit(
			@ModelAttribute AnimalDto animalDto,
			@RequestParam(required = false) MultipartFile media,
			HttpSession session
			) throws IllegalStateException, IOException {
		String loginId = (String) session.getAttribute("loginId");
		if(loginId == null) throw new NeedPermissionException("권한 부족");
		if(media != null) {
			int mediaNo = mediaService.save(media);
			animalDao.connect(animalDto.getAnimalNo(), mediaNo);
		}
		animalDto.setAnimalMaster(loginId);
		animalDao.update(animalDto);
	}

	@PostMapping("/delete")
	public void delete(
			@RequestParam int animalNo
			) {
		AnimalDto findDto = animalDao.selectOne(animalNo);
		if(findDto != null) {
			animalDao.delete(animalNo);
			try {
				int mediaNo = animalDao.findMediaNo(animalNo);
				mediaService.delete(mediaNo);
			} catch (Exception e) {}
		}
	}
}
