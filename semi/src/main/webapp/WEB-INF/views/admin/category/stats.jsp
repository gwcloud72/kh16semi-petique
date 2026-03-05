<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%-- [관리자] 카테고리 관리 화면 --%>

<c:set var="pageTitle" value="게시판 상세 정보" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-600 admin-page">
	<div class="admin-header">
		<h1>게시판 상세 정보</h1>
		<div class="admin-actions">
			<a href="list" class="btn btn-neutral">목록</a>
		</div>
	</div>

	<table class="table table-border admin-detail-table">
		<tbody>
			<tr>
				<th>게시판 번호</th>
				<td>${categoryDetail.categoryNo}</td>
			</tr>
			<tr>
				<th>게시판 이름</th>
				<td>${categoryDetail.categoryName}</td>
			</tr>
			<tr>
				<th>게시글 수</th>
				<td>${categoryDetail.boardCount}</td>
			</tr>
			<tr>
				<th>마지막 활동 시간</th>
				<td>
					<c:choose>
						<c:when test="${not empty categoryDetail.lastUseTime}">
							<fmt:formatDate value="${categoryDetail.lastUseTime}" pattern="yyyy-MM-dd HH:mm:ss" />
						</c:when>
						<c:otherwise>활동 내역 없음</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th>마지막 활동 사용자</th>
				<td><c:out value="${categoryDetail.lastUser}" default="작성자 없음" /></td>
			</tr>
		</tbody>
	</table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
