package com.spring.semi.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.semi.dao.CategoryDao;
import com.spring.semi.vo.CategoryDetailVO;


/**
 * CategoryService - 비즈니스 로직을 담당하는 서비스.
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    public CategoryDetailVO getCategoryDetailByName(String categoryName) {

        CategoryDetailVO category = categoryDao.selectBasicCategoryStatsByName(categoryName);

        if (category == null) {
            return null;
        }


        category.setLastUseTime(categoryDao.selectLastUseTime(category.getCategoryNo()));


        category.setLastUser(categoryDao.selectLastUser(category.getCategoryNo()));

        return category;
    }
}
