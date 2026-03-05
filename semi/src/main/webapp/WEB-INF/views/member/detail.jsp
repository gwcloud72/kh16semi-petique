<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [회원] 프로필 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="pageTitle" value="프로필" scope="request"/>
<c:set var="pageCss" value="/css/member-page.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-900">
	<div class="member-page">
		<div class="member-hero">
			<div class="member-hero__avatar">
				<img class="member-avatar" src="profile?member_id=${findDto.memberId}">
			</div>
			<div class="member-hero__info">
				<div class="member-hero__title">
					<h2 class="m-0"><c:out value="${findDto.memberNickname}"/></h2>
					<c:choose>
						<c:when test="${findDto.memberAuth == 't'}">
							<span class="badge badge-positive">인증</span>
						</c:when>
						<c:otherwise>
							<span class="badge badge-neutral">미인증</span>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="member-hero__meta">
					<div class="meta-row">
						<span class="meta-label">소개</span>
						<span class="meta-value">
							<c:choose>
								<c:when test="${empty findDto.memberDescriptionPlain}"><span class="gray">(소개 없음)</span></c:when>
								<c:otherwise><c:out value="${findDto.memberDescriptionPlain}"/></c:otherwise>
							</c:choose>
						</span>
					</div>
				</div>
			</div>
			<div class="member-hero__actions">
				<c:choose>
					<c:when test="${sessionScope.loginId == findDto.memberId}">
						<a class="btn btn-positive w-100p" href="${cp}/member/mypage">마이페이지</a>
						<a class="btn btn-neutral w-100p" href="${cp}/">홈</a>
					</c:when>
					<c:otherwise>
						<a class="btn btn-neutral w-100p" href="${cp}/">홈</a>
					</c:otherwise>
				</c:choose>
			</div>
		</div>

		<div class="member-tabs">
			<a class="member-tab ${tab == 'animals' ? 'on' : ''}" href="detail?memberNickname=${findDto.memberNickname}&tab=animals">동물 <span class="count">${animalCount}</span></a>
			<a class="member-tab ${tab == 'posts' ? 'on' : ''}" href="detail?memberNickname=${findDto.memberNickname}&tab=posts">게시글 <span class="count">${postCount}</span></a>
		</div>

		<c:choose>
			<c:when test="${tab == 'posts'}">
				<div class="member-panel">
					<form action="detail" method="get" class="member-search" autocomplete="off">
						<input type="hidden" name="memberNickname" value="${findDto.memberNickname}">
						<input type="hidden" name="tab" value="posts">
						<select name="column" class="field">
							<option value="board_title" ${pageVO.column == 'board_title' ? 'selected' : ''}>제목</option>
						</select>
						<input type="search" name="keyword" class="field flex-fill" value="${pageVO.keyword}" placeholder="검색어를 입력해 주세요">
						<button type="submit" class="btn btn-neutral">검색</button>
						<c:if test="${pageVO.search}">
							<a class="btn btn-menu" href="detail?memberNickname=${findDto.memberNickname}&tab=posts">초기화</a>
						</c:if>
					</form>

					<table class="table table-border table-hover mt-10">
						<thead>
							<tr>
								<th style="width:90px">No</th>
								<th style="width:140px">구분</th>
								<th>제목</th>
								<th style="width:120px">작성일</th>
								<th style="width:90px">조회</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty boardList}">
									<tr>
										<td colspan="5" class="center">게시글이 없습니다</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:forEach var="board" items="${boardList}">
										<tr>
											<td class="center">${board.boardNo}</td>
											<td class="center"><c:out value="${board.categoryName}"/></td>
											<td class="left">
												<a class="link" href="${cp}/board/${board.categoryKey}/detail?boardNo=${board.boardNo}">
													<c:out value="${board.boardTitle}"/>
												</a>
											</td>
											<td class="center">${board.formattedWtime}</td>
											<td class="center">${board.boardView}</td>
										</tr>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="5" class="center">
									<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
								</td>
							</tr>
						</tfoot>
					</table>
				</div>
			</c:when>

			<c:otherwise>
				<div class="member-panel">
					<form action="detail" method="get" class="member-search" autocomplete="off">
						<input type="hidden" name="memberNickname" value="${findDto.memberNickname}">
						<input type="hidden" name="tab" value="animals">
						<select name="column" class="field">
							<option value="animal_name" ${pageVO.column == 'animal_name' ? 'selected' : ''}>이름</option>
							<option value="animal_content" ${pageVO.column == 'animal_content' ? 'selected' : ''}>소개</option>
						</select>
						<input type="search" name="keyword" class="field flex-fill" value="${pageVO.keyword}" placeholder="검색어를 입력해 주세요">
						<button type="submit" class="btn btn-neutral">검색</button>
						<c:if test="${pageVO.search}">
							<a class="btn btn-menu" href="detail?memberNickname=${findDto.memberNickname}&tab=animals">초기화</a>
						</c:if>
					</form>

					<table class="table table-border table-hover mt-10">
						<thead>
							<tr>
								<th style="width:90px">사진</th>
								<th style="width:160px">이름</th>
								<th>소개</th>
								<th style="width:110px">분양</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty animalList}">
									<tr>
										<td colspan="4" class="center">등록된 동물이 없습니다</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:forEach var="animalDto" items="${animalList}">
										<tr>
											<td class="center"><img class="thumb-60" src="${cp}/animal/profile?animalNo=${animalDto.animalNo}"></td>
											<td><c:out value="${animalDto.animalName}"/></td>
											<td class="left">
												<c:choose>
													<c:when test="${empty animalDto.animalContent}"><span class="gray">(소개 없음)</span></c:when>
													<c:otherwise>
														<c:out value="${fn:length(animalDto.animalContent) > 80 ? fn:substring(animalDto.animalContent, 0, 80) : animalDto.animalContent}"/>
														<c:if test="${fn:length(animalDto.animalContent) > 80}">...</c:if>
													</c:otherwise>
												</c:choose>
											</td>
											<td class="center">
												<c:choose>
													<c:when test="${animalDto.animalPermission == 't'}"><span class="badge badge-positive">가능</span></c:when>
													<c:otherwise><span class="badge badge-neutral">불가</span></c:otherwise>
												</c:choose>
											</td>
										</tr>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="4" class="center">
									<jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
								</td>
							</tr>
						</tfoot>
					</table>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
