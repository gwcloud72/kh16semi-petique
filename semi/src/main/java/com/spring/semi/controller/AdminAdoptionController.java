package com.spring.semi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.AdoptionApplyDao;
import com.spring.semi.vo.AdoptionApprovalAdminVO;
import com.spring.semi.vo.PageVO;


/**
 * AdminAdoptionController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/admin/adoption")
public class AdminAdoptionController {

	@Autowired
	private AdoptionApplyDao adoptionApplyDao;

	@GetMapping("/approval")
	public String approval(
			@ModelAttribute("pageVO") PageVO pageVO,
			@RequestParam(required = false, defaultValue = "APPROVED") String status,
			Model model) {

		pageVO.setSize(10);

		int count = adoptionApplyDao.countAdminApproval(pageVO, status);
		pageVO.setDataCount(count);

		List<AdoptionApprovalAdminVO> list = adoptionApplyDao.selectAdminApprovalList(pageVO, status);

		model.addAttribute("status", status);
		model.addAttribute("list", list);
		return "/WEB-INF/views/admin/adoption/approval.jsp";
	}
}
