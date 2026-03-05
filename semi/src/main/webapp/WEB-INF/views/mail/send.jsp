<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- [메일] send 템플릿 --%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<c:set var="pageCss" value="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.css,/summernote/custom-summernote.css,/css/board_write.css" scope="request"/>
<jsp:include page="/WEB-INF/views/template/header.jsp"></jsp:include>


<script src="https://cdn.jsdelivr.net/npm/summernote@0.9.0/dist/summernote-lite.min.js"></script>

<script src="${cp}/summernote/custom-summernote.js"></script>

<script type="text/javascript">

	$(function(){
		var state = {
				checkMemberNicknameValid : false,
				ok : function () {
					return this.checkMemberNicknameValid;
				}
		};

		$(".check-memberNickname").on("click", function () {
			$.ajax({
				url : window.contextPath + "/rest/mail/checkMember",
				method : "post",
				data : {memberNickname : $("[name=memberNickname]").val()},
				success : function (response) {
					switch(response){
						case -1 :
							state.checkMemberNicknameValid = false;
							$("[name=memberNickname]").removeClass("success fail fail2").addClass("fail2")
							return;
						case 0 :
							state.checkMemberNicknameValid = false;
							$("[name=memberNickname]").removeClass("success fail fail2").addClass("fail")
							return;
						case 1 :
							state.checkMemberNicknameValid = true;
							$("[name=memberNickname]").removeClass("success fail fail2").addClass("success")
							return;
						default :
					}
				}
			})
		})

		$(".check-send").on("submit", function () {
			if(state.ok() == false) {
				alert("우편을 받을 회원 정보가 존재하지 않습니다.");
				return false;
			}
			return true;
		})
	})

</script>

<div class="container w-600">
	<form action="send" method="post" enctype="multipart/form-data" autocomplete="off" class="check-send">
		<div class="cell">
			<label>
				<span>제목</span>
				<i class="fa-solid fa-asterisk red"></i>
			</label>
			<input type="text" class="field w-100p" name="mailTitle">
		</div>
		<div class="cell">
			<label>
				<span>받는 이</span>
				<i class="fa-solid fa-asterisk red"></i>
			</label>
			<div class="flex-box flex-wrap">
				<input type="text" class="field flex-fill" name="memberNickname">
				<button type="button" class="btn btn-neutral check-memberNickname">
					<i class="fa-solid fa-magnifying-glass"></i>
					<span>확인</span>
				</button>
				<div class="success-feedback w-100p" >확인완료</div>
				<div class="fail-feedback w-100p">회원이 존재하지않습니다.</div>
				<div class="fail2-feedback w-100p">본인에게 보낼 수 없습니다.</div>
			</div>
		</div>
		<div class="cell">
			<label>
				<span>내용</span>
				<i class="fa-solid fa-asterisk red"></i>
			</label>
			<textarea name="mailContent" class="summernote-editor"></textarea>
			<div id="total-char" data-maxLength="200"></div>
		</div>
		<div class="cell center">
			<button type="submit" class="btn btn-positive w-50p">
				<i class="fa-solid fa-paper-plane"></i>
				<span>우편 보내기</span>
			</button>
		</div>
	</form>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"></jsp:include>
