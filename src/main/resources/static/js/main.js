// src/main/resources/static/js/main.js
function loadMessage() {
    fetch("/api/hello")
        .then(res => res.text())
        .then(data => {
            document.getElementById("msg").innerText = data;
        });
}