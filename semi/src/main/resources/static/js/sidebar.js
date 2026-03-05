// sidebar.js - 사이드바(새글/랭킹) UI 스크립트
moment.locale('ko');

$(function () {
	var tabButtons = $(".sidebar-tab");
	if (tabButtons.length > 0) {
		tabButtons.on("click", function () {
			var tab = $(this).data("tab");
			tabButtons.removeClass("on").attr("aria-selected", "false");
			$(this).addClass("on").attr("aria-selected", "true");
			$(".sidebar-panel").addClass("d-none");
			$(".sidebar-panel[data-panel='" + tab + "']").removeClass("d-none");
		});
	}


    Promise.all([
    	newboard_list(),
    	ranking_list()
    ]).then(() => {
    	free_board_list()
    })

    function newboard_list() {
        return $.ajax({
            url: window.contextPath + "/rest/main/newboard",
            method: "post",
            data: {},
            success: function (response) {
                var wrapper = $(".new-board-list-wrapper")
                wrapper.empty()
				var htmlBuffer = [];

                for (var i = 0; i < response.length; i++) {
                    var newboard = response[i];

                    var html = $($.parseHTML($("#new-board-template").text()))

                    var url = window.contextPath + "/board/" + newboard.categoryName + "/detail?boardNo=" + newboard.boardNo

                    html.find(".new-board-title").text(newboard.boardTitle).attr("href", url);
                    html.find(".new-board-time").text(newboard.formattedWtime);

                    htmlBuffer.push(html);


                }
                wrapper.append(htmlBuffer)
            }
        });
    }

    function ranking_list() {
        return $.ajax({
            url: window.contextPath + "/rest/main/ranking",
            method: "post",
            data: {},
            success: function (response) {
                var wrapper = $(".ranking-list-wrapper")
                wrapper.empty()

                var htmlBuffer = [];

                var number = 1;
                for (var i = 0; i < response.length; i++) {
                    var ranking = response[i];


                    var html = $($.parseHTML($("#ranking-template").text()));

                    html.find(".ranking-profile").attr("src", window.contextPath + "/member/profile?member_id=" + ranking.memberId);
                    html.find(".number").text(number);
                    number++;

                    var url = window.contextPath + "/member/detail?memberNickname=" + ranking.memberNickname

                    html.find(".ranking-nickname").text(ranking.memberNickname).attr("href", url);
                    var rawDesc = ranking.memberDescription || "";
                    var plainDesc = $("<div>").html(rawDesc).text().replace(/\s+/g, " ").trim();
                    if (plainDesc.length === 0) plainDesc = "소개 없음";
                    html.find(".ranking-description").text(plainDesc);

                    html.find(".ranking-member-point").text(ranking.memberPoint);


                    htmlBuffer.push(html)
                }
                wrapper.append(htmlBuffer);
            }
        });
    }

    function free_board_list() {

		var times = $(".free-board-time")
		if(times.length == 0) return

		var now = moment()

		times.each(function (){
			var text = $(this).text().trim()
			var time = moment(text)
			$(this).text(time.from(now))
		})
    }
});
