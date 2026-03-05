// login.js - 로그인 화면 인터랙션
$(function(){
	$(".btn-login").on("click", function(){

		var memberId = $("[name=memberId]").val()
		var memberPw = $("[name=memberPw]").val()

		if(memberId.length == 0 || memberPw.length == 0){
			$("[name=memberId], [name=memberPw]").toggleClass("fail")
			return;
		}

		$.ajax({
			url : window.contextPath + "/rest/member/login",
			method : "post",
			data : {
				memberId : memberId,
				memberPw : memberPw
			},
			success : function(response){
				if(response){
					location.reload()
				} else {
					$("[name=memberId], [name=memberPw]").toggleClass("fail")
				}
			}
		})
	})
})
