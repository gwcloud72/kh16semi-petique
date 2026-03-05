package com.spring.semi.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * PageVO - 화면/쿼리 결과용 VO.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PageVO
{

	private int page = 1;
	private int size = 10;
	private String column, keyword;
	private int dataCount;
	private int blockSize = 10;


	public boolean isSearch()
	{
		return column != null && keyword != null;
	}

	public boolean isList()
	{
		return column == null || keyword == null;
	}

	public String getSearchParams()
	{
		if (isSearch())
			return "size=" + size + "&column=" + column + "&keyword=" + keyword;
		else
			return "size=" + size;
	}

	public int getBlockStart()
	{
		return (page - 1) / blockSize * blockSize + 1;
	}

	public int getBlockFinish()
	{
		int number = (page - 1) / blockSize * blockSize + blockSize;
		return Math.min(getTotalPage(), number);
	}

	public int getTotalPage()
	{
		return (dataCount - 1) / size + 1;
	}

	public int getBegin()
	{
		return page * size - (size - 1);
	}

	public int getEnd()
	{
		return page * size;
	}

	public boolean isFirstBlock()
	{
		return getBlockStart() == 1;
	}

	public boolean isLastBlock()
	{
		return getBlockFinish() == getTotalPage();
	}

	public int getPrevPage()
	{
		return getBlockStart() - 1;
	}

	public int getNextPage()
	{
		return getBlockFinish() + 1;
	}

	public void fixPageRange() {
	    int total = getTotalPage();
	    if (page < 1) page = 1;
	    if (page > total) page = total;
	}
}
