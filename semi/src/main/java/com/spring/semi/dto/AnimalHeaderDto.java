package com.spring.semi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * AnimalHeaderDto - 요청/응답 데이터 전달 객체.
 */
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AnimalHeaderDto {
    private int headerNo;
    private String headerName;
}
