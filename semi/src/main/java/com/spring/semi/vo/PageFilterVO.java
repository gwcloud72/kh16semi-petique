package com.spring.semi.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * PageFilterVO - 화면/쿼리 결과용 VO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageFilterVO {


    private int page = 1;
    private int size = 10;
    private int dataCount;
    private int blockSize = 10;


    private int begin;
    private int end;


    private String column;
    private String keyword;
    private String animalHeaderName;
    private String typeHeaderName;
    private String adoptionStage;


    private String orderBy = "wtime";


    public boolean isSearch() {
        return (keyword != null && !keyword.isEmpty()) ||
               (animalHeaderName != null && !animalHeaderName.isEmpty()) ||
               (typeHeaderName != null && !typeHeaderName.isEmpty()) ||
               (adoptionStage != null && !adoptionStage.isEmpty());
    }

    public boolean isList() {
        return !isSearch();
    }


    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getTotalPage() {
        return (dataCount - 1) / size + 1;
    }

    public int getBlockStart() {
        return (page - 1) / blockSize * blockSize + 1;
    }

    public int getBlockFinish() {
        int number = (page - 1) / blockSize * blockSize + blockSize;
        return Math.min(getTotalPage(), number);
    }

    public boolean isFirstBlock() {
        return getBlockStart() == 1;
    }

    public boolean isLastBlock() {
        return getBlockFinish() == getTotalPage();
    }

    public int getPrevPage() {
        return getBlockStart() - 1;
    }

    public int getNextPage() {
        return getBlockFinish() + 1;
    }

    public void fixPageRange() {
        int total = getTotalPage();
        if (page < 1) page = 1;
        if (page > total) page = total;
    }


    public String getSearchParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("size=").append(size);

        if (orderBy != null && !orderBy.isEmpty()) sb.append("&orderBy=").append(orderBy);

        if (animalHeaderName != null && !animalHeaderName.isEmpty())
            sb.append("&animalHeaderName=").append(animalHeaderName);

        if (typeHeaderName != null && !typeHeaderName.isEmpty())
            sb.append("&typeHeaderName=").append(typeHeaderName);

        if (adoptionStage != null && !adoptionStage.isEmpty())
            sb.append("&adoptionStage=").append(adoptionStage);

        if (keyword != null && !keyword.isEmpty()) {
            sb.append("&keyword=").append(keyword);

            if (column != null && !column.isEmpty()) {
                sb.append("&column=").append(column);
            }
        }

        return sb.toString();
    }
}
