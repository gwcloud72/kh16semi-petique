<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [회원] 로그인 화면 --%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-300">
	<form action="login" method="post">
		<div class="cell">
			<label>아이디</label>
			<input class="field w-100p" type="text" name="memberId">
		</div>
		<div class="cell">
			<label>비밀번호</label>
			<input class="field w-100p" type="password" name="memberPw">
		</div>
		<div class="cell center">
			<button class="btn btn-positive w-100p" type="submit">로그인</button>
		</div>
		<hr>
		<div class="cell center">
			<a href="findId" class="link gray">아이디 찾기</a>
			<span class="gray">|</span>
			<a href="findPw" class="link gray">비밀번호 찾기</a>
			<span class="gray">|</span>
			<a class="link gray" href="join">회원가입</a>
		</div>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
