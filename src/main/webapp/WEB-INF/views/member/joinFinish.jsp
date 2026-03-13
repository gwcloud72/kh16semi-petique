<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- [회원] 회원가입 완료 화면 --%>

<c:set var="pageTitle" value="회원가입 완료" scope="request"/>
<c:set var="pageCss" value="/css/member-auth.css" scope="request"/>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="auth-page container w-550">
	<div class="auth-card">
		<div class="auth-head">
			<h1 class="auth-title">회원가입 완료</h1>
			<div class="auth-subtitle">이제 PETIQUE를 이용하실 수 있어요.</div>
		</div>

		<div class="auth-actions">
			<a class="btn btn-positive w-100p" href="${cp}/member/login">로그인 하기</a>
			<a class="btn btn-menu w-100p" href="${cp}/">홈으로</a>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
