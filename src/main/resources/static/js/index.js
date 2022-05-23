let button = document.getElementById('getButton');

fillChart([]);

button.addEventListener('click', function() {
  if (dateFrom.value && dateTo.value) {
    let url = '/api/statistics?from=' + dateFrom.value + '&to=' + dateTo.value;
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open('GET', url, false);
    xmlHttp.send(null);
    let statistics = JSON.parse(xmlHttp.responseText);
    fillChart(statistics);

  } else {
    alert('Please complete both fields first!!!');
  }
});

function fillChart(statistics) {
  let dates = [];
  let cpu = [];
  let ram = [];
  let hardDrive = [];
  let network = [];
  statistics.forEach((status) => {
    dates.push(status.date)
    cpu.push(status.cpuUsagePercent);
    ram.push(status.ramUsagePercent);
    hardDrive.push(status.hardDriveFree);
    network.push(status.dnsConnectionTime);
  });
  new Chart('statistics', {
    type: 'line',
    data: {
      labels: dates,
      datasets: [
        {
          data: cpu,
          label: 'Cpu usage percent',
          borderColor: 'red',
          fill: false
        },
        {
          data: ram,
          label: 'Ram usage percent',
          borderColor: 'green',
          fill: false
        },
        {
          data: hardDrive,
          label: 'Disk free space',
          borderColor: 'blue',
          fill: false
        },
        {
          data: network,
          label: 'DNS connection time',
          borderColor: 'orange',
          fill: false
        }
      ]
    },
    options: {title: {display: true, text: 'Statistics'}}
  });
}