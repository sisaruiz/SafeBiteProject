<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Browse Users</title>
</head>
<body>
<%
	if(session.getAttribute("uname")==null){
		response.sendRedirect("login.jsp");
	}
%>
<h1>
Welcome, <%=session.getAttribute("uname")%>
</h1>
<br>
<p><a href="yourProfile.jsp">Your profile</a></p>
<br>
<form action="logout.jsp">
    <input type="submit" value="Logout">
</form>
<br>
<section>
    <form action="SearchUsersServlet" method="POST">
        <label for="searchTerm">Search Term:</label>
        <input type="text" id="searchTerm" name="searchTerm" required>
        <br>
        <input type="submit" value="Search">
    </form>
</section>
</body>
</html>
