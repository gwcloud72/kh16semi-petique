<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%-- [에러] all 화면 --%>

<c:set var="cp" value="${pageContext.request.contextPath}" scope="request"/>


<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>500 - 오류가 발생했습니다</title>
	<link rel="stylesheet" href="${cp}/css/error.css">
</head>
<body>
	<div class="error-page">
		<div class="error-card">
			<img class="error-image" src="${cp}/image/error/500.png" alt="500">
			<h1 class="error-title"><c:out value="${empty title ? '일시적인 오류가 발생했습니다' : title}"/></h1>
			<p class="error-desc">잠시 후 다시 시도해 주세요. 문제가 계속되면 관리자에게 문의해 주세요.</p>
			<div class="error-actions">
				<a href="<c:url value='/' />" class="btn-home">홈으로</a>
				<a href="javascript:location.reload();" class="btn-back">새로고침</a>
			</div>
		</div>
	</div>
</body>
</html>
