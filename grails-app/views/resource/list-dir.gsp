<%@ page import="hm.Application; java.nio.file.Path" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <title>槐盟</title>
</head>
<body>
<div>
    <h1>${dir.absolutePath}</h1>
    <h2><a href="../">上一级目录 ../</a></h2>
    <g:each in="${dir.listFiles()}" var="it">
        <h2><a href="${it.isDirectory()?it.name+'/':it.name}">${it.isDirectory()?it.name+'/':it.name}</a></h2>
    </g:each>
</div>
</body>
</html>

