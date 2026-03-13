package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.TypeHeaderDto;
import com.spring.semi.mapper.TypeHeaderMapper;


/**
 * TypeHeaderDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class TypeHeaderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TypeHeaderMapper typeHeaderMapper;


    public int sequence() {
        String sql = "SELECT type_header_seq.NEXTVAL FROM dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }


    public boolean insert(TypeHeaderDto dto) {
        String sql = "INSERT INTO type_header (header_no, header_name) VALUES (?, ?)";
        Object[] params = { dto.getHeaderNo(), dto.getHeaderName() };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean update(TypeHeaderDto dto) {
        String sql = "UPDATE type_header SET header_name = ? WHERE header_no = ?";
		Object[] params = { dto.getHeaderName(), dto.getHeaderNo() };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean updateByNo(int no, String newName) {
        String sql = "UPDATE type_header SET header_name = ? WHERE header_no = ?";
        Object[] params = { newName, no };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean delete(int no) {
        String sql = "DELETE FROM type_header WHERE header_no = ?";
        Object[] params = { no };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public List<TypeHeaderDto> selectByName(String name) {
        String sql = "SELECT * FROM type_header WHERE header_name = ?";
        Object[] params = { name };
        return jdbcTemplate.query(sql, typeHeaderMapper, params);
    }


    public List<TypeHeaderDto> selectAll() {
        String sql = "SELECT * FROM type_header ORDER BY header_no ASC";
        return jdbcTemplate.query(sql, typeHeaderMapper);
    }


    public TypeHeaderDto selectOne(int no) {
        String sql = "SELECT * FROM type_header WHERE header_no = ?";
        Object[] params = { no };
        List<TypeHeaderDto> list = jdbcTemplate.query(sql, typeHeaderMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }
}
