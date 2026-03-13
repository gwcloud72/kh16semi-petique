// add-animal.js - 동물 등록 폼 UI 스크립트
$(function(){
	const base = window.contextPath || "";

	$(".btn-add-animal").on("click", function(){
		const origin = $("#animal-template").text();
		const html = $.parseHTML(origin);
		$(".target").append(html);
	});

	$(document).on("click", ".btn-animal", function(){
		const wrapper = $(this).closest(".animal-wrapper");
		const animalButton = wrapper.find(".btn-animal");
		const permission = animalButton.attr("data-permission");
		if(permission === "f") {
			animalButton.attr("data-permission", "t");
			animalButton.find("span").text("분양가능");
		}
		else {
			animalButton.attr("data-permission", "f");
			animalButton.find("span").text("분양불가");
		}
	});

	$(document).on("click", ".animal-cancel", function(){
		const wrapper = $(this).closest(".animal-wrapper");
		const animalNo = wrapper.attr("data-animal-no");
		if(animalNo === "new") {
			wrapper.remove();
			return;
		}
		$.ajax({
			url: base + "/rest/animal/delete",
			method: "post",
			data: { animalNo: animalNo },
			success: function(){
				wrapper.remove();
			}
		});
	});

	$(document).on("click", ".animal-access", function(){
		const wrapper = $(this).closest(".animal-wrapper");
		const animalName = wrapper.find(".animal-name").val();
		const animalPermission = wrapper.find(".btn-animal").attr("data-permission");
		const animalContent = wrapper.find(".animal-content").val();
		const animalNo = wrapper.attr("data-animal-no");
		const files = wrapper.find("[name=media]").prop("files");

		const form = new FormData();
		if(files && files.length !== 0) {
			form.append("media", files[0]);
		}
		form.append("animalName", animalName);
		form.append("animalPermission", animalPermission);
		form.append("animalContent", animalContent);

		if(animalNo === "new") {
			$.ajax({
				processData: false,
				contentType: false,
				url: base + "/rest/animal/add",
				method: "post",
				data: form,
				success: function(response){
					wrapper.attr("data-animal-no", response);
					wrapper.find(".animal-access").toggleClass("d-none");
					wrapper.find(".animal-edit").toggleClass("d-none");
					wrapper.find(".animal-name").attr("readonly", "readonly");
					wrapper.find(".animal-content").attr("readonly", "readonly");
					wrapper.find(".btn-animal").attr("disabled", "disabled");
					wrapper.find("[name=media]").prop("type", "hidden");
				}
			});
		}
		else {
			form.append("animalNo", animalNo);
			$.ajax({
				processData: false,
				contentType: false,
				url: base + "/rest/animal/edit",
				method: "post",
				data: form,
				success: function(){
					wrapper.find(".animal-access").toggleClass("d-none");
					wrapper.find(".animal-edit").toggleClass("d-none");
					wrapper.find(".animal-name").attr("readonly", "readonly");
					wrapper.find(".animal-content").attr("readonly", "readonly");
					wrapper.find(".btn-animal").attr("disabled", "disabled");
					wrapper.find("[name=media]").prop("type", "hidden");
				}
			});
		}
	});

	$(document).on("click", ".animal-edit", function() {
		const wrapper = $(this).closest(".animal-wrapper");
		wrapper.find(".animal-access").toggleClass("d-none");
		wrapper.find(".animal-edit").toggleClass("d-none");
		wrapper.find(".animal-name").removeAttr("readonly");
		wrapper.find(".animal-content").removeAttr("readonly");
		wrapper.find(".btn-animal").removeAttr("disabled");
		wrapper.find("[name=media]").prop("type", "file");
	});
});
