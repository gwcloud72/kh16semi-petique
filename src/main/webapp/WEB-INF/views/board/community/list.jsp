<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [커뮤니티] 목록 화면 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="pageCss" value="/css/board_list.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container w-1200 board-page">

	<div class="cell center">
		<h1>${category.categoryName}</h1>
	</div>
	<jsp:include page="/WEB-INF/views/board/fragment/notice-top.jsp"></jsp:include>

	<div class="cell center mt-30 mb-50">
		<form action="list">
			<div class="search-bar">
				<select name="column">
					<option value="board_title" ${pageVO.column == 'board_title' ? 'selected' : ''}>제목</option>
					<option value="board_writer" ${pageVO.column == 'board_writer' ? 'selected' : ''}>아이디</option>
					<option value="member_nickname" ${pageVO.column == 'member_nickname' ? 'selected' : ''}>닉네임</option>
					<option value="header_name" ${pageVO.column == 'header_name' ? 'selected' : ''}>분류</option>

				</select> <input type="text" name="keyword" value="${pageVO.keyword}"
					required placeholder="검색어 입력">

				<button type="submit" class="btn btn-positive">검색</button>
			</div>
		</form>

				<div class="board-actions">
			<div class="board-sort">
				<a href="list?orderBy=wtime" class="btn ${orderBy eq 'wtime' ? 'btn-positive' : 'btn-neutral'}">최신순</a>
				<a href="list?orderBy=view" class="btn ${orderBy eq 'view' ? 'btn-positive' : 'btn-neutral'}">조회순</a>
				<a href="list?orderBy=like" class="btn ${orderBy eq 'like' ? 'btn-positive' : 'btn-neutral'}">추천순</a>
			</div>
			<div class="board-write">
				<c:choose>
					<c:when test="${sessionScope.loginId != null}">
						<a href="write" class="btn btn-positive">글쓰기</a>
					</c:when>
					<c:otherwise>
						<span class="board-login-hint"><a href="${cp}/member/login" class="link">로그인</a>을 해야 글을 작성할 수 있습니다</span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>

	<c:choose>
		<c:when test="${empty boardList}">
			<div class="no-posts">등록된 글이 없습니다.</div>
		</c:when>
		<c:otherwise>
			<div class="cell">
				<table class="table table-border table-hover w-100p">
<tbody>
<c:forEach var="boardDetailVO" items="${boardList}" varStatus="st">
						        <tr>
							        <td>
							        		<div class="cell flex-box">
										        <img src="${cp}/board/community/image?boardNo=${boardDetailVO.boardNo}" class="left thumb-60" >
										        <div class="community-container ms-10">
											        	<div class="top-container flex-box">
											        		 <div class="comminity-title">
												        		 <a href="detail?boardNo=${boardDetailVO.boardNo}">
												        		 ${boardDetailVO.boardTitle}
												        		 </a>
											        		 </div>

											        		 <div class="comminity-reply red">[${boardDetailVO.boardReply}]</div>
											        	</div>
											        	<div class="bottom-container flex-box">
											        		<img class="avatar-24">
											        		<div class="ms-10 comminity-writer">
						        				<a class="profile-link" href="${cp}/member/detail?memberNickname=${boardDetailVO.memberNickname }">
											        			[${boardDetailVO.memberNickname}]
											        		</a>
											        		</div>
											        		<div class="ms-10 comminity-wtime">${boardDetailVO.formattedWtime}</div>
											        		<i class="ms-10 fa fa-eye"></i> ${boardDetailVO.boardView}
											                <i class="ms-10 fa-regular fa-heart"></i> ${boardDetailVO.boardLike}
											        	</div>
										        </div>
									    	</div>
							        </td>
							    </tr>
						    </c:forEach>
</tbody>
					<tfoot>
						<tr>
							<td colspan="1">
								검색결과 :
								${pageVO.begin} - ${pageVO.end}
								/
								${pageVO.dataCount}개
							</td>
						</tr>

						<tr>
					        <td colspan="1" class="center">
					            <jsp:include page="/WEB-INF/views/template/pagination.jsp"></jsp:include>
					        </td>
					    </tr>
					</tfoot>
				</table>
			</div>
		</c:otherwise>
	</c:choose>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
