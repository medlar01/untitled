<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
</head>
<body>

<input type="text" />
<button onclick="send()">发送</button>
<br />
<br />
<textarea style="width: 500px; height: 300px"></textarea>

<script>
    let sock;
    const
        textarea    = document.getElementsByTagName("textarea")[0],
        input       = document.getElementsByTagName("input")[0],
        button      = document.getElementsByTagName("button")[0]

    if (!window.WebSocket) {
        window.WebSocket = window.MozWebSocket
    }
    if (window.WebSocket) {
        sock = new WebSocket("ws://127.0.0.1:8080/websocket")
        console.log("sock", sock)
        sock.onmessage = function (event) {
            console.log("onmessage", event)
            textarea.value = event.data
        }
        sock.onopen = function (event) {
            console.log("onopen", event)
            textarea.value = "已建立连接!";
        }
        sock.onclose = function (event) {
            console.log("onclose", event)
            textarea.value = "已关闭连接!";
        }
    }
    else {
        alert("该浏览器不支持websocket!")
    }

    function send() {
        if (!window.WebSocket) return

        if (sock.readyState === WebSocket.OPEN) {
            const msg = input.value
            console.log("send", msg)
            sock.send(msg)
        }
        else {
            alert("未建立连接!")
        }
    }
</script>

</body>
</html>