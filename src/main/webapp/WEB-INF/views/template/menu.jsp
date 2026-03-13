<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [템플릿] menu --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:choose>

	<c:when test="${sessionScope.loginId != null && sessionScope.loginLevel == '1' }">
		<a href="${cp}/">홈</a>
		<a href="${cp}/board/list">게시판</a>
		<a href="#">메일</a>
		<a href="${cp}/member/logout">로그아웃</a>
	</c:when>


	<c:when test="${sessionScope.loginId != null && sessionScope.loginLevel >= '2' }">
		<a href="${cp}/">홈</a>
		<a href="${cp}/board/list">게시판</a>
		<a href="#">메일</a>
		<a href="${cp}/member/logout">로그아웃</a>
	</c:when>


	<c:otherwise>
		<a href="${cp}/">홈</a>
		<a href="${cp}/board/list">게시판</a>
		<a href="${cp}/member/login">로그인</a>
		<a href="${cp}/member/join">회원가입</a>
	</c:otherwise>
</c:choose>
