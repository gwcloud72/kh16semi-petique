// adoption-list.js - 분양 리스트 탭/필터 UI 스크립트
$(function () {
	var form = $("#adoptFilterForm");
	if (form.length) {
		form.on("submit", function () {
			var animal = form.find("[name='animalHeaderName']");
			var type = form.find("[name='typeHeaderName']");
			var orderBy = form.find("[name='orderBy']");
			var keyword = form.find("[name='keyword']");
			var column = form.find("[name='column']");

			if (animal.val() === "") animal.prop("disabled", true);
			if (type.val() === "") type.prop("disabled", true);

			var kw = (keyword.val() || "").trim();
			if (kw.length === 0) {
				keyword.val("").prop("disabled", true);
				column.prop("disabled", true);
			}

			if (orderBy.val() === "wtime") orderBy.prop("disabled", true);
		});
	}

	$(document).on("click", ".js-adopt-card", function (e) {
		if ($(e.target).closest("a, button, input, textarea, select, label").length) return;
		var href = $(this).data("href");
		if (href) window.location.href = href;
	});
});
