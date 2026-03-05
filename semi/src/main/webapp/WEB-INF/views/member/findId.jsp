<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- [회원] 아이디 찾기 화면 --%>

<c:set var="pageTitle" value="아이디 찾기" scope="request"/>
<c:set var="pageCss" value="/css/member-auth.css" scope="request"/>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script type="text/javascript" src="${cp}/js/find.js"></script>

<div class="auth-page container w-550">
	<div class="auth-card">
		<div class="auth-head">
			<h1 class="auth-title">아이디 찾기</h1>
			<div class="auth-subtitle">가입 시 등록한 이메일로 아이디를 안내드립니다.</div>
		</div>

		<form class="auth-form" action="findId" method="post" id="send-email" autocomplete="off">
			<div class="cell">
				<label>이메일</label>
				<input class="field w-100p" type="email" name="memberEmail" placeholder="example@petique.com" required>
				<div class="auth-hint">등록된 이메일만 전송됩니다.</div>
			</div>
			<div class="auth-actions">
				<button type="button" class="btn btn-positive w-100p btn-find-send">
					<i class="fa-solid fa-paper-plane"></i>
					<span>이메일 전송</span>
				</button>
			</div>
		</form>

		<div class="auth-help">
			<a href="${cp}/member/login">로그인</a>
			<span class="gray">|</span>
			<a href="${cp}/member/findPw">비밀번호 찾기</a>
			<span class="gray">|</span>
			<a href="${cp}/member/join">회원가입</a>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
