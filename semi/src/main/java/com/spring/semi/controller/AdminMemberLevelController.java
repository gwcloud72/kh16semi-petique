package com.spring.semi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spring.semi.dao.MemberLevelDao;
import com.spring.semi.dto.MemberLevelDto;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.MemberService;


/**
 * AdminMemberLevelController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/admin/level")
public class AdminMemberLevelController {

	@Autowired
	private MemberLevelDao memberLevelDao;
	@Autowired
	private MemberService memberService;


	@GetMapping("/list")
	public String list(Model model) {
		List<MemberLevelDto> levels = memberLevelDao.selectAll();
		model.addAttribute("levels", levels);
		return "/WEB-INF/views/admin/level/list.jsp";
	}


	@GetMapping("/detail")
	public String detail(@RequestParam int levelNo, Model model) {
		MemberLevelDto level = memberLevelDao.selectOne(levelNo);
		if (level == null) {
			throw new TargetNotfoundException("해당 회원 등급이 존재하지 않습니다. levelNo=" + levelNo);
		}
		model.addAttribute("level", level);
		return "/WEB-INF/views/admin/level/detail.jsp";
	}


	@GetMapping("/add")
	public String add() {
		return "/WEB-INF/views/admin/level/add.jsp";
	}

	@PostMapping("/add")
	public String addSubmit(MemberLevelDto level) {
		memberLevelDao.insert(level);
		return "redirect:list";
	}

	@PostMapping("/delete")
	public String delete(@RequestParam int levelNo, RedirectAttributes redirectAttributes) {
	    MemberLevelDto level = memberLevelDao.selectOne(levelNo);
	    if (level == null) {
	        redirectAttributes.addFlashAttribute("message", "삭제할 회원 등급이 존재하지 않습니다. levelNo=" + levelNo);
	        return "redirect:list";
	    }

	    int memberCount = memberLevelDao.countMembersByLevel(levelNo);
	    if (memberCount > 0) {
	        redirectAttributes.addFlashAttribute("message",
	            "이 등급을 가진 회원이 존재하므로 삭제할 수 없습니다. 회원 수: " + memberCount);
	        return "redirect:list";
	    }

	    memberLevelDao.delete(levelNo);
	    redirectAttributes.addFlashAttribute("message", "회원 등급이 삭제되었습니다.");
	    return "redirect:list";
	}


	@GetMapping("/edit")
	public String edit(@RequestParam int levelNo, Model model) {
		MemberLevelDto level = memberLevelDao.selectOne(levelNo);
		if (level == null) {
			throw new TargetNotfoundException("수정할 회원 등급이 존재하지 않습니다. levelNo=" + levelNo);
		}
		model.addAttribute("level", level);
		return "/WEB-INF/views/admin/level/edit.jsp";
	}


	@PostMapping("/edit")
	public String edit(MemberLevelDto level) {
		MemberLevelDto memberLevelDto = memberLevelDao.selectOne(level.getLevelNo());
		if (memberLevelDto == null) {
			throw new TargetNotfoundException("수정할 회원 등급이 존재하지 않습니다. levelNo=" + level.getLevelNo());
		}
		memberLevelDao.update(level);
		return "redirect:detail?levelNo=" + memberLevelDto.getLevelNo();
	}


	@PostMapping("/updateAll")
	public String updateAll() {

	    memberService.updateMemberLevels();


	    return "redirect:list";
	}

}
