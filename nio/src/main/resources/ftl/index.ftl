<!DOCTYPE HTML>
<html>
<head>
<style>
    body {
        font-size: 15px;
        font-family: 'courier New'
    }

    a {
        text-decoration: none
    }
</style>
</head>
<body>
<#list list as map>
    <a href='${map["href"]}'>${map["name"]}</a><br/>
</#list>
</body>
</html>