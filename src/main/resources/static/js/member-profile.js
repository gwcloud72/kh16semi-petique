// member-profile.js - 회원 프로필 화면 인터랙션
$(function(){
	var origin = $(".image-profile").attr("src");
	$("#profile-input").on("input", function(){
		var list = $("#profile-input").prop("files")
		if(list.length == 0) return;

		var form = new FormData();
		form.append("media", list[0]);
		$.ajax({
			processData : false,
			contentType : false,
			url : window.contextPath + "/rest/member/profile",
			method : "post",
			data : form,
			success : function(response){
				var new_origin = origin + "&t=" + new Date().getTime();
				$(".image-profile").attr("src", new_origin);
			}
		});
	});
	$("#profile-delete").on("click", function(){
		$.ajax({
			url : window.contextPath + "/rest/member/delete",
			method : "post",
			data : {},
			success : function(response) {
				$(".image-profile").attr("src", window.contextPath + "/image/error/no-image.png")
			}
		})
	});
});
