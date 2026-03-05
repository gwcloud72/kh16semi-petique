package com.spring.semi.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.semi.dao.AnimalDao;
import com.spring.semi.dto.AnimalDto;
import com.spring.semi.service.MediaService;
import com.spring.semi.vo.PageVO;


/**
 * AdminAnimalController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/admin/animal")
public class AdminAnimalController {

	@Autowired
	private AnimalDao animalDao;
	@Autowired
	private MediaService mediaService;

	@GetMapping("/list")
	public String list(
			Model model,
			@ModelAttribute PageVO pageVO
			) {
		pageVO.setDataCount(animalDao.count(pageVO));
		List<AnimalDto> animalList = animalDao.selectListForPaging(pageVO);

		model.addAttribute("animalList", animalList);
		model.addAttribute("pageVO", pageVO);

		return "/WEB-INF/views/admin/animal/list.jsp";
	}

	@GetMapping("detail")
	public String detail(
			Model model,
			@RequestParam int animalNo
			) {
		AnimalDto animalDto = animalDao.selectOne(animalNo);

		model.addAttribute("animalDto", animalDto);

		return "/WEB-INF/views/admin/animal/detail.jsp";
	}

	@GetMapping("/edit")
	public String edit(
			Model model,
			@RequestParam int animalNo
			) {
		AnimalDto animalDto = animalDao.selectOne(animalNo);

		model.addAttribute("animalDto", animalDto);

		return "/WEB-INF/views/admin/animal/edit.jsp";

	}
	@PostMapping("/edit")
	public String edit(
			@ModelAttribute AnimalDto animalDto,
			@RequestParam(required = false) MultipartFile media
			) throws IllegalStateException, IOException {
		if(media != null && media.getContentType().contains("application/octet-stream") == false) {
			try {
				int mediaNo = animalDao.findMediaNo(animalDto.getAnimalNo());
				mediaService.delete(mediaNo);
			} catch (Exception e) {}
			int mediaNo = mediaService.save(media);
			animalDao.connect(animalDto.getAnimalNo(), mediaNo);
		}

		animalDao.update(animalDto);


		return "redirect:detail?animalNo=" + animalDto.getAnimalNo();
	}

	@GetMapping("/delete")
	public String delete(
			@RequestParam int animalNo
			) {
		animalDao.delete(animalNo);

		try {
			int mediaNo = animalDao.findMediaNo(animalNo);
			mediaService.delete(mediaNo);
		} catch (Exception e) {}

		return "redirect:list";
	}

}
