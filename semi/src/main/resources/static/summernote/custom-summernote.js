$(function(){
    $(".summernote-editor").summernote({
        height: 250,
        minHeight: 200,
        maxHeight: 400,

        placeholder:"내용을 입력하세요.",
        toolbar:[
        ["font", ["style", "fontname", "fontsize", "forecolor", "backcolor"]],
        ["style", ["bold", "italic", "underline", "strikethrough"]],
        ["attach", ["picture"]],
        ["tool", ["ol", "ul", "paragraph", "table", "hr", "fullscreen"]],
        ],

		callbacks: {
            onImageUpload: function(files) {

                var form = new FormData();
				for(var i=0; i<files.length;i++)
                	form.append("media", files[i]);

                $.ajax({
                    processData: false,
                    contentType: false,
                    url: window.contextPath + "/rest/media/temps",
                    method: "post",
                    data: form,
                    success: function(response) {
						for(var i=0;i<response.length;i++){
	                        var img = $("<img>").attr("src", window.contextPath + "/media/download?mediaNo="
								+ response[i]).attr("data-pk", response[i]).addClass("custom-image");
	                        $(".summernote-editor").summernote("insertNode", img[0]);
						}
                    }
                });
            },
        }
    });
	$(".text-summernote-editor").summernote({
	        height: 250,
	        minHeight: 200,
	        maxHeight: 400,

	        placeholder:"내용을 입력하세요.",
	        toolbar:[
	        ["font", ["style", "fontname", "fontsize", "forecolor", "backcolor"]],
	        ["style", ["bold", "italic", "underline", "strikethrough"]],
	        ["tool", ["ol", "ul", "paragraph", "table", "hr", "fullscreen"]],
	        ],

			callbacks: {
	            onImageUpload: function() {
					alert("이곳에는 이미지 업로드가 불가능합니다.")
					return;

	            }
	        }
	    });
		$(".note-editable").on("input compositionend", function(){
			var limit = $("#total-char").attr("data-maxLength");
			var char = $(this).text();
			var totalChar = char.length;
			$("#total-char").text(totalChar);
			    if(totalChar > limit){
					totalChar = limit;
					$("#total-char").text(totalChar);
			        $(this).text(char.substring(0, limit));

			        var el = this;
			        var range = document.createRange();
			        var sel = window.getSelection();
			        range.selectNodeContents(el);
			        range.collapse(false);
			        sel.removeAllRanges();
			        sel.addRange(range);
			    }
		})
});
