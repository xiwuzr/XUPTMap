<%@ page import="java.util.Set" %>
<%@ page import="com.pojo.Path" %><%--
  Created by IntelliJ IDEA.
  User: 佳豪
  Date: 2019/12/31
  Time: 14:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>这个图的最小生成树</title>
</head>
<body>
<%
    Set<Path> set=(Set)session.getAttribute("tree");
%>
<h2>
    最小生成树:
</h2>

<br/>

<%
    for(Path pp:set){
%>
      <%=pp.getStart()%> <%=pp.getEnd()%></br>
<%

    }
    session.invalidate();
%>

</body>
</html>
