<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [회원] 마이페이지 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="pageTitle" value="마이페이지" scope="request"/>
<c:set var="pageCss" value="/css/member-page.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="${cp}/js/member-profile.js"></script>
<script src="${cp}/js/mypage.js"></script>

<div class="container w-900">
	<div class="member-page">
		<div class="member-hero">
			<div class="member-hero__avatar">
				<img class="image-profile member-avatar" src="profile?member_id=${memberDto.memberId}">
				<div class="member-hero__avatarActions">
					<label class="btn btn-menu" for="profile-input">변경</label>
					<button type="button" class="btn btn-neutral" id="profile-delete">삭제</button>
					<input type="file" id="profile-input" class="d-none" accept="image/*">
				</div>
			</div>

			<div class="member-hero__info">
				<div class="member-hero__title">
					<h2 class="m-0">
						<c:out value="${memberDto.memberNickname}"/>
					</h2>
					<c:choose>
						<c:when test="${memberDto.memberAuth == 't'}">
							<span class="badge badge-positive">인증</span>
						</c:when>
						<c:otherwise>
							<span class="badge badge-neutral">미인증</span>
						</c:otherwise>
					</c:choose>
				</div>

				<div class="member-hero__meta">
					<div class="meta-row">
						<span class="meta-label">아이디</span>
						<span class="meta-value"><c:out value="${memberDto.memberId}"/></span>
					</div>
					<div class="meta-row">
						<span class="meta-label">이메일</span>
						<span class="meta-value"><c:out value="${memberDto.memberEmail}"/></span>
					</div>
					<div class="meta-row">
						<span class="meta-label">소개</span>
						<span class="meta-value">
							<c:choose>
								<c:when test="${empty memberDto.memberDescriptionPlain}">
									<span class="gray">(소개 없음)</span>
								</c:when>
								<c:otherwise>
									<c:out value="${memberDto.memberDescriptionPlain}"/>
								</c:otherwise>
							</c:choose>
						</span>
					</div>
				</div>

				<div class="member-hero__stats">
					<div class="stat">
						<div class="stat__label">보유 포인트</div>
						<div class="stat__value"><fmt:formatNumber value="${memberDto.memberPoint}"/></div>
					</div>
					<div class="stat">
						<div class="stat__label">사용 포인트</div>
						<div class="stat__value"><fmt:formatNumber value="${memberDto.memberUsedPoint}"/></div>
					</div>
				</div>
			</div>

			<div class="member-hero__actions">
				<a class="btn btn-positive w-100p" href="edit">정보 수정</a>
				<a class="btn btn-neutral w-100p" href="password">비밀번호 변경</a>
				<a class="btn btn-menu w-100p" href="${cp}/animal/list">동물 관리</a>
				<a class="btn btn-negative w-100p" href="drop">회원 탈퇴</a>
			</div>
		</div>

		<div class="member-tabs">
			<a class="member-tab ${tab == 'write' ? 'on' : ''}" href="mypage?tab=write">작성글 <span class="count">${writeCount}</span></a>
			<a class="member-tab ${tab == 'deleted' ? 'on' : ''}" href="mypage?tab=deleted">삭제글 <span class="count">${deletedCount}</span></a>
			<a class="member-tab ${tab == 'animals' ? 'on' : ''}" href="mypage?tab=animals">동물 <span class="count">${animalCount}</span></a>
			<a class="member-tab ${tab == 'noti' ? 'on' : ''}" href="mypage?tab=noti">알림 <span class="count">${notiCount}</span></a>
			<a class="member-tab ${tab == 'points' ? 'on' : ''}" href="mypage?tab=points">포인트 내역 <span class="count">${pointCount}</span></a>
			<a class="member-tab ${tab == 'applies' ? 'on' : ''}" href="mypage?tab=applies">분양 신청 <span class="count">${applyCount}</span></a>
		</div>

		<c:choose>
			<c:when test="${tab == 'animals'}">
				<div class="member-panel">
					<form action="mypage" method="get" class="member-search" autocomplete="off">
						<input type="hidden" name="tab" value="animals">
						<select name="column" class="field">
							<option value="animal_name" ${pageVO.column == 'animal_name' ? 'selected' : ''}>이름</option>
							<option value="animal_content" ${pageVO.column == 'animal_content' ? 'selected' : ''}>소개</option>
						</select>
						<input type="search" name="keyword" class="field flex-fill" value="${pageVO.keyword}" placeholder="검색어를 입력해 주세요">
						<button type="submit" class="btn btn-neutral">검색</button>
						<c:if test="${pageVO.search}">
							<a class="btn btn-menu" href="mypage?tab=animals">초기화</a>
						</c:if>
					</form>

					<div class="panel-actions">
						<a class="btn btn-positive" href="${cp}/animal/add">
							<i class="fa-solid fa-plus"></i>
							<span>등록</span>
						</a>
						<a class="btn btn-menu" href="${cp}/animal/list">전체 보기</a>
					</div>

					<table class="table table-border table-hover">
						<thead>
							<tr>
								<th style="width:90px">사진</th>
								<th style="width:160px">이름</th>
								<th>소개</th>
								<th style="width:110px">분양</th>
								<th style="width:160px">관리</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty animalList}">
									<tr>
										<td colspan="5" class="center">등록된 동물이 없습니다</td>
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
											<td class="center">
												<a class="btn btn-menu" href="${cp}/animal/detail?animalNo=${animalDto.animalNo}">상세</a>
												<a class="btn btn-neutral" href="${cp}/animal/edit?animalNo=${animalDto.animalNo}">수정</a>
											</td>
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


			<c:when test="${tab == 'applies'}">
				<div class="member-panel">
					<table class="table table-border table-hover">
						<thead>
							<tr>
								<th style="width:170px">신청일</th>
								<th style="width:120px">상태</th>
								<th>분양글</th>
								<th style="width:140px">동물</th>
								<th style="width:140px">작성자</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty applyList}">
									<tr>
										<td colspan="5" class="center">분양 신청 내역이 없습니다</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:forEach var="a" items="${applyList}">
										<tr>
											<td class="center">
												<fmt:formatDate value="${a.applyWtime}" pattern="yy.MM.dd HH:mm"/>
											</td>
											<td class="center">
												<c:choose>
													<c:when test="${a.applyStatus == 'APPLIED'}"><span class="badge badge-neutral">접수</span></c:when>
													<c:when test="${a.applyStatus == 'APPROVED'}"><span class="badge badge-positive">승인</span></c:when>
													<c:when test="${a.applyStatus == 'COMPLETED'}"><span class="badge badge-positive">완료</span></c:when>
													<c:when test="${a.applyStatus == 'REJECTED'}"><span class="badge badge-negative">거절</span></c:when>
													<c:when test="${a.applyStatus == 'CANCELLED'}"><span class="badge badge-negative">취소</span></c:when>
													<c:otherwise><span class="badge badge-neutral"><c:out value="${a.applyStatus}"/></span></c:otherwise>
												</c:choose>
											</td>
											<td>
												<c:url var="adoptDetailUrl" value="${cp}/board/adoption/detail">
													<c:param name="boardNo" value="${a.boardNo}"/>
												</c:url>
												<a href="${adoptDetailUrl}" class="link"><c:out value="${a.boardTitle}"/></a>
											</td>
											<td class="center"><c:out value="${a.animalName}"/></td>
											<td class="center">
												<c:url var="writerProfileUrl" value="${cp}/member/detail">
													<c:param name="memberNickname" value="${a.boardWriterNickname}"/>
												</c:url>
												<a class="writer-link" href="${writerProfileUrl}"><c:out value="${a.boardWriterNickname}"/></a>
											</td>
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

			<c:when test="${tab == 'points'}">
				<div class="member-panel">
					<div class="panel-actions">
						<a class="btn btn-menu" href="${cp}/member/donation">펫콩</a>
					</div>

					<table class="table table-border table-hover">
						<thead>
							<tr>
								<th style="width:170px">일시</th>
								<th style="width:110px">구분</th>
								<th>내용</th>
								<th style="width:120px" class="right">변동</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty pointHistoryList}">
									<tr>
										<td colspan="4" class="center">포인트 내역이 없습니다</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:forEach var="h" items="${pointHistoryList}">
										<tr>
											<td class="center"><fmt:formatDate value="${h.historyWtime}" pattern="yyyy-MM-dd HH:mm"/></td>
											<td class="center">
												<c:choose>
													<c:when test="${h.historyType == 'EARN'}">적립</c:when>
													<c:when test="${h.historyType == 'DONATE'}">기부</c:when>
													<c:otherwise>변경</c:otherwise>
												</c:choose>
											</td>
											<td class="left"><c:out value="${h.historyMemo}"/></td>
											<td class="right">
												<span class="point-amount ${h.historyAmount gt 0 ? 'pos' : 'neg'}">
													<c:if test="${h.historyAmount gt 0}">+</c:if><fmt:formatNumber value="${h.historyAmount}"/>
												</span>
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
			</c:when>

			<c:when test="${tab == 'noti'}">
				<div class="member-panel">
					<div class="panel-actions">
						<form action="${cp}/notification/readAll" method="post">
							<button type="submit" class="btn btn-neutral">모두 읽음</button>
						</form>
					</div>

					<table class="table table-border table-hover">
						<thead>
							<tr>
								<th style="width:170px">시간</th>
								<th>내용</th>
								<th style="width:110px">상태</th>
								<th style="width:90px">관리</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
								<c:when test="${empty notiList}">
									<tr>
										<td colspan="4" class="center">알림이 없습니다</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:forEach var="n" items="${notiList}">
										<tr>
											<td class="center"><fmt:formatDate value="${n.notiWtime}" pattern="yy.MM.dd HH:mm"/></td>
											<td class="left">
												<c:url var="goUrl" value="${cp}/notification/go">
													<c:param name="notiNo" value="${n.notiNo}"/>
												</c:url>
												<a class="link" href="${goUrl}"><c:out value="${n.notiMessage}"/></a>
											</td>
											<td class="center">
												<c:choose>
													<c:when test="${n.notiRead eq 'N'}"><span class="badge badge-positive">새 알림</span></c:when>
													<c:otherwise><span class="badge badge-neutral">읽음</span></c:otherwise>
												</c:choose>
											</td>
										<td class="center">
											<form class="noti-delete-form" action="${cp}/notification/delete" method="post" onsubmit="return confirm('이 알림을 삭제할까요?');">
												<input type="hidden" name="notiNo" value="${n.notiNo}">
												<button type="submit" class="btn btn-neutral noti-delete-btn">삭제</button>
											</form>
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
			</c:when>

			<c:otherwise>
				<div class="member-panel">
					<form action="mypage" method="get" class="member-search" autocomplete="off">
						<input type="hidden" name="tab" value="${tab}">
						<select name="column" class="field">
							<option value="board_title" ${pageVO.column == 'board_title' ? 'selected' : ''}>제목</option>
						</select>
						<input type="search" name="keyword" class="field flex-fill" value="${pageVO.keyword}" placeholder="검색어를 입력해 주세요">
						<button type="submit" class="btn btn-neutral">검색</button>
						<c:if test="${pageVO.search}">
							<a class="btn btn-menu" href="mypage?tab=${tab}">초기화</a>
						</c:if>
					</form>

					<div class="panel-actions">
						<c:if test="${tab == 'write'}">
							<button type="button" class="btn btn-negative" id="btn-delete-selected">선택 삭제</button>
						</c:if>
					</div>

					<table class="table table-border table-hover">
						<thead>
							<tr>
								<c:if test="${tab == 'write'}">
									<th style="width:50px" class="center"><input type="checkbox" id="check-all"></th>
								</c:if>
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
										<td colspan="${tab == 'write' ? 6 : 5}" class="center">게시글이 없습니다</td>
									</tr>
								</c:when>
								<c:otherwise>
									<c:forEach var="board" items="${boardList}">
										<tr>
											<c:if test="${tab == 'write'}">
												<td class="center"><input type="checkbox" class="check-one" value="${board.boardNo}"></td>
											</c:if>
											<td class="center">${board.boardNo}</td>
											<td class="center"><c:out value="${board.categoryName}"/></td>
											<td class="left">
												<c:choose>
													<c:when test="${tab == 'write'}">
														<a class="link" href="${cp}/board/${board.categoryKey}/detail?boardNo=${board.boardNo}">
															<c:out value="${board.boardTitle}"/>
														</a>
													</c:when>
													<c:otherwise>
														<c:out value="${board.boardTitle}"/>
													</c:otherwise>
												</c:choose>
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
								<td colspan="${tab == 'write' ? 6 : 5}" class="center">
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
