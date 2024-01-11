<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Logout</title>
</head>
<body>
<%
session.removeAttribute("uname");
session.invalidate();
%>
<h1>You have been logged out!</h1>
<a href=login.jsp >Login again..</a>
</body>
</html>