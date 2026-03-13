<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<%-- [회원] 후원/펫콩 화면 --%>

<c:set var="pageCss" value="/css/donation.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<div class="container mt-20 donation-container">
	<div id="reward-toast" class="reward-toast d-none"></div>

	<div class="cell point-zone">
		<div class="point-label">보유 펫콩</div>
		<div class="point-value">
			<i class="fa-solid fa-bone me-5"></i>
			<span class="point-number">
				<fmt:formatNumber value="${memberDto.memberPoint}" pattern="###,###"/>
			</span>
			<button type="button" class="point-use">펫콩 기부하기</button>
		</div>
	</div>

	<div class="cell invite-card flex-box">
		<div class="invite-left flex-box">
			<i class="fa-solid fa-user-group me-10 invite-icon"></i>
			<div class="invite-text">
				<div class="invite-title">친구초대</div>
				<div class="invite-desc">초대 링크로 가입하면 1,000p 지급</div>
			</div>
		</div>
		<div class="invite-right">
			<button class="btn btn-neutral btn-copy bbt" type="button">링크 복사</button>
		</div>
	</div>

	<div class="cell mission-card">
		<div class="mission-head flex-box">
			<div class="mission-title">
				<i class="fa-solid fa-flag me-10 mission-flag"></i>
				펫콩 미션
			</div>
		</div>

		<div class="mission-row" data-reward-type="댓글">
			<div class="mission-icon"><i class="fa-solid fa-comment-dots"></i></div>
			<div class="mission-badge">[일일]</div>
			<div class="mission-link"><a href="${cp}/board/community/list">댓글 쓰기</a></div>
			<div class="mission-reward">
				<span class="reward-label">1회당 보상</span>
				<i class="fa-solid fa-bone ms-5"></i>
				<span class="reward-value">20</span>
			</div>
		</div>

		<div class="mission-row" data-reward-type="펫플루언서">
			<div class="mission-icon"><i class="fa-solid fa-pen-to-square"></i></div>
			<div class="mission-badge">[일일]</div>
			<div class="mission-link"><a href="${cp}/board/petfluencer/write">펫플루언서 글쓰기</a></div>
			<div class="mission-reward">
				<span class="reward-label">1회당 보상</span>
				<i class="fa-solid fa-bone ms-5"></i>
				<span class="reward-value">50</span>
			</div>
		</div>

		<div class="mission-row" data-reward-type="자유게시판">
			<div class="mission-icon"><i class="fa-solid fa-pen-to-square"></i></div>
			<div class="mission-badge">[일일]</div>
			<div class="mission-link"><a href="${cp}/board/community/write">자유게시판 글쓰기</a></div>
			<div class="mission-reward">
				<span class="reward-label">1회당 보상</span>
				<i class="fa-solid fa-bone ms-5"></i>
				<span class="reward-value">50</span>
			</div>
		</div>

		<div class="mission-row" data-reward-type="분양게시판">
			<div class="mission-icon"><i class="fa-solid fa-pen-to-square"></i></div>
			<div class="mission-badge">[일일]</div>
			<div class="mission-link"><a href="${cp}/board/adoption/write">분양게시판 글쓰기</a></div>
			<div class="mission-reward">
				<span class="reward-label">1회당 보상</span>
				<i class="fa-solid fa-bone ms-5"></i>
				<span class="reward-value">60</span>
			</div>
		</div>

		<div class="mission-row" data-reward-type="정보게시판">
			<div class="mission-icon"><i class="fa-solid fa-pen-to-square"></i></div>
			<div class="mission-badge">[일일]</div>
			<div class="mission-link"><a href="${cp}/board/info/write">정보게시판 글쓰기</a></div>
			<div class="mission-reward">
				<span class="reward-label">1회당 보상</span>
				<i class="fa-solid fa-bone ms-5"></i>
				<span class="reward-value">70</span>
			</div>
		</div>
	</div>

	<div class="cell donate-note">
		<div class="note-title">기부 안내</div>
		<div class="note-text">펫콩은 한국유기동물복지협회로 기부됩니다.</div>
		<div class="note-text"><a class="note-link" href="https://animalwa.org/" target="_blank" rel="noopener noreferrer">animalwa.org</a></div>
	</div>
</div>

<script type="text/javascript">
	$(function(){
		const base = window.contextPath || "";
		const point = Number("${memberDto.memberPoint}" || 0);

		$(".bbt").on("click", function(){
			const inviteUrl = window.location.origin + base + "/member/join";
			if(!navigator.clipboard) {
				prompt("아래 링크를 복사하세요", inviteUrl);
				return;
			}
			navigator.clipboard.writeText(inviteUrl)
				.then(function(){
					alert("복사가 완료되었습니다!");
				})
				.catch(function(){
					prompt("아래 링크를 복사하세요", inviteUrl);
				});
		});

		$(".point-use").on("click", function(){
			if(point <= 0) {
				alert("아직 펫콩이 부족해요. 다음에 따뜻한 마음을 전해볼까요? 🐶");
				return;
			}
			if(!confirm("한국유기동물복지협회(https://animalwa.org/)로 기부하시겠습니까?")){
				return;
			}

			$.ajax({
				url: base + "/member/usePoint",
				method: "get",
				success: function(response){
					if(response === "success") {
						alert("기부가 완료되었습니다.");
						location.reload();
					}
					else {
						alert("기부 처리 중 오류가 발생했습니다.");
					}
				},
				error: function(){
					alert("기부 처리 중 오류가 발생했습니다.");
				}
			});
		});

		const rewardType = "${rewardType}";
		if(rewardType) {
			const row = $(".mission-row[data-reward-type='" + rewardType + "']");
			if(row.length) {
				row.addClass("is-highlight");
				$("#reward-toast").removeClass("d-none").text(rewardType + " 보상이 지급되었습니다.");
			}
		}
	});
</script>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
