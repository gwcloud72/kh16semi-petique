<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- [회원] 찾기 결과 화면 --%>

<c:set var="pageTitle" value="이메일 전송 완료" scope="request"/>
<c:set var="pageCss" value="/css/member-auth.css" scope="request"/>

<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="auth-page container w-550">
	<div class="auth-card">
		<div class="auth-head">
			<h1 class="auth-title">이메일 전송 완료</h1>
			<div class="auth-subtitle">요청하신 안내 메일을 발송했습니다.</div>
		</div>

		<div class="auth-note">
			메일함과 스팸함을 확인해 주세요. 몇 분이 지나도 도착하지 않으면 다시 시도해 주세요.
		</div>

		<div class="auth-actions">
			<a class="btn btn-positive w-100p" href="${cp}/member/login">로그인으로 이동</a>
			<a class="btn btn-menu w-100p" href="${cp}/member/findId">아이디 찾기</a>
			<a class="btn btn-menu w-100p" href="${cp}/member/findPw">비밀번호 찾기</a>
		</div>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
