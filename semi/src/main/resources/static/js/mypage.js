// mypage.js - 마이페이지 탭/선택 삭제 등 UI 스크립트
$(function () {
	var $checkAll = $("#check-all");
	var $deleteBtn = $("#btn-delete-selected");

	function syncAll() {
		var $items = $(".check-one");
		if ($items.length === 0) {
			$checkAll.prop("checked", false);
			return;
		}
		var checked = $items.filter(":checked").length;
		$checkAll.prop("checked", checked === $items.length);
	}

	$checkAll.on("change", function () {
		$(".check-one").prop("checked", $checkAll.is(":checked"));
	});

	$(document).on("change", ".check-one", syncAll);

	$deleteBtn.on("click", function () {
		var $checked = $(".check-one:checked");
		if ($checked.length === 0) {
			alert("삭제할 게시글을 선택해 주세요");
			return;
		}
		if (!confirm("선택한 게시글을 삭제하시겠습니까?")) return;

		var boardNos = $checked
			.map(function () { return $(this).val(); })
			.get();

		$.ajax({
			url: window.contextPath + "/board/community/mypageDelete",
			method: "post",
			traditional: true,
			data: { boardNo: boardNos },
			success: function (response) {
				if (response === "success") {
					location.reload();
					return;
				}
				alert("삭제 처리에 실패했습니다");
			},
			error: function () {
				alert("삭제 요청 중 오류가 발생했습니다");
			}
		});
	});
});
