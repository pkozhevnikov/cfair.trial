
$("#submit").click(function() {
	var req = {
		userId: $("#userId").val(),
		currencyFrom: $("#currencyFrom").val(),
		currencyTo: $("#currencyTo").val(),
		rate: $("#rate").val(),
		amountBuy: $("#amountBuy").val(),
		amountSell: $("#amountSell").val(),
		timePlaced: $("#timePlaced").val(),
		originatingCountry: $("#originatingCountry").val()
	};
	$.ajax({
		url: serviceUrl + "/add",
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		accept: "application/json",
		data: JSON.stringify(req),
		success: function() {
			clearFields();
			showMsg("Posted successfully", "success");
			$("#list tbody")
			.append($("<tr>")
				.append($("<td>").text(req.userId))
				.append($("<td>").text(req.currencyFrom + "/" + req.currencyTo))
				.append($("<td>").text(req.rate))
				.append($("<td>").text(req.amountBuy))
				.append($("<td>").text(req.amountSell))
				.append($("<td>").text(req.timePlaced))
				.append($("<td>").text(req.originatingCountry)));

		},
		error: ajaxError
	});
});

function clearFields() {
	$("#userId").val(null);
	$("#currencyFrom").val(null);
	$("#currencyTo").val(null);
	$("#rate").val(null);
	$("#amountBuy").val(null);
	$("#amountSell").val(null);
	$("#timePlaced").val(null);
	$("#originatingCountry").val(null);
}

function ajaxError(response) {
	var er = JSON.parse(response.responseText);
	var s = er.message;
	if (er.causeMessage) s += "; " + er.causeMessage;
	showMsg(s, "error");
}

function showMsg(msg, type) {
	$("#msg").removeClass("hidden").removeClass("error").removeClass("success").addClass(type).text(msg);
}

function hideMsg() {
	$("#msg").removeClass("error").removeClass("success").addClass("hidden");
}

$("#refresh").click(function() {
	hideMsg();
	$.ajax({
		url: serviceUrl + "/list",
		type: "GET",
		contentType: "application/json",
		success: function(rows) {
			buildTable(rows);
		},
		error: ajaxError
	});
});

function buildTable(rows) {
	
	$("#list tbody tr").remove();
	$.each(rows, function(index, row) {
		$("#list tbody")
			.append($("<tr>")
				.append($("<td>").text(row.userId))
				.append($("<td>").text(row.currencyFrom + "/" + row.currencyTo))
				.append($("<td>").text(row.rate))
				.append($("<td>").text(row.amountBuy))
				.append($("<td>").text(row.amountSell))
				.append($("<td>").text(row.timePlaced))
				.append($("<td>").text(row.originatingCountry)));
	});
}

$("#refresh").click();

