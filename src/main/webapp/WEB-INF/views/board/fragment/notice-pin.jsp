<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>


<%-- [게시판 공통] 화면 --%>

<c:if test="${sessionScope.loginLevel eq 0}">
	<div class="cell mt-20">
		<div class="flex-box">
			<div class="flex-box flex-vertical w-25p">
				<label>공지 고정순서</label>
				<input type="number" name="noticePinOrder" class="field w-100p mt-2" min="1" max="9999" value="${noticePinOrderValue}">
			</div>
			<div class="flex-box flex-vertical w-25p ms-10">
				<label>고정 시작일</label>
				<input type="date" name="noticePinStart" class="field w-100p mt-2" value="${noticePinStart}">
			</div>
			<div class="flex-box flex-vertical w-25p ms-10">
				<label>고정 종료일</label>
				<input type="date" name="noticePinEnd" class="field w-100p mt-2" value="${noticePinEnd}">
			</div>
		</div>
	</div>
</c:if>
