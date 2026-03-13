// member-check.js - 회원가입 입력 검증 및 이메일 인증 흐름
var state = {
	memberIdValid : false,
	memberPwValid : false,
	memberNicknameValid : false,
	memberEmailValid: true,
	ok : function (){
		return this.memberIdValid && this.memberPwValid && this.memberNicknameValid && this.memberEmailValid;
	}
};

$(function () {
	var authBtn = $("[name=memberAuth]");
	if(authBtn.val() == "f"){
		$("[name=memberEmail]").removeAttr("readonly");
		$(".btn-cert-send").removeClass("d-none");
		$(".auth-btn").removeClass("bggreen bgred").addClass("bgred");
	}
	else {
		$(".auth-edit-btn").removeClass("d-none");
		$(".auth-btn").removeClass("bggreen bgred").addClass("bggreen");
	}

	$(".auth-edit-btn").on("click", function () {
		$("[name=memberEmail]").removeAttr("readonly").focus();
		$(".auth-edit-btn").addClass("d-none");
		$(".btn-cert-send").removeClass("d-none");
	});

	$("[name=memberId]").on("blur", function () {
		var idVal = $(this).val();
		var regex = /^[A-Za-z][A-Za-z0-9]{1,19}$/;

		if (!regex.test(idVal) || idVal.length == 0) {
			$(this).removeClass("success fail fail2").addClass("fail");
			state.memberIdValid = false;
			return;
		}

		$.ajax({
			url: window.contextPath + "/rest/member/checkId",
			method: "post",
			data: {memberId: idVal},
			success: function(response) {
				if(response) {
					$("[name=memberId]").removeClass("success fail fail2").addClass("fail2");
					state.memberIdValid = false;
					return;
				}
				$("[name=memberId]").removeClass("success fail fail2").addClass("success");
				state.memberIdValid = true;
			}
		});
	});

	$("[name=memberPw]").on("blur", function () {
		var pwVal = $(this).val();
		var regexAll = /^[A-Za-z0-9!@#$]{8,20}$/;
		var regex1 = /[A-Za-z]+/;
		var regex2 = /[0-9]+/;
		var regex3 = /[!@#$]+/;
		var valid = regexAll.test(pwVal)
					&& regex1.test(pwVal)
					&& regex2.test(pwVal)
					&& regex3.test(pwVal);
		if(!valid || pwVal.length == 0) {
			$(this).removeClass("success fail fail2").addClass("fail");
			state.memberPwValid = false;
			return;
		}
		$(this).removeClass("success fail fail2").addClass("success");
		state.memberPwValid = true;
	});

	$(".fa-eye-slash, .fa-eye").on("click", function(){
		$(".fa-eye-slash, .fa-eye").toggleClass("d-none");
		var input =$(this).closest(".cell").find("input");
		if(input.attr("type") === "password"){
			input.attr("type", "text");
		}
		else {
			input.attr("type", "password");
		}
	});

	$("[name=memberNickname]").on("blur", function(){
		var nickVal = $(this).val();
		var regex = /^(?! )[A-Za-z0-9가-힣 ]{3,16}(?<! )$/;
		var valid = regex.test(nickVal);
		if(valid == false || nickVal.trim().length === 0){
			$(this).removeClass("success fail fail2").addClass("fail");
			state.memberNicknameValid = false;
			return;
		}
		$.ajax({
			url : window.contextPath + "/rest/member/checkNickname",
			method : "post",
			data : {memberNickname : nickVal},
			success : function(response){
				if(response){
					$("[name=memberNickname]").removeClass("success fail fail2").addClass("fail2");
					state.memberNicknameValid = false;
					return;
				}
				$("[name=memberNickname]").removeClass("success fail fail2").addClass("success");
				state.memberNicknameValid = true;
			}
		});
	});

	$(".btn-cert-send").on("click", function () {
		var email = $("[name=memberEmail]").val();
		var regex = /^(.*?)@(.*?)$/;
		var valid = regex.test(email);

		$(".cell.flex-box .auth-btn").addClass("d-none");

		if (valid == false) {
			$("[name=memberEmail]").removeClass("success fail fail2").addClass("fail");
			state.memberEmailValid = false;
			return;
		}

		$.ajax({
			url: window.contextPath + "/rest/member/certSend",
			method: "post",
			data: { certEmail: email },
			success: function (response) {
				if(response){
					$(".cert-input-area").removeClass("d-none");
					$("[name=memberEmail]").removeClass("success fail fail2").addClass("success");
					return;
				}
				$("[name=memberEmail]").removeClass("success fail fail2").addClass("fail2");
			},
			beforeSend:function(){
				$(".btn-cert-send").prop("disabled", true);
				$(".btn-cert-send").find("i").removeClass("fa-paper-plane").addClass("fa-spinner fa-spin");
				$(".btn-cert-send").find("span").text("인증메일 발송중");
			},
			complete:function(){
				$(".btn-cert-send").prop("disabled", false);
				$(".btn-cert-send").find("i").removeClass("fa-spinner fa-spin").addClass("fa-paper-plane");
				$(".btn-cert-send").find("span").text("인증메일 전송");
			}
		});
	});

	$(".btn-cert-check").on("click", function () {
		var certNumber = $(".cert-input").val();
		var regex = /^[0-9]{5}$/;
		var valid = regex.test(certNumber);

		if (valid == false) {
			$(".cert-input").removeClass("success fail fail2").addClass("fail");
			state.memberEmailValid = false;
			return;
		}

		var certEmail = $("[name=memberEmail]").val();
		$.ajax({
			url: window.contextPath + "/rest/member/certCheck",
			method: "post",
			data: { certEmail: certEmail, certNumber: certNumber },
			success: function (response) {
				if (response) {
					$(".cert-input").removeClass("success fail fail2").val("").addClass("d-none");
					$("[name=memberEmail]").removeClass("success fail fail2").addClass("success").prop("readonly", true);
					state.memberEmailValid = true;
					$(".btn-cert-check").addClass("d-none");
					$(".auth-btn").removeClass("d-none");
					$("[name=memberAuth]").val("t");
					$(".auth-btn").removeClass("bgred").addClass("bggreen");
				}
				else {
					$(".cert-input").removeClass("success fail fail2").addClass("fail");
					state.memberEmailValid = false;
				}
			}
		});
	});

	$(".check-form").on("submit", function(){
		if(state.ok() == false){
			alert("입력 항목에 오류가 존재합니다.");
			return false;
		}
		return true;
	});

	$("[name=media]").on("input", function () {
		var originUrl = $(".img-preview").prop("src");
		if(originUrl.startsWith("blob:")){
			URL.revokeObjectURL(originUrl);
		}

		if(this.files.length == 0) {
			$(".img-preview").prop("src", window.contextPath + "/image/error/no-image.png");
		}
		else {
			var imageUrl = URL.createObjectURL(this.files[0]);
			$(".img-preview").prop("src", imageUrl);
		}
	});
});
