package com.spring.semi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spring.semi.dao.CategoryDao;
import com.spring.semi.dto.CategoryDto;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.service.CategoryService;
import com.spring.semi.vo.CategoryDetailVO;


/**
 * CategoryController - 웹 요청을 처리하는 MVC 컨트롤러.
 */
@Controller
@RequestMapping("/admin/category")
public class CategoryController {

	@Autowired
	private CategoryDao categoryDao;
    @Autowired
    private CategoryService categoryService;


	@GetMapping("/add")
	public String add() {
		return "/WEB-INF/views/admin/category/add.jsp";
	}

	@PostMapping("/add")
	public String write(@ModelAttribute CategoryDto categoryDto) {
		int categoryNo = categoryDao.sequence();
		categoryDto.setCategoryNo(categoryNo);
		categoryDao.insert(categoryDto);
		return "redirect:/admin/category/list";

	}


	@RequestMapping("/list")
	public String list(Model model, @RequestParam(required = false) String column,
			@RequestParam(required = false) String keyword) {
		boolean isSearch = column != null && keyword != null;
		if (isSearch) {
			List<CategoryDto> categoryList = categoryDao.searchList(column, keyword);
			model.addAttribute("categoryList", categoryList);
		} else {
			List<CategoryDto> categoryList = categoryDao.selectList();
			model.addAttribute("categoryList", categoryList);
		}
		return "/WEB-INF/views/admin/category/list.jsp";

	}


	@GetMapping("/edit")
	public String edit(@RequestParam int categoryNo, Model model) {
		CategoryDto categoryDto = categoryDao.selectOne(categoryNo);
		if (categoryDto == null)
			throw new TargetNotfoundException("존재하지 않는 게시판");
		model.addAttribute("categoryDto", categoryDto);
		return "/WEB-INF/views/admin/category/edit.jsp";
	}

	@PostMapping("/edit")
	public String update(@ModelAttribute CategoryDto categoryDto) {
		categoryDao.update(categoryDto);
		return "redirect:list";

	}


	@PostMapping("/delete")
	public String deletePost(@RequestParam int categoryNo) {
	    CategoryDto categoryDto = categoryDao.selectOne(categoryNo);
	    if (categoryDto == null)
	        throw new TargetNotfoundException("존재하지 않는 게시판");
	    categoryDao.delete(categoryNo);
	    return "redirect:list";
	}


	  @GetMapping("/stats")
	    public String categoryStats(@RequestParam String categoryName, Model model) {
	        CategoryDetailVO detail = categoryService.getCategoryDetailByName(categoryName);
	        model.addAttribute("categoryDetail", detail);
	        return "/WEB-INF/views/admin/category/stats.jsp";
	    }


}
