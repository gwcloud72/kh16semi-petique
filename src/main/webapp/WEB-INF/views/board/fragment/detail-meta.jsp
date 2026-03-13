<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>


<%-- [게시판 공통] 화면 --%>

<div class="board-meta">
	<c:url var="writerProfileUrl" value="/member/detail">
		<c:param name="memberNickname" value="${post.memberNickname}"/>
	</c:url>
	<table>
		<tbody>
			<tr>
				<th>작성자</th>
				<td>
					<a class="profile-link" href="${writerProfileUrl}"><c:out value="${post.memberNickname}"/></a>
					<c:if test="${not empty post.badgeImage}">${post.badgeImage}</c:if>
					<c:if test="${not empty post.levelName}">
						<span class="level-badge">${post.levelName}</span>
					</c:if>
				</td>
				<th>조회</th>
				<td>${post.boardView}</td>
			</tr>
			<tr>
				<th>작성일</th>
				<td><fmt:formatDate value="${post.boardWtime}" pattern="yyyy-MM-dd HH:mm"/></td>
				<th>수정일</th>
				<td>
					<c:choose>
						<c:when test="${not empty post.boardEtime}">
							<fmt:formatDate value="${post.boardEtime}" pattern="yyyy-MM-dd HH:mm"/>
						</c:when>
						<c:otherwise>-</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${showAnimalHeader and not empty post.animalHeaderName}">
				<tr>
					<th>동물 분류</th>
					<td colspan="3">${post.animalHeaderName}</td>
				</tr>
			</c:if>
			<c:if test="${showTypeHeader and not empty post.typeHeaderName}">
				<tr>
					<th>말머리</th>
					<td colspan="3">${post.typeHeaderName}</td>
				</tr>
			</c:if>
			<c:if test="${post.boardScore > 0}">
				<tr>
					<th>평점</th>
					<td colspan="3">
						<span class="board-score"><i class="fa-solid fa-star"></i> ${post.boardScore}</span>
					</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</div>
