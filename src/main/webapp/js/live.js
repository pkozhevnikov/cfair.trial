var dps = [];
var chart = new CanvasJS.Chart("chartContainer", {
	title : {
		text : "",
	},
    axisX: {
    	intervalType: "second",
    	interval: 5,
        valueFormatString: "mm:ss",
        labelAngle: -50
    },
    axisY: {
    	interval: 0.2,
    	minimum: 0.4,
    	maximum: 1.6,
    	gridThickness: .5,
    	valueFormatString: "0.0"
    },
	data : [ {
		type : "candlestick",
		xValueType: "dateTime",
		dataPoints : dps
	} ]
});
chart.render();


var request = { url: ratesUrl,
		contentType : "text/plain",
		logLevel : "debug",
		transport : "websocket" ,
		trackMessageLength : true,
		reconnectInterval : 5000
};

var selectedPair;

function pairClick() {
	var cp = $.data(this, "cp");
	selectedPair = cp.from + cp.to;
	chart.options.title.text = cp.from + "/" + cp.to;
	renderChart();
}

var pairs = [];

var rates = {
};

function renderChart() {
	if (selectedPair) {
		dps.splice(0, dps.lenght);
		$.each(rates[selectedPair], function(index, f) {
			dps[index] = f;
		});
		chart.render();
	}
}

function renderTable() {
	$("#summary tbody tr").remove();
	$.each(pairs, function(index, f) {
		$("#summary tbody")
			.append($("<tr>")
				.append($("<td>")
					.append($("<a>").text(f.currencyPair.from + "/" + f.currencyPair.to)
							.css("cursor", "pointer")
							.data("cp", f.currencyPair).click(pairClick)))
				.append($("<td>").text(millisecondsToTime(f.period)))
				.append($("<td>").text(round(f.open, 4)))
				.append($("<td>").text(round(f.min, 4)))
				.append($("<td>").text(round(f.max, 4)))
				.append($("<td>").text(round(f.close, 4)))
				.append($("<td>").text(f.totalMessages))
				.append($("<td>").text(f.totalBuy))
				.append($("<td>").text(f.totalSell)));
	});
}

request.onMessage = function(response) {
	var str = response.responseBody;
	pairs = JSON.parse(str);
	renderTable();
	
	$.each(pairs, function(index, f) {
		var p = f.currencyPair.from + f.currencyPair.to;
		if (!selectedPair) {
			selectedPair = p;
			chart.options.title.text = f.currencyPair.from + "/" + f.currencyPair.to;
		}
		var crates = rates[p];
		if (!crates) {
			crates = [];
			rates[p] = crates;
		}
		
		if (crates.length < 60) {
			var c = 1;
			for (var i = 60 - crates.length - 1; i >= 0; i--) {
				crates[i] = {
					x: f.period - 1000 * c++,
					y: [1, 1, 1, 1]
				};
			}
		} 

		crates.push({
			x: f.period,
			y: [f.open, f.max, f.min, f.close]
		});
		while (crates.length != 60)
			crates.shift();

	});
	
	renderChart();
	
};

function round(a, b) {
	b = b || 0;
	return Math.round(a * Math.pow(10, b)) / Math.pow(10, b);
}

function millisecondsToTime(milli) {
      var milliseconds = milli % 1000;
      var seconds = Math.floor((milli / 1000) % 60);
      var minutes = Math.floor((milli / (60 * 1000)) % 60);

      return minutes + ":" + seconds + "." + milliseconds;
}

var socket = $.atmosphere.subscribe(request);


