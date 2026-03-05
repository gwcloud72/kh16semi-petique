package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.AnimalHeaderDto;
import com.spring.semi.mapper.AnimalHeaderMapper;


/**
 * AnimalHeaderDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class AnimalHeaderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AnimalHeaderMapper animalHeaderMapper;


    public int sequence() {
        String sql = "SELECT animal_header_seq.NEXTVAL FROM dual";
        return jdbcTemplate.queryForObject(sql, int.class);
    }


    public boolean insert(AnimalHeaderDto dto) {
        String sql = "INSERT INTO animal_header (header_no, header_name) VALUES (?, ?)";
        Object[] params = { dto.getHeaderNo(), dto.getHeaderName() };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean update(AnimalHeaderDto dto) {
        String sql = "UPDATE animal_header SET header_name = ? WHERE header_no = ?";
        Object[] params = { dto.getHeaderName(), dto.getHeaderNo() };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean updateByNo(int no, String newName) {
        String sql = "UPDATE animal_header SET header_name = ? WHERE header_no = ?";
        Object[] params = { newName, no };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean delete(int no) {
        String sql = "DELETE FROM animal_header WHERE header_no = ?";
        Object[] params = { no };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public List<AnimalHeaderDto> selectByName(String name) {
        String sql = "SELECT * FROM animal_header WHERE header_name = ?";
        Object[] params = { name };
        return jdbcTemplate.query(sql, animalHeaderMapper, params);
    }


    public List<AnimalHeaderDto> selectAll() {
        String sql = "SELECT * FROM animal_header ORDER BY header_no ASC";
        return jdbcTemplate.query(sql, animalHeaderMapper);
    }


    public AnimalHeaderDto selectOne(int no) {
        String sql = "SELECT * FROM animal_header WHERE header_no = ?";
        Object[] params = { no };
        List<AnimalHeaderDto> list = jdbcTemplate.query(sql, animalHeaderMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }
}
