package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.CategoryDto;
import com.spring.semi.mapper.CategoryDetailMapper;
import com.spring.semi.mapper.CategoryMapper;
import com.spring.semi.vo.CategoryDetailVO;


/**
 * CategoryDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class CategoryDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private CategoryDetailMapper categoryDetailMapper;

	public int sequence() {
		String sql = "select category_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	}


	public void insert(CategoryDto categoryDto) {
		String sql = "insert into category (category_no, category_name) VALUES (?, ?)";
		Object[] params = { categoryDto.getCategoryNo(), categoryDto.getCategoryName() };
		jdbcTemplate.update(sql, params);
	}


	public List<CategoryDto> selectList() {
		String sql = "select * from category order by category_no ASC";
		return jdbcTemplate.query(sql, categoryMapper);
	}


	public List<CategoryDto> searchList(String column, String keyword) {
		if (keyword == null || keyword.isBlank()) return List.of();
		String sql = "select * from category "
				+ "where instr(category_name, ?) > 0 "
				+ "order by category_name asc, category_no asc";
		Object[] params = { keyword };
		return jdbcTemplate.query(sql, categoryMapper, params);
	}


	public boolean delete(String categoryName) {
	    String sql = "delete from category where category_name = ?";
	    Object[] params = { categoryName };
	    return jdbcTemplate.update(sql, params) > 0;
	}
	public boolean delete(int categoryNo) {
		String sql = "delete from category where category_no = ?";
		Object[] params = { categoryNo };
		return jdbcTemplate.update(sql, params) > 0;
	}


	public boolean update(CategoryDto categoryDto) {
		String sql = "update category set category_name = ? where category_no = ?";
		Object[] params = { categoryDto.getCategoryName(), categoryDto.getCategoryNo() };
		return jdbcTemplate.update(sql, params) > 0;
	}

	public CategoryDto selectOne(String categoryName) {
		String sql = "select * from category where category_name = ?";
		Object[] params = { categoryName };
		List<CategoryDto> list = jdbcTemplate.query(sql, categoryMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}
	public CategoryDto selectOne(int categoryNo) {
		String sql = "select * from category where category_no = ?";
		Object[] params = { categoryNo };
		List<CategoryDto> list = jdbcTemplate.query(sql, categoryMapper, params);
		return list.isEmpty() ? null : list.get(0);
	}


	public CategoryDetailVO selectBasicCategoryStatsByName(String categoryName) {
	    String sql =
	        "select category_no, category_name, " +
	        "  (select count(*) from board where board_category_no = category.category_no) as board_count " +
	        "from category " +
	        "where category_name = ?";
	    Object[] params = { categoryName };
	    List<CategoryDetailVO> list = jdbcTemplate.query(sql, categoryDetailMapper, params);
	    return list.isEmpty() ? null : list.get(0);
	}


	public java.sql.Timestamp selectLastUseTime(int categoryNo) {
	    String sql =
	        "select max(greatest(nvl(board_etime, board_wtime), board_wtime)) " +
	        "from board " +
	        "where board_category_no = ?";
	    return jdbcTemplate.queryForObject(sql, java.sql.Timestamp.class, categoryNo);
	}


	public String selectLastUser(int categoryNo) {
	    String sql =
	        "select board_writer from ( " +
	        "  select board_writer, greatest(nvl(board_etime, board_wtime), board_wtime) as last_time " +
	        "  from board " +
	        "  where board_category_no = ? " +
	        "  order by last_time desc " +
	        ") where rownum = 1";
	    List<String> result = jdbcTemplate.queryForList(sql, String.class, categoryNo);
	    return result.isEmpty() ? null : result.get(0);
	}


	public CategoryDto selectOneByName(String categoryName) {
	    String sql = "select * from category where category_name = ?";
	    List<CategoryDto> list = jdbcTemplate.query(sql, categoryMapper, categoryName);
	    return list.isEmpty() ? null : list.get(0);
	}


}
