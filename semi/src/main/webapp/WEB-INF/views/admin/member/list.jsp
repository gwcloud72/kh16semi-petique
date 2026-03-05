<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [관리자] 회원 관리 목록 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-900 admin-page">
	<div class="cell right">
		<a class="btn btn-neutral" href="${cp}/admin/home">목록으로</a>
	</div>
	<div class="cell right">
		<form
			action="${pageContext.request.contextPath}/admin/level/updateAll"
			method="post" class="d-inline">
			<button type="submit" class="btn btn-neutral">전체 등급 갱신</button>
		</form>

	</div>

	<table class="table table-border table-striped table-hover w-100p">

		<tr>
			<th>아이디</th>
			<th>닉네임</th>
			<th>이메일</th>
			<th>인증</th>
			<th>포인트</th>
			<th>등급</th>
			<th>가입일</th>
			<th>최종로그인</th>
			<th>비밀번호변경일</th>
		</tr>
		<c:forEach var="memberDto" items="${memberList }">
			<tr>
				<td><a class="link"
					href="detail?memberId=${memberDto.memberId }">${memberDto.memberId }</a>
				</td>
				<td><span>${memberDto.memberNickname}</span></td>
				<td><span>${memberDto.memberEmail }</span></td>
				<td><span>${memberDto.memberAuth}</span></td>
				<td><span>${memberDto.memberPoint}</span></td>
				<td><span>${memberDto.memberLevel }</span></td>
				<td><span>${memberDto.memberJoin}</span></td>
				<td><span>${memberDto.memberLogin}</span></td>
				<td><span>${memberDto.memberChange}</span></td>
			</tr>
		</c:forEach>
		<tfoot>
			<tr>
				<td colspan="9" class="center">검색결과 : ${pageVO.begin} - ${pageVO.end} /
					${pageVO.dataCount}개</td>
			</tr>

			<tr>
				<td colspan="9" class="center"><jsp:include
						page="/WEB-INF/views/template/pagination.jsp"></jsp:include></td>
			</tr>
		</tfoot>
	</table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
