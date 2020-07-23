$(document).ready(function () {

  let requests = new LoginRequests()
  let notifications = new Notifications()

  let currentComponent = null

  let nav = {
      "#requests": function () {
          return requests
      },
      "#notifications": function () {
          return notifications
      }
  }

  // Dropdown behaviour
  $("#nav-mobile a").click(function() {
      render(this.hash)
  });

  // Standard behaviour
  render(window.location.hash);

  function render(hash) {
      if(currentComponent) {
        currentComponent.beforeRemove()
      }

      let result = nav[hash];
      currentComponent = result ? result() : requests

      $("main").empty()
      $("main").append(currentComponent.render())
  }

})
