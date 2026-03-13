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
import com.spring.semi.util.DummyAnimalImage;
import com.spring.semi.error.NeedPermissionException;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.error.UnauthorizationException;
import com.spring.semi.service.MediaService;
import com.spring.semi.vo.PageVO;

import jakarta.servlet.http.HttpSession;


/**
 * AnimalController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/animal")
public class AnimalController {

	@Autowired
	private AnimalDao animalDao;
	@Autowired
	private MediaService mediaService;

	@GetMapping("/list")
	public String list(Model model, HttpSession session, @ModelAttribute("pageVO") PageVO pageVO) {
		String loginId = (String) session.getAttribute("loginId");
		if (loginId == null) throw new UnauthorizationException("로그인이 필요합니다");

		if (pageVO.getKeyword() != null && pageVO.getKeyword().isBlank()) {
			pageVO.setKeyword(null);
			pageVO.setColumn(null);
		}

		pageVO.setSize(8);
		pageVO.setDataCount(animalDao.countByMaster(pageVO, loginId));
		pageVO.fixPageRange();

		List<AnimalDto> animalList = animalDao.selectListByMasterForPaging(pageVO, loginId);
		model.addAttribute("animalList", animalList);
		return "/WEB-INF/views/animal/list.jsp";
	}

	@GetMapping("/add")
	public String addForm() {
		return "/WEB-INF/views/animal/add.jsp";
	}

	@PostMapping("/add")
	public String add(@ModelAttribute AnimalDto animalDto, @RequestParam(required = false) MultipartFile media, HttpSession session)
			throws IllegalStateException, IOException {

		String loginId = (String) session.getAttribute("loginId");
		if (loginId == null) throw new UnauthorizationException("로그인이 필요합니다");

		int animalNo = animalDao.sequence();
		animalDto.setAnimalNo(animalNo);
		animalDto.setAnimalMaster(loginId);

		if (!"t".equals(animalDto.getAnimalPermission()) && !"f".equals(animalDto.getAnimalPermission())) {
			animalDto.setAnimalPermission("f");
		}

		animalDao.insert(animalDto);

		if (media != null && !media.isEmpty()) {
			int mediaNo = mediaService.save(media);
			animalDao.connect(animalNo, mediaNo);
		}

		return "redirect:detail?animalNo=" + animalNo;
	}

	@GetMapping("/detail")
	public String detail(Model model, @RequestParam int animalNo, HttpSession session) {
		AnimalDto animalDto = animalDao.selectOne(animalNo);
		if (animalDto == null) throw new TargetNotfoundException("존재하지 않는 동물 정보");
		checkOwner(animalDto, session);

		model.addAttribute("animalDto", animalDto);
		return "/WEB-INF/views/animal/detail.jsp";
	}

	@GetMapping("/edit")
	public String editForm(Model model, @RequestParam int animalNo, HttpSession session) {
		AnimalDto animalDto = animalDao.selectOne(animalNo);
		if (animalDto == null) throw new TargetNotfoundException("존재하지 않는 동물 정보");
		checkOwner(animalDto, session);

		model.addAttribute("animalDto", animalDto);
		return "/WEB-INF/views/animal/edit.jsp";
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute AnimalDto animalDto, @RequestParam(required = false) MultipartFile media, HttpSession session)
			throws IllegalStateException, IOException {

		AnimalDto before = animalDao.selectOne(animalDto.getAnimalNo());
		if (before == null) throw new TargetNotfoundException("존재하지 않는 동물 정보");
		checkOwner(before, session);

		if (!"t".equals(animalDto.getAnimalPermission()) && !"f".equals(animalDto.getAnimalPermission())) {
			animalDto.setAnimalPermission(before.getAnimalPermission());
		}

		animalDao.update(animalDto);

		if (media != null && !media.isEmpty()) {
			Integer oldMediaNo = null;
			try {
				oldMediaNo = animalDao.findMediaNo(animalDto.getAnimalNo());
			}
			catch (Exception e) {
			}

			int mediaNo = mediaService.save(media);
			boolean updated = animalDao.updateProfile(animalDto.getAnimalNo(), mediaNo);
			if (!updated) {
				animalDao.connect(animalDto.getAnimalNo(), mediaNo);
			}

			if (oldMediaNo != null) {
				try {
					mediaService.delete(oldMediaNo);
				}
				catch (Exception e) {
				}
			}
		}

		return "redirect:detail?animalNo=" + animalDto.getAnimalNo();
	}

	@PostMapping("/delete")
	public String delete(@RequestParam int animalNo, HttpSession session) {
		AnimalDto animalDto = animalDao.selectOne(animalNo);
		if (animalDto == null) throw new TargetNotfoundException("존재하지 않는 동물 정보");
		checkOwner(animalDto, session);

		Integer oldMediaNo = null;
		try {
			oldMediaNo = animalDao.findMediaNo(animalNo);
		}
		catch (Exception e) {
		}

		animalDao.disconnectProfile(animalNo);

		if (oldMediaNo != null) {
			try {
				mediaService.delete(oldMediaNo);
			}
			catch (Exception e) {
			}
		}

		animalDao.delete(animalNo);
		return "redirect:list";
	}

	@GetMapping("/profile")
	public String profile(@RequestParam int animalNo) {
		try {
			int mediaNo = animalDao.findMediaNo(animalNo);
			return "redirect:/media/download?mediaNo=" + mediaNo;
		}
		catch (Exception e) {
			return "redirect:" + DummyAnimalImage.path(animalNo);
		}
	}

	private void checkOwner(AnimalDto animalDto, HttpSession session) {
		String loginId = (String) session.getAttribute("loginId");
		Integer loginLevel = (Integer) session.getAttribute("loginLevel");
		boolean isAdmin = loginLevel != null && loginLevel == 0;
		boolean isOwner = loginId != null && loginId.equals(animalDto.getAnimalMaster());
		if (!isOwner && !isAdmin) throw new NeedPermissionException("권한이 없습니다");
	}
}
