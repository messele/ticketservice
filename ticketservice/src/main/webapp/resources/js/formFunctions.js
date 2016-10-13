

$(function() {
		$(".input").on("focus", function() {
			$(".error").hide();
		});

		$("#resetBtn").on("click", function() {
			$(".error").hide();
			$("input").text("");
		});
	})