//			Body-->
//			body='{
//			  "filterType": "<<noFilter, chessMoves, givenKeywords>>",
//			  "searchQuery": "<<Search Query for Youtube Video>>",
//			  "keywords": [<<Array of search keywords if filterType is givenKeywords>>]
//			}'

//Set data in canvas tag myChart 
const ctx = document.getElementById("myChart").getContext("2d");
const filterValueElement = document.getElementById("filterOption");
const connectBtn = document.getElementById('connectBtn');
const disconnectBtn = document.getElementById('disconnectBtn');
const searchQueryElement = document.getElementById("searchQuery");
//Connecting to Websocket Server
const websocketEndpoint = "wss://3fsm3zmhpb.execute-api.us-east-1.amazonaws.com/production";
var socket = new WebSocket(websocketEndpoint);
var chartelement = document.getElementById("myChart");
chartelement.style.display = "none";

var masterDataAuthor = {};
var masterDataCount = {};

var topMoves = [];
var countOfTopMoves = [];

var totalCount = 0;

var yValues = ["Italy", "France", "Spain", "USA", "Argentina", "sadas", "asdsa", "asdas", "asfdwd", "wef"];
var xValues = [55, 49, 44, 24, 15, 3, 3, 3, 132, 34];
var barColors = [
  'rgba(255, 99, 132, 0.2)',
  'rgba(255, 159, 64, 0.2)',
  'rgba(255, 205, 86, 0.2)',
  'rgba(75, 192, 192, 0.2)',
  'rgba(54, 162, 235, 0.2)',
  'rgba(153, 102, 255, 0.2)',
  'rgba(201, 203, 207, 0.2)',
  'rgb(230,23,250, 0.2)',
  'rgb(139,69,19,0.2)',
  '	rgb(128,0,0, 0.2)'
];
var borderColors = [
  'rgb(255, 99, 132)',
  'rgb(255, 159, 64)',
  'rgb(255, 205, 86)',
  'rgb(75, 192, 192)',
  'rgb(54, 162, 235)',
  'rgb(153, 102, 255)',
  'rgb(201, 203, 207)',
  'rgb(230,23,250)',
  'rgb(139,69,19)',
  '	rgb(128,0,0)'
];

var chartData = {
  type: "bar",
  data: {
    labels: topMoves,
    datasets: [{
      backgroundColor: barColors,
      borderColor: borderColors,
      borderWidth: 3,
      barThickness: 40,
      categoryPercentage: 0.1,
      barPercentage: 0.1,

      data: countOfTopMoves
    }]
  },
  options: {
    indexAxis: 'y',
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      tooltip: {
        // Disable the on-canvas tooltip
        enabled: false,
        position: 'nearest',
        external: tooltipHandler
      },
      legend: {
        display: false
    }
    }
  }
}
var ytChart = new Chart(ctx, chartData);

const getOrCreateTooltip = (chart) => {
  let tooltipEl = chart.canvas.parentNode.querySelector('div');

  if (!tooltipEl) {
    tooltipEl = document.createElement('div');
    tooltipEl.style.background = 'rgba(0, 0, 0, 0.7)';
    tooltipEl.style.borderRadius = '3px';
    tooltipEl.style.color = 'white';
    tooltipEl.style.opacity = 1;
    tooltipEl.style.pointerEvents = 'none';
    tooltipEl.style.position = 'absolute';
    tooltipEl.style.transform = 'translate(-50%, 0)';
    tooltipEl.style.transition = 'all .1s ease';

    const table = document.createElement('table');
    table.style.margin = '0px';

    tooltipEl.appendChild(table);
    chart.canvas.parentNode.appendChild(tooltipEl);
  }

  return tooltipEl;
};

