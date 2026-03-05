// nickname-check.js - 닉네임 중복/형식 체크
var state = {
	memberNicknameValid : false,
	ok : function (){
		return this.memberNicknameValid
	},
};

$(function(){

	$("[name=memberNickname]").on("blur", function(){
		var nickVal = $(this).val();
		var regex = /^(?! )[A-Za-z0-9가-힣 ]{3,16}(?<! )$/;
		var valid = regex.test(nickVal)
		if(valid == false || nickVal.trim().length === 0){
			$(this).removeClass("success fail fail2").addClass("fail");
			state.memberNicknameValid = false
			return;
		}
		var memberId = $("[name=memberId]").val()
		$.ajax({
			url : window.contextPath + "/rest/member/checkDuplication",
			method : "post",
			data : {
				memberId : memberId,
				memberNickname : nickVal
			},
			success : function(response){
				if(response){
					$("[name=memberNickname]").removeClass("success fail fail2").addClass("fail2");
					state.memberNicknameValid = false
					return
				}
				$("[name=memberNickname]").removeClass("success fail fail2").addClass("success");
				state.memberNicknameValid = true
			}
		})
	})

	$(".check-form").on("submit", function(){
			if(state.ok() == false){
				alert("입력 항목에 오류가 존재합니다.")
				return false
			}
			return true
		})
})
