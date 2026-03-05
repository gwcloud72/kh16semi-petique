<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<%-- [관리자] 회원 관리 상세 --%>

<c:set var="pageTitle" value="회원 상세" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-900 admin-page">
	<div class="admin-header">
		<h1>회원 상세</h1>
		<div class="admin-actions">
			<a class="btn btn-neutral" href="list">목록</a>
		</div>
	</div>

	<table class="table table-border admin-detail-table">
		<tbody>
			<tr>
				<th>아이디</th>
				<td>${memberDto.memberId}</td>
			</tr>
			<tr>
				<th>닉네임</th>
				<td>${memberDto.memberNickname}</td>
			</tr>
			<tr>
				<th>이메일</th>
				<td><c:out value="${memberDto.memberEmail}" default="-"/></td>
			</tr>
			<tr>
				<th>인증 여부</th>
				<td><c:out value="${memberDto.memberAuth}" default="-"/></td>
			</tr>
			<tr>
				<th>소개글</th>
				<td><c:out value="${memberDto.memberDescriptionPlain}" default="-"/></td>
			</tr>
			<tr>
				<th>포인트</th>
				<td>${memberDto.memberPoint}</td>
			</tr>
		</tbody>
	</table>

	<div class="admin-actions">
		<a class="btn btn-neutral" href="edit?memberId=${memberDto.memberId}">정보 수정</a>
		<c:if test="${memberDto.memberEmail != null}">
			<a class="btn btn-neutral" href="password?memberEmail=${memberDto.memberEmail}">비밀번호 재설정</a>
		</c:if>
		<a class="btn btn-negative" href="drop?memberId=${memberDto.memberId}">강제 탈퇴</a>
	</div>

	<div class="admin-section-title">등록된 동물</div>
	<c:choose>
		<c:when test="${empty animalList}">
			<div class="alert alert-info">등록된 동물이 없습니다.</div>
		</c:when>
		<c:otherwise>
			<table class="table table-border table-striped admin-table">
				<thead>
					<tr>
						<th>번호</th>
						<th>이름</th>
						<th>소개</th>
						<th>분양 상태</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="animal" items="${animalList}">
						<tr>
							<td>${animal.animalNo}</td>
							<td><c:out value="${animal.animalName}"/></td>
							<td class="left"><c:out value="${animal.animalContent}"/></td>
							<td>
								<c:choose>
									<c:when test="${animal.animalPermission == 'f'}">분양불가</c:when>
									<c:otherwise>분양가능</c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>

	<div class="admin-section-title">작성 게시글</div>
	<c:choose>
		<c:when test="${empty boardListVO}">
			<div class="alert alert-info">작성한 게시글이 없습니다.</div>
		</c:when>
		<c:otherwise>
			<table class="table table-border table-hover table-striped admin-table">
				<thead>
					<tr>
						<th>번호</th>
						<th>카테고리</th>
						<th class="left">제목</th>
						<th>작성일</th>
						<th>조회</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="board" items="${boardListVO}">
						<tr>
							<td>${board.boardNo}</td>
							<td><c:out value="${board.categoryName}"/></td>
							<td class="left">
								<a class="link" href="${cp}/board/${board.categoryName}/detail?boardNo=${board.boardNo}"><c:out value="${board.boardTitle}"/></a>
							</td>
							<td><fmt:formatDate value="${board.boardWtime}" pattern="yyyy.MM.dd" /></td>
							<td>${board.boardView}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:otherwise>
	</c:choose>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
