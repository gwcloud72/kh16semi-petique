<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- [home.jsp] home 화면 --%>

<c:set var="pageTitle" value="KH PETIQUE" scope="request"/>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.css,/css/home.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/gh/hiphop5782/score@latest/score.js"></script>
<script type="text/javascript">
	$(function () {
		new Swiper(".swipers-petfluencer", {
			slidesPerView: 4.8,
			spaceBetween: 20,
			loop: true,
			speed: 800,
			autoplay: {
				delay: 5000,
				disableOnInteraction: false,
			},
			slidesPerGroup: 1,
		});

		new Swiper(".swipers-review", {
			slidesPerView: 3,
			spaceBetween: 20,
			loop: true,
		});

		$(".star-viewer").score({
			starColor: "#a67c52",
			editable: false,
			integerOnly: false,
			zeroAvailable: false,
		});
	});
</script>

<div class="home-page">
	<div class="cell flex-box home-hero">
		<div class="home-hero-left w-50p">
			<img class="home-hero-image" src="${cp}/image/petCafe.png" alt="메인 이미지">
			<div class="flex-fill">
				<div class="home-hero-title">반려동물 커뮤니티</div>
				<div class="home-hero-sub">분양, 정보, 후기까지 한 곳에서 확인하세요</div>
				<div class="home-hero-actions">
					<a class="btn btn-positive" href="${cp}/board/adoption/list">분양 게시판</a>
					<a class="btn btn-neutral" href="${cp}/board/community/list">커뮤니티</a>
				</div>
			</div>
		</div>

		<div class="home-hero-right w-50p">
			<a class="home-banner" href="${cp}/member/donation">
				<span class="home-banner-title">펫콩 쌓기</span>
				<span class="home-banner-sub">후원하고 포인트도 적립하세요</span>
			</a>
			<c:choose>
				<c:when test="${sessionScope.loginId != null}">
					<a class="home-banner secondary" href="${cp}/member/mypage">
						<span class="home-banner-title">내 정보</span>
						<span class="home-banner-sub">프로필/활동내역 확인</span>
					</a>
				</c:when>
				<c:otherwise>
					<a class="home-banner secondary" href="${cp}/member/login">
						<span class="home-banner-title">로그인</span>
						<span class="home-banner-sub">로그인 후 더 많은 기능을 이용하세요</span>
					</a>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<div class="section-title">
		<i class="fa-solid fa-comment-sms"></i>
		<span>커뮤니티</span>
	</div>
	<div class="home-grid home-grid-2">
		<c:forEach var="post" items="${community_board_list}">
			<a class="home-post" href="${cp}/board/community/detail?boardNo=${post.boardNo}">
				<div class="home-post-title"><c:out value="${post.boardTitle}"/></div>
				<div class="home-post-meta">
					<span class="home-post-writer"><c:out value="${post.boardWriter}"/></span>
					<span class="home-post-stats">
						<i class="fa-solid fa-eye"></i> ${post.boardView}
						<i class="fa-solid fa-comment-dots"></i> ${post.boardReply}
						<i class="fa-solid fa-heart"></i> ${post.boardLike}
					</span>
					<span class="home-post-time"><c:out value="${post.formattedWtime}"/></span>
				</div>
			</a>
		</c:forEach>
	</div>
	<div class="cell center">
		<a href="${cp}/board/community/list" class="link">커뮤니티 더보기 &gt;</a>
	</div>

	<div class="section-title">
		<span>펫플루언서</span>
	</div>
	<div class="cell ms-10 me-10 center">
		<div class="swiper swipers-petfluencer">
			<div class="swiper-wrapper">
				<c:forEach var="post" items="${petfluencer_board_list}">
					<div class="swiper-slide">
						<div class="card">
							<a href="${cp}/board/petfluencer/detail?boardNo=${post.boardNo}" class="link">
								<img src="${cp}/board/petfluencer/image?boardNo=${post.boardNo}" alt="이미지">
								<span class="overlay-btn" aria-hidden="true"><i class="fa fa-camera"></i></span>
								<div class="like-badge"><i class="fa fa-heart"></i> ${post.boardLike}</div>
								<div class="card-container">
									<div class="card-title"><c:out value="${post.boardTitle}"/></div>
									<div class="card-writer"><c:out value="${post.boardWriter}"/></div>
									<div class="card-info">
										<i class="fa fa-eye"></i> ${post.boardView}
										<i class="fa fa-comment"></i> ${post.boardReply}
									</div>
								</div>
							</a>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>
	<div class="cell ms-10 me-10 center">
		<a href="${cp}/board/petfluencer/list" class="link">펫 플루언서 더보기 &gt;</a>
	</div>

	<div class="section-title">
		<span>FUN</span>
	</div>
	<div class="home-grid home-grid-2">
		<c:forEach var="post" items="${fun_board_list}">
			<a class="home-thumb-post" href="${cp}/board/fun/detail?boardNo=${post.boardNo}">
				<img class="home-thumb" src="${cp}/board/fun/image?boardNo=${post.boardNo}" alt="이미지">
				<div class="home-thumb-body">
					<div class="home-post-title"><c:out value="${post.boardTitle}"/></div>
					<div class="home-post-meta">
						<span class="home-post-stats"><i class="fa-solid fa-eye"></i> ${post.boardView}</span>
						<span class="home-post-time"><c:out value="${post.formattedWtime}"/></span>
					</div>
				</div>
			</a>
		</c:forEach>
	</div>
	<div class="cell center">
		<a href="${cp}/board/fun/list" class="link">FUN 더보기 &gt;</a>
	</div>

	<div class="section-title">
		<span>사용후기</span>
	</div>
	<div class="cell ms-10 me-10 center">
		<div class="swiper swipers-review">
			<div class="swiper-wrapper">
				<c:forEach var="post" items="${review_board_scroll}">
					<div class="swiper-slide">
						<div class="card">
							<a href="${cp}/board/review/detail?boardNo=${post.boardNo}" class="link">
								<img src="${cp}/board/review/image?boardNo=${post.boardNo}" alt="이미지">
								<span class="overlay-btn" aria-hidden="true"><i class="fa fa-camera"></i></span>
								<div class="like-badge"><i class="fa fa-heart"></i> ${post.boardLike}</div>
								<div class="card-container">
									<div class="card-title"><c:out value="${post.boardTitle}"/></div>
									<div class="card-writer"><c:out value="${post.boardWriter}"/></div>
									<div class="card-info">
										<i class="fa fa-eye"></i> ${post.boardView}
										<i class="fa fa-comment"></i> ${post.boardReply}
									</div>
								</div>
							</a>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
	</div>

	<div class="home-grid home-grid-2">
		<c:forEach var="post" items="${review_board_list}">
			<a class="home-post" href="${cp}/board/review/detail?boardNo=${post.boardNo}">
				<div class="home-post-title"><c:out value="${post.boardTitle}"/></div>
				<div class="home-post-meta">
					<span class="home-post-writer"><c:out value="${post.boardWriter}"/></span>
					<span class="home-post-stats">
						<i class="fa-solid fa-eye"></i> ${post.boardView}
						<i class="fa-solid fa-comment-dots"></i> ${post.boardReply}
						<i class="fa-solid fa-heart"></i> ${post.boardLike}
					</span>
					<span class="home-post-time"><c:out value="${post.formattedWtime}"/></span>
				</div>
				<div class="star-viewer" data-max="5" data-rate="${post.boardScore}"></div>
			</a>
		</c:forEach>
	</div>
	<div class="cell center">
		<a href="${cp}/board/review/list" class="link">사용후기 더보기 &gt;</a>
	</div>

	<div class="section-title">
		<span>동물위키</span>
	</div>
	<div class="home-grid home-grid-3">
		<c:forEach var="post" items="${animal_wiki_board_list}">
			<a class="home-wiki" href="${cp}/board/animal/detail?boardNo=${post.boardNo}">
				<img class="home-wiki-thumb" src="${cp}/board/animal/image?boardNo=${post.boardNo}" alt="이미지">
				<div class="home-wiki-title"><c:out value="${post.boardTitle}"/></div>
				<div class="home-wiki-meta">
					<i class="fa-solid fa-eye"></i> ${post.boardView}
					<span class="home-wiki-time"><c:out value="${post.formattedWtime}"/></span>
				</div>
			</a>
		</c:forEach>
	</div>
	<div class="cell center">
		<a href="${cp}/board/animal/list" class="link">동물위키 더보기 &gt;</a>
	</div>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
