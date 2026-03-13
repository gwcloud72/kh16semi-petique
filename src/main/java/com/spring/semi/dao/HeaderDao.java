package com.spring.semi.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.HeaderDto;
import com.spring.semi.error.TargetNotfoundException;
import com.spring.semi.mapper.HeaderMapper;


/**
 * HeaderDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class HeaderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HeaderMapper headerMapper;

    private final Map<String, String> typeMap = Map.of(
    		"animal", "animal_header",
    		"type", "type_header"
    		);


    public int sequence(String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "SELECT #1_seq.NEXTVAL FROM dual";
        sql = sql.replace("#1", typeName);
        return jdbcTemplate.queryForObject(sql, int.class);
    }

    public List<HeaderDto> selectList(String type){
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
    	String sql = "select * from #1 order by header_no asc";
    	sql = sql.replace("#1", typeName);

    	return jdbcTemplate.query(sql, headerMapper);
    }


    public boolean insert(HeaderDto headerDto, String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "INSERT INTO #1 VALUES (?, ?)";
        sql = sql.replace("#1", typeName);
        Object[] params = { headerDto.getHeaderNo(), headerDto.getHeaderName() };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean update(HeaderDto headerDto, String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "UPDATE #1 SET header_name = ? WHERE header_no = ?";
        sql = sql.replace("#1", typeName);
        Object[] params = { headerDto.getHeaderName(), headerDto.getHeaderNo() };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean updateByHeaderNo(int headerNo, String newHeaderName, String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "UPDATE #1 SET header_name = ? WHERE header_no = ?";
        sql = sql.replace("#1", typeName);
        Object[] params = { newHeaderName, headerNo };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public boolean delete(int headerNo, String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "DELETE FROM #1 WHERE header_no = ?";
        sql = sql.replace("#1", typeName);
        Object[] params = { headerNo };
        return jdbcTemplate.update(sql, params) > 0;
    }


    public List<HeaderDto> selectByHeaderName(String headerName, String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "SELECT * FROM #1 WHERE header_name = ?";
        sql = sql.replace("#1", typeName);
        Object[] params = { headerName };
        return jdbcTemplate.query(sql, headerMapper, params);
    }


    public List<HeaderDto> selectAll(String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "SELECT * FROM #1 ORDER BY header_no ASC";
        sql = sql.replace("#1", typeName);
        return jdbcTemplate.query(sql, headerMapper);
    }


    public HeaderDto selectOne(int headerNo, String type) {
    	String typeName = typeMap.get(type);
    	if(typeName == null) throw new TargetNotfoundException();
        String sql = "SELECT * FROM #1 WHERE header_no = ?";
        sql = sql.replace("#1", typeName);
        Object[] params = { headerNo };
        List<HeaderDto> list = jdbcTemplate.query(sql, headerMapper, params);
        return list.isEmpty() ? null : list.get(0);
    }
}
