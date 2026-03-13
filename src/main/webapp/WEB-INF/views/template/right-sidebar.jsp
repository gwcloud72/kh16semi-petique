<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>


<%-- [템플릿] right-sidebar --%>

<c:if test="${showSidebar}">

<script src="${cp}/js/login.js"></script>

<script type="text/template" id="ranking-template">
	<div class="cell w-100p flex-box ranking-wrapper">
		<span class="number me-10">1</span>
		<img src="${cp}/image/error/no-image.png" class="ranking-profile ms-10" onerror="this.onerror=null;this.src='${cp}/image/error/no-image.png';">
		<div class="ranking-info flex-box flex-vertical">
			<a class="ranking-nickname left link" href="#">닉네임</a>
			<span class="ranking-description text-ellipsis left">설명</span>
		</div>
		<div class="ranking-heart flex-box">
			<i class="fa-solid fa-heart red"></i>
			<span class="ranking-member-point">100</span>
		</div>
	</div>
</script>

<script type="text/template" id="new-board-template">
	<div class="cell w-100p new-board-wrapper flex-box">
		<div class="left flex-fill">
			<a class="new-board-title link" href="#">제목</a>
		</div>
		<div class="right">
			<span class="new-board-time">시간</span>
		</div>
	</div>
</script>

<script src="${cp}/js/sidebar.js"></script>

	<aside class="cell flex-box flex-vertical right-sidebar">
		<div class="sidebar-card">
			<c:choose>
				<c:when test="${sessionScope.loginId != null}">
					<div class="sidebar-profile">
						<img class="sidebar-profile__img" src="${cp}/member/profile?member_id=${sessionScope.loginId}" width="54" height="54">
						<div class="sidebar-profile__meta">
							<div class="sidebar-profile__name">${sidebarInfo.memberNickname}</div>
							<div class="sidebar-profile__points">
								<span>보유 <strong>${sidebarInfo.memberPoint}</strong></span>
								<span class="sidebar-dot">•</span>
								<span>사용 <strong>${sidebarInfo.memberUsedPoint}</strong></span>
							</div>
						</div>
					</div>
					<div class="sidebar-actions">
						<a href="${cp}/mail/list/receive" class="btn btn-neutral w-100p">우편함</a>
						<a href="${cp}/member/mypage?tab=noti" class="btn btn-neutral w-100p sidebar-badge-btn">
							<span>알림</span>
							<c:if test="${not empty unreadNotiCount and unreadNotiCount gt 0}">
								<span class="nav-badge">
									<c:choose>
										<c:when test="${unreadNotiCount gt 9}">9+</c:when>
										<c:otherwise>${unreadNotiCount}</c:otherwise>
									</c:choose>
								</span>
							</c:if>
						</a>
						<div class="flex-box gap-10">
							<a href="${cp}/member/mypage" class="btn btn-menu flex-fill">내 정보</a>
							<a href="${cp}/member/logout" class="btn btn-neutral flex-fill">로그아웃</a>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<form id="login-form" method="post" class="sidebar-login">
						<div class="sidebar-login__row">
							<label class="sidebar-login__label">아이디</label>
							<input class="field w-100p" type="text" name="memberId">
							<div class="fail-feedback"></div>
						</div>
						<div class="sidebar-login__row">
							<label class="sidebar-login__label">비밀번호</label>
							<input class="field w-100p" type="password" name="memberPw">
							<div class="fail-feedback"></div>
						</div>
						<div class="sidebar-login__row">
							<button class="btn btn-positive btn-login w-100p" type="submit">로그인</button>
						</div>
						<div class="sidebar-login__links">
							<a href="${cp}/member/findId" class="link gray">아이디 찾기</a>
							<span class="gray">|</span>
							<a href="${cp}/member/findPw" class="link gray">비밀번호 찾기</a>
							<span class="gray">|</span>
							<a class="link gray" href="${cp}/member/join">회원가입</a>
						</div>
					</form>
				</c:otherwise>
			</c:choose>
		</div>

		<div class="sidebar-card">
			<div class="sidebar-tabs" role="tablist">
				<button type="button" class="sidebar-tab on" data-tab="new" role="tab" aria-selected="true">새글</button>
				<button type="button" class="sidebar-tab" data-tab="rank" role="tab" aria-selected="false">랭킹</button>
			</div>
			<div class="sidebar-panels">
				<div class="sidebar-panel" data-panel="new">
					<div class="new-board-list-wrapper"></div>
				</div>
				<div class="sidebar-panel d-none" data-panel="rank">
					<div class="ranking-list-wrapper"></div>
				</div>
			</div>
		</div>
	</aside>
</c:if>