function tooltipHandler(context) {
  // Tooltip Element
  const { chart, tooltip } = context;
  const tooltipEl = getOrCreateTooltip(chart);

  // Hide if no tooltip
  if (tooltip.opacity === 0) {
    tooltipEl.style.opacity = 0;
    return;
  }

  // Set Text
  if (tooltip.body) {
    const titleLines = tooltip.title || [];
    const bodyLines = tooltip.body.map(b => b.lines);

    const tableHead = document.createElement('thead');

    titleLines.forEach(title => {
      const tr = document.createElement('tr');
      tr.style.borderWidth = 0;

      const th = document.createElement('th');
      th.style.borderWidth = 0;
      const text = document.createTextNode(title);

      th.appendChild(text);
      tr.appendChild(th);
      tableHead.appendChild(tr);
    });

    const tableBody = document.createElement('tbody');
    bodyLines.forEach((body, i) => {
      const colors = tooltip.labelColors[i];

      const span = document.createElement('span');
      span.style.background = colors.backgroundColor;
      span.style.borderColor = colors.borderColor;
      span.style.borderWidth = '2px';
      span.style.marginRight = '10px';
      span.style.height = '10px';
      span.style.width = '10px';
      span.style.display = 'inline-block';

      const tr = document.createElement('tr');
      tr.style.backgroundColor = 'inherit';
      tr.style.borderWidth = 0;

      const td = document.createElement('td');
      td.style.borderWidth = 0;

      const text = document.createTextNode(body);

      td.appendChild(span);
      td.appendChild(text);
      tr.appendChild(td);
      tableBody.appendChild(tr);

      //append top 10 authors to tooltip

      masterDataAuthor[titleLines[0]].forEach(element => {
        var author_span = document.createElement('span');

        var author_tr = document.createElement('tr');
        author_tr.style.backgroundColor = 'inherit';
        author_tr.style.borderWidth = 0;

        var author_td = document.createElement('td');
        author_td.style.borderWidth = 0;

        var author_text = document.createTextNode(element);

        author_td.appendChild(author_span);
        author_td.appendChild(author_text);
        author_tr.appendChild(author_td);
        tableBody.appendChild(author_tr);
      });

    });

    const tableRoot = tooltipEl.querySelector('table');

    // Remove old children
    while (tableRoot.firstChild) {
      tableRoot.firstChild.remove();
    }

    // Add new children
    tableRoot.appendChild(tableHead);
    tableRoot.appendChild(tableBody);
  }

  const { offsetLeft: positionX, offsetTop: positionY } = chart.canvas;

  // Display, position, and set styles for font
  tooltipEl.style.opacity = 1;
  tooltipEl.style.left = positionX + tooltip.caretX + 'px';
  tooltipEl.style.top = positionY + tooltip.caretY + 'px';
  tooltipEl.style.font = tooltip.options.bodyFont.string;
  tooltipEl.style.padding = tooltip.padding + 'px ' + tooltip.padding + 'px';
};


function showKeywordFields() {
  let filterValue = filterValueElement.value;
  console.log("Filter value" + filterValue);
  let keywordList = document.getElementById("KeywordList");
  let html1 = "";
  if (filterValue == "givenKeywords") {
    html1 += `<b>Enter Keywords</b><br>`;
    for (let i = 0; i < 10; i++) {
      html1 += ` <div class="form">
              <input type="text" class="form-control" id ="keyword${i}" placeholder="Keyword ${i + 1}">
        </div>`
    }

  } else {
    html1 = "";
  }
  keywordList.innerHTML = html1;
  //console.log(html1);
}

//If Bad Request Display error for 3 seconds
function showBadRequestError() {
  var errorElement = document.getElementById("showError");
  errorElement.innerHTML = `<div class="alert alert-danger" role="alert"><center><b>Invalid Search Query<b></center></div>`;
  setTimeout(function () {
    errorElement.innerHTML = "";
  }, 3000);
}

//Send Data in given format to trigger Websocket server to send chat messages
const sendRequest = function () {
  let searchQuery = searchQueryElement.value;
  let filterValue = filterValueElement.value;
  let keywordList = [];
  if (filterValue == "givenKeywords") {
    for (let i = 0; i < 10; i++) {
      if ((document.getElementById(`keyword${i}`).value) != "") {
        keywordList.push(document.getElementById(`keyword${i}`).value);
      }
    }
  }
  console.log("Sending message..." + searchQuery + "  " + filterValue);
  socket.send(`{"filterType": ${filterValue},"searchQuery": ${searchQuery},"keywords": [${keywordList.toString()}]}`);
}

function processData(data) {
  const obj = JSON.parse(data);
  const curList = obj["messages"];

  for (let message of curList) {
    author = message["author"];
    userMsg = message["message"];
    var logTableElement = document.getElementById('log_table');
    if (userMsg in masterDataAuthor) {
      // If same user has same message do not add
      if (!masterDataAuthor[userMsg].includes(author)) {
        // Keep only first 10 authors name
        totalCount+=1;
        if (masterDataAuthor[userMsg].length < 10) {
          masterDataAuthor[userMsg].push(author);
        }
        masterDataCount[userMsg] += 1;
      }
    } else {
      totalCount+=1;
      masterDataAuthor[userMsg] = [author];
      masterDataCount[userMsg] = 1;
    }

    var items = Object.keys(masterDataCount).map(function (key) {
      return [key, masterDataCount[key]];
    });

    items.sort(function (first, second) {
      return second[1] - first[1];
    });
    if (items.length > 10) {
      items = items.slice(0, 10);
    }
    topMoves = [];
    countOfTopMoves = [];
    items.forEach(element => {
      topMoves.push(element[0]);
      countOfTopMoves.push(element[1]);
    });
  }
  ytChart.data.labels = topMoves;
  ytChart.data.datasets[0].data = countOfTopMoves;
  ytChart.update();

}

socket.onopen = (e) => {
  console.log("Connected Websocket");
}
socket.onmessage = (event) => {
  if (event.data == "Bad Request") {
    console.log(event.data);
    showBadRequestError();
  } else {
    chartelement.style.display = "block";
    processData(event.data);
  }
}
socket.onerror = (e) => {
  // Return an error if any occurs
  console.log(e);
}

filterValueElement.addEventListener("change", showKeywordFields);
connectBtn.addEventListener("click", function () {
  sendRequest();
});
disconnectBtn.addEventListener("click", function () {
  location.reload();
})
