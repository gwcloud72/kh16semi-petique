package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.AnimalDto;
import com.spring.semi.mapper.AnimalMapper;
import com.spring.semi.vo.PageVO;


/**
 * AnimalDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class AnimalDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private AnimalMapper animalMapper;

	public int sequence() {
		String sql = "select animal_seq.nextval from dual";

		return jdbcTemplate.queryForObject(sql, int.class);
	}
	public void insert(AnimalDto animalDto) {
		String sql = "insert into animal(animal_no, animal_name, animal_content ,animal_permission, animal_master) "
													+ "values(?, ?, ?, ?, ?)";
		Object[] params = {
				animalDto.getAnimalNo(),
				animalDto.getAnimalName(),
				animalDto.getAnimalContent(),
				animalDto.getAnimalPermission(),
				animalDto.getAnimalMaster()
		};

		jdbcTemplate.update(sql, params);
	}

	public List<AnimalDto> selectList(String animal_master) {
		String sql = "select * from animal where animal_master = ? order by animal_no asc";
		Object[] params = {animal_master};

		return jdbcTemplate.query(sql, animalMapper, params);
	}

	public List<AnimalDto> selectList() {
		String sql = "select * from animal order by animal_no asc";

		return jdbcTemplate.query(sql, animalMapper);
	}

	public boolean delete(int animal_no) {
		String sql = "delete from animal where animal_no = ?";
		Object[] params = {animal_no};

		return jdbcTemplate.update(sql, params) > 0;
	}

	public boolean update(AnimalDto animalDto) {
		String sql = "update animal "
								+ "set animal_name = ?, animal_content = ?, "
										+ "animal_permission = ? where animal_no = ?";
		Object[] params = {
				animalDto.getAnimalName(),
				animalDto.getAnimalContent(),
				animalDto.getAnimalPermission(),
				animalDto.getAnimalNo()
		};

		return jdbcTemplate.update(sql, params) > 0;

	}
	public AnimalDto selectOne(int animal_no) {
		String sql = "select * from animal where animal_no = ?";
		Object[] params = {animal_no};
		List<AnimalDto> list = jdbcTemplate.query(sql, animalMapper, params);

		return list.isEmpty()? null : list.get(0);
	}

	public int findMediaNo(int animal_no) {
		String sql = "select media_no from animal_profile where animal_no = ?";
		Object[] params = { animal_no };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}


		public int count(PageVO pageVO) {
			if (pageVO == null || pageVO.isList()) {
				String sql = "select count(*) from animal";
				return jdbcTemplate.queryForObject(sql, int.class);
			}

			String column = allowedColumn(pageVO.getColumn());
			String keyword = pageVO.getKeyword();
			if (column == null || keyword == null || keyword.isBlank()) {
				String sql = "select count(*) from animal";
				return jdbcTemplate.queryForObject(sql, int.class);
			}

			String sql = "select count(*) from animal where instr(" + column + ", ?) > 0";
			return jdbcTemplate.queryForObject(sql, int.class, keyword);
		}

		public List<AnimalDto> selectListForPaging(PageVO pageVO) {
			if (pageVO == null) return List.of();

			String column = allowedColumn(pageVO.getColumn());
			String keyword = pageVO.getKeyword();
			boolean hasKeyword = column != null && keyword != null && !keyword.isBlank();

			StringBuilder sql = new StringBuilder();
			List<Object> params = new java.util.ArrayList<>();

			sql.append("select * from (");
			sql.append("  select rownum rn, TMP.* from (");
			sql.append("    select * from animal ");
			if (hasKeyword) {
				sql.append("where instr(").append(column).append(", ?) > 0 ");
				params.add(keyword);
			}
			sql.append("order by ");
			if (hasKeyword) {
				sql.append(column).append(" asc, ");
			}
			sql.append("animal_no asc");
			sql.append("  ) TMP");
			sql.append(") where rn between ? and ?");

			params.add(pageVO.getBegin());
			params.add(pageVO.getEnd());
			return jdbcTemplate.query(sql.toString(), animalMapper, params.toArray());
		}

	private String allowedColumn(String column) {
		if ("animal_name".equals(column)) return "animal_name";
		if ("animal_content".equals(column)) return "animal_content";
		return null;
	}

	public int countByMaster(PageVO pageVO, String animalMaster) {
		String column = allowedColumn(pageVO.getColumn());
		if (pageVO.isList() || column == null || pageVO.getKeyword() == null) {
			String sql = "select count(*) from animal where animal_master = ?";
			Object[] params = { animalMaster };
			return jdbcTemplate.queryForObject(sql, int.class, params);
		}
		String sql = "select count(*) from animal where animal_master = ? and instr(#1, ?) > 0";
		sql = sql.replace("#1", column);
		Object[] params = { animalMaster, pageVO.getKeyword() };
		return jdbcTemplate.queryForObject(sql, int.class, params);
	}

	public List<AnimalDto> selectListByMasterForPaging(PageVO pageVO, String animalMaster) {
		String column = allowedColumn(pageVO.getColumn());
		if (pageVO.isList() || column == null || pageVO.getKeyword() == null) {
			String sql = "select * from ("
					+ "select rownum rn, TMP.* from ("
						+ "select * from animal where animal_master = ? order by animal_no desc"
					+ ") TMP"
				+ ") where rn between ? and ?";
			Object[] params = { animalMaster, pageVO.getBegin(), pageVO.getEnd() };
			return jdbcTemplate.query(sql, animalMapper, params);
		}
		String sql = "select * from ("
				+ "select rownum rn, TMP.* from ("
					+ "select * from animal where animal_master = ? and instr(#1, ?) > 0 order by animal_no desc"
				+ ") TMP"
			+ ") where rn between ? and ?";
		sql = sql.replace("#1", column);
		Object[] params = { animalMaster, pageVO.getKeyword(), pageVO.getBegin(), pageVO.getEnd() };
		return jdbcTemplate.query(sql, animalMapper, params);
	}

	public boolean updateProfile(int animalNo, int mediaNo) {
		String sql = "update animal_profile set media_no = ? where animal_no = ?";
		Object[] params = { mediaNo, animalNo };
		return jdbcTemplate.update(sql, params) > 0;
	}

	public void disconnectProfile(int animalNo) {
		String sql = "delete from animal_profile where animal_no = ?";
		Object[] params = { animalNo };
		jdbcTemplate.update(sql, params);
	}
		public void connect(int animalNo, int mediaNo) {
			String sql = "insert into animal_profile values(?, ?)";

			Object[] params = {animalNo, mediaNo};

			jdbcTemplate.update(sql, params);

		}

		public List<AnimalDto> selectFilterTMaster(String animal_master) {
		    String sql = "SELECT * FROM animal WHERE animal_master = ? AND animal_permission = 't'";

		    Object[] params = {animal_master};

		    return jdbcTemplate.query(sql, animalMapper, params);
		}

		public boolean updateMaster(int animalNo, String animalMaster) {
			String sql = "update animal set animal_master = ? where animal_no = ?";
			Object[] params = { animalMaster, animalNo };
			return jdbcTemplate.update(sql, params) > 0;
		}

}
