// Generic modal close behavior: close on [data-modal-close] click or clicking backdrop
(function () {
  document.addEventListener("DOMContentLoaded", function () {
    // click on any element with data-modal-close inside a modal
    document
      .querySelectorAll(".modal [data-modal-close]")
      .forEach(function (btn) {
        btn.addEventListener("click", function () {
          var modal = btn.closest(".modal");
          if (modal) {
            modal.style.display = "none";
          }
        });
      });
    // click on backdrop area closes
    document.querySelectorAll(".modal").forEach(function (modal) {
      modal.addEventListener("click", function (e) {
        if (e.target === modal) modal.style.display = "none";
      });
    });
  });
})();
