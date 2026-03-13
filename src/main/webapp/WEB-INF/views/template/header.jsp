<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%-- [템플릿] 공통 헤더 --%>

<c:set var="cp" value="${pageContext.request.contextPath}" scope="request"/>


<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title><c:out value="${empty pageTitle ? '3d Semi-Project' : pageTitle}"/></title>

	<link rel="stylesheet" type="text/css" href="${cp}/css/commons.css">
	<c:set var="uri" value="${pageContext.request.requestURI}"/>
	<c:set var="showSidebar" value="${not (fn:contains(uri, '/member/join') or fn:contains(uri, '/member/login') or fn:contains(uri, '/member/find') or fn:contains(uri, '/member/password') or fn:contains(uri, '/admin'))}" scope="request"/>
	<c:if test="${fn:contains(uri, '/admin')}">
		<link rel="stylesheet" type="text/css" href="${cp}/css/admin.css">
	</c:if>

	<c:if test="${not empty pageCss}">
		<c:forTokens items="${pageCss}" delims="," var="cssHref">
			<c:set var="href" value="${fn:trim(cssHref)}"/>
			<c:choose>
				<c:when test="${fn:startsWith(href, 'http') or fn:startsWith(href, '//')}">
					<link rel="stylesheet" type="text/css" href="${href}">
				</c:when>
				<c:otherwise>
					<link rel="stylesheet" type="text/css" href="${cp}${href}">
				</c:otherwise>
			</c:choose>
		</c:forTokens>
	</c:if>
	<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
	<script>
		window.contextPath = "${cp}";
	</script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/moment.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.30.1/locale/ko.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/twemoji@14.0.2/dist/twemoji.min.js" defer></script>
	<script src="${cp}/js/nav-menu.js" defer></script>
	<script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>

<c:choose>
	<c:when test="${showSidebar}">
		<c:set var="bodyClass" value="has-sidebar" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="bodyClass" value="no-sidebar" scope="request"/>
	</c:otherwise>
</c:choose>

<body class="${bodyClass}">
	<div class="container w-1600 site-shell">
		<header class="kh-header">
			<div class="kh-header__wrap">
				<div class="kh-top">
					<aside class="kh-brand me-20">
						<img src="${cp}/image/logo.png" alt="KH PETIQUE" class="kh-brand__logoImg">
					</aside>
					<nav class="kh-nav flex-box flex-center">
						<a class="btn btn-positive" href="${cp}/">HOME</a>
						<a class="btn btn-menu" href="${cp}/board/adoption/list">ADOPTION</a>
						<a class="btn btn-menu" href="${cp}/board/community/list">COMMUNITY</a>
						<a class="btn btn-menu" href="${cp}/board/animal/list">WIKI</a>
						<a class="btn btn-menu" href="${cp}/board/review/list">REVIEW</a>
						<div class="nav-menu">
							<button type="button" class="btn btn-menu nav-menu__btn" aria-haspopup="true" aria-expanded="false">MORE</button>
							<div class="nav-menu__panel" role="menu">
								<a class="nav-menu__item" href="${cp}/board/info/list" role="menuitem">INFO</a>
								<a class="nav-menu__item" href="${cp}/board/petfluencer/list" role="menuitem">PETFLUENCER</a>
								<a class="nav-menu__item" href="${cp}/board/fun/list" role="menuitem">FUN</a>
								<c:choose>
									<c:when test="${not empty sessionScope.loginId}">
								<a class="nav-menu__item" href="${cp}/member/mypage?tab=noti" role="menuitem">
									알림
									<c:if test="${not empty unreadNotiCount and unreadNotiCount gt 0}">
										<span class="nav-badge">${unreadNotiCount}</span>
									</c:if>
								</a>
										<a class="nav-menu__item" href="${cp}/member/mypage" role="menuitem">마이페이지</a>
										<a class="nav-menu__item" href="${cp}/animal/list" role="menuitem">동물 관리</a>
										<a class="nav-menu__item" href="${cp}/member/logout" role="menuitem">로그아웃</a>
									</c:when>
									<c:otherwise>
										<a class="nav-menu__item" href="${cp}/member/login" role="menuitem">로그인</a>
										<a class="nav-menu__item" href="${cp}/member/join" role="menuitem">회원가입</a>
									</c:otherwise>
								</c:choose>
								<c:if test="${sessionScope.loginLevel == 0}">
									<div class="nav-menu__divider"></div>
									<a class="nav-menu__item" href="${cp}/admin/category/list" role="menuitem">관리자</a>
								</c:if>
							</div>
						</div>
					</nav>
				</div>
			</div>
		</header>

		<div class="cell flex-box layout-root">
			<main class="flex-fill">
