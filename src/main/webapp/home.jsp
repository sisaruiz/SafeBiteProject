<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login success</title>
</head>
<body>
<%
	if(session.getAttribute("uname")==null){
		response.sendRedirect("login.jsp");
	}
%>
<h1>
Welcome, <%=session.getAttribute("uname")%>
<br>
Login successful...
</h1>
<br>
<form action=logout.jsp>
<input type=submit value=Logout>

</form>
</body>
</html>