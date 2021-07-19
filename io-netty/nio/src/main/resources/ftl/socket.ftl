<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <style>
        input, button, textarea {
            border: 1px solid black;
            border-radius: unset;
            font-family: 'Courier New';
            min-height: 18px;
        }
    </style>
</head>
<body>

<input type="text" />
<button onclick="send()">发送</button>
<button onclick="_close()">断开</button>
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
        sock.onmessage = function (event) {
            textarea.value += ('\r\n' + event.data)
        }
        sock.onopen = function (event) {
            textarea.value = "已建立连接!";
        }
        sock.onclose = function (event) {
            textarea.value += "\r\n已关闭连接!";
        }
    }
    else {
        alert("该浏览器不支持websocket!")
    }

    function send() {
        if (!window.WebSocket) return

        if (sock.readyState === WebSocket.OPEN) {
            const msg = input.value
            sock.send(msg)
        }
        else {
            alert("未建立连接!")
        }
    }

    function _close() {
        if (sock.readyState === WebSocket.OPEN) {
            sock.close()
        }
    }
</script>

</body>
</html>