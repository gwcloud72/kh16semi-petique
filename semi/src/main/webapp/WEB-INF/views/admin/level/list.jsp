<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%-- [관리자] 레벨/뱃지 관리 목록 --%>

<jsp:include page="/WEB-INF/views/template/header.jsp" />


<div class="container w-800 admin-page">
	<div class="cell center">
		<h1>회원 등급 목록</h1>
	</div>
	<c:if test="${not empty message}">
		<div class="alert alert-info">${message}</div>
	</c:if>

	<div class="cell right">
		<a class="btn btn-neutral" href="${cp}/admin/home">목록으로</a>
	</div>

	<div class="cell right mb-20">
		<a href="${pageContext.request.contextPath}/admin/level/add"
			class="btn btn-positive">등급 추가</a>

		<form
			action="${pageContext.request.contextPath}/admin/level/updateAll"
			method="post" class="d-inline">
			<button type="submit" class="btn btn-neutral">전체 등급 갱신</button>
		</form>

	</div>

	<c:choose>
		<c:when test="${empty levels}">
			<div class="no-levels">등록된 등급이 없습니다.</div>
		</c:when>
		<c:otherwise>
			<table class="table table-border table-hover table-striped w-100p">
				<thead>
					<tr>
						<th>번호</th>
						<th>뱃지</th>
						<th>등급명</th>
						<th>최소 포인트</th>
						<th>최대 포인트</th>
						<th>설명</th>
						<th>관리</th>
						<th>해당 등급 회원수</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="level" items="${levels}">
						<tr>
							<td>${level.levelNo}</td>
							<td class="center" class="admin-badge-image">${level.badgeImage}</td>
							<td><a class="link"
								href="${pageContext.request.contextPath}/admin/level/detail?levelNo=${level.levelNo}">
									${level.levelName} </a></td>
							<td>${level.minPoint}</td>
							<td>${level.maxPoint}</td>
							<td>${level.description}</td>
							<td class="actions">
                      <a class="btn-link" href="${pageContext.request.contextPath}/admin/level/edit?levelNo=${level.levelNo}">수정</a>


								<c:if test="${level.memberCount == 0}">
									<form method="post"
										action="${pageContext.request.contextPath}/admin/level/delete"
									 class="d-inline"
										onsubmit="return confirm('정말 삭제하시겠습니까?');">
										<input type="hidden" name="levelNo" value="${level.levelNo}">
										<button type="submit" class="btn btn-delete">삭제</button>
									</form>
								</c:if>
							</td>
							<td>${level.memberCount}</td>

						</tr>
					</c:forEach>

				</tbody>

			</table>
		</c:otherwise>
	</c:choose>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp" />
