// find.js - 아이디/비밀번호 찾기 이메일 전송 처리
$(function () {

	$(".btn-find-send").on("click", function (e) {
		e.preventDefault();

		var email = $.trim($("[name=memberEmail]").val());
		if (!email) {
			alert("이메일을 입력해 주세요.");
			$("[name=memberEmail]").focus();
			return;
		}

		$.ajax({
			url: window.contextPath + "/rest/member/findSend",
			method: "post",
			data: { memberEmail: email },
			beforeSend: function () {
				$(".btn-find-send").prop("disabled", true);
				$(".btn-find-send").find("i")
					.removeClass("fa-paper-plane")
					.addClass("fa-spinner fa-spin");
				$(".btn-find-send").find("span").text("이메일 발송중");
			},
			success: function (response) {
				if (response) {
					$("[name=memberEmail]").removeClass("success fail fail2").addClass("success");
					$("#send-email").submit();
					return;
				}

				$("[name=memberEmail]").removeClass("success fail fail2").addClass("fail2");
				alert("해당 이메일이 등록되어 있지 않습니다.");
			},
			error: function () {
				alert("요청 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
			},
			complete: function () {
				$(".btn-find-send").prop("disabled", false);
				$(".btn-find-send").find("i")
					.removeClass("fa-spinner fa-spin")
					.addClass("fa-paper-plane");
				$(".btn-find-send").find("span").text("이메일 전송");
			}
		});
	});

});
