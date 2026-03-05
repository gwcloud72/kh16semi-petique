<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>


<%-- [게시판 공통] 화면 --%>

<c:if test="${not empty noticeList}">
	<div class="notice-panel">
		<div class="notice-panel__head">
			<span class="notice-badge">공지</span>
			<span class="notice-panel__title">공지사항</span>
		</div>
		<ul class="notice-panel__list">
			<c:forEach var="n" items="${noticeList}">
				<li class="notice-panel__item">
					<a class="notice-panel__link" href="detail?boardNo=${n.boardNo}">
						<span class="notice-pin">📌</span>
						<span class="notice-text">${n.boardTitle}</span>
					</a>
					<span class="notice-meta">
						<c:choose>
							<c:when test="${not empty n.memberNickname}">
								<c:url var="noticeWriterUrl" value="/member/detail">
									<c:param name="memberNickname" value="${n.memberNickname}"/>
								</c:url>
								<a class="profile-link" href="${noticeWriterUrl}"><c:out value="${n.memberNickname}"/></a>
							</c:when>
							<c:otherwise>
								<c:url var="noticeWriterUrl" value="/member/detail">
									<c:param name="memberId" value="${n.boardWriter}"/>
								</c:url>
								<a class="profile-link" href="${noticeWriterUrl}"><c:out value="${n.boardWriter}"/></a>
							</c:otherwise>
						</c:choose>
						<span class="notice-dot">·</span>
						<fmt:formatDate value="${n.boardWtime}" pattern="yy.MM.dd"/>
					</span>
				</li>
			</c:forEach>
		</ul>
	</div>
</c:if>
