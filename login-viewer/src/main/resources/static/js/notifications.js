class Notifications {

  constructor() {
    // Initial Setup
    this.container = this.generateContainer()
    this.container.append(this.generateLoader())
  }

  generateContainer() {
    return $("<div>", { id: "notifications-container", class: "container" })
  }

  generateLoader(message) {
    return $(`
      <div class="valign-wrapper flex center ">
        <div class="center-div">
          <div id="loading" class="grey-text text-darken-1">
            <h5>${message ? message : "Loading"}</h5>
          </div>

          <div class="preloader-wrapper big active">
            <div class="spinner-layer spinner-blue-only">
              <div class="circle-clipper left">
                <div class="circle"></div>
                  </div><div class="gap-patch">
                    <div class="circle"></div>
                  </div><div class="circle-clipper right">
                <div class="circle"></div>
              </div>
            </div>
          </div>
        </div>
      <div>`)
  }

  render() {
    this.enableUpdating = true;
    this.update()
    return this.container
  }

  beforeRemove() {
    this.enableUpdating = false
  }

  update() {
    $.ajax({
      url: "http://localhost:9000/notifications",
      context: this,
      success: function (data) {
        if (this.enableUpdating) {
          let container = this.generateContainer()
          if (data.length > 0) {
            container.append(new NotificationsTableRender(data).render())
          } else {
            container.append(this.generateLoader("Waiting for Data"))
          }
          $("#notifications-container").replaceWith(container)
        }
      },
      error: function () {
        // TODO: Handle Error
      }
    });

    if (this.enableUpdating) {
      setTimeout(() => this.update(), 4000)
    }
  }

}

class NotificationsTableRender {

  constructor(data) {
    this.data = data
  }

  render() {
    return this.buildTableFromResult()
  }

  buildTableFromResult() {
    let table = $(`
        <table class="responsive-table striped centered blue-text text-darken-2">
          <thead>
            <th>Reference</th>
            <th>Severity</th>
            <th>Timestamp</th>
            <th>Message</th>
          <thead>
        </table>`)

    let tableBody = $('<tbody></tbody>')
    table.append(tableBody)

    $.each(this.data, function (index, item) {
      tableBody.append(new NotificationsRowRender(index, item).render());
    });

    return table
  }

}

class NotificationsRowRender {

  constructor(index, data) {
    this.index = index
    this.data = data
  }

  render() {
    let item = this.data
    return $(`
      <tr class="grey-text text-darken-1">
        <td>${item.reference}</td>
        <td class=${item.severity == "High" ? "red-text" : item.severity == "Medium" ? "yellow-text" : "green-text"}>${item.severity}</td>
        <td>${this.formatTimestamp(item.timestamp)}</td>
        <td>${item.message}</td>
      </td>`)
  }

  formatTimestamp(timestamp) {
    let date = new Date(timestamp)
    let formatted_date = date.getFullYear() + "-"
      + this.appendLeadingZeroes(date.getMonth() + 1) + "-"
      + this.appendLeadingZeroes(date.getDate()) + " "
      + this.appendLeadingZeroes(date.getHours()) + ":"
      + this.appendLeadingZeroes(date.getMinutes()) + ":"
      + this.appendLeadingZeroes(date.getSeconds())
    return formatted_date
  }

  appendLeadingZeroes(n) {
    return (n <= 9) ? "0" + n : n
  }

}
