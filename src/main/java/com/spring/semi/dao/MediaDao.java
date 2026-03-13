package com.spring.semi.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.spring.semi.dto.MediaDto;
import com.spring.semi.mapper.MediaMapper;


/**
 * MediaDao - DB 접근을 담당하는 DAO.
 */
@Repository
public class MediaDao
{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private MediaMapper mediaMapper;

	public int sequence()
	{
		String sql = "select media_seq.nextval from dual";
		return jdbcTemplate.queryForObject(sql, int.class);
	}

	public void insert(MediaDto mediaDto)
	{
		String sql = "insert into media (media_no, media_name, "
				+ "media_type, media_size) "
				+ "values (?, ?, ?, ?)";
		Object[] params = {mediaDto.getMediaNo(), mediaDto.getMediaName(),
				mediaDto.getMediaType(), mediaDto.getMediaSize()};
		jdbcTemplate.update(sql, params);
	}

	public MediaDto selectOne(int mediaNo)
	{
		String sql = "select * from media where media_no = ?";
		Object[] params = {mediaNo};
		List<MediaDto> list = jdbcTemplate.query(sql, mediaMapper, params);
		return list.isEmpty()? null : list.get(0);
	}

	public boolean delete(int mediaNo)
	{
		String sql = "delete from media where media_no = ?";
		Object[] params = {mediaNo};
		return jdbcTemplate.update(sql, params) > 0;
	}
}
