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
<h2>
Welcome, <%=session.getAttribute("uname")%>
</h2>
<br>
<p><a href="profile.jsp">Your profile</a></p>
<br>
<form action="logout.jsp">
    <input type="submit" value="Logout">
</form>
<br>
<a href="browseProducts.jsp">Search products</a>
<a href="browseUsers.jsp">Search users</a>
<section>
    <h3>Products you may want to try ...</h3>
</section>
<section>
    <h3>Explore potential connections ...</h3>
</section>
</body>
</html>
