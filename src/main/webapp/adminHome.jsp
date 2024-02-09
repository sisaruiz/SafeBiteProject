<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ page import="org.neo4j.driver.AuthTokens" %>
<%@ page import="org.neo4j.driver.Driver" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Administrator home</title>
</head>
<body>
<%
	if(session.getAttribute("uname")==null || (Boolean) session.getAttribute("admin")==false){
		response.sendRedirect("login.jsp");
	}
%>
<h1>Home</h1>
<h2>
Welcome, <%=session.getAttribute("uname")%>
</h2>
<br>
<form action="logout.jsp">
    <input type="submit" value="Logout">
</form>
<br>
<section>
<h3>Products</h3>
<p>Add product</p>
<form action=InsertProductServlet method=post>
<pre>
		Product name:	<input type="text" name="pname">
		Image URL:		<input type="text" name="imgURL">
		Ingredients:	<input type="text" name="ingredients">
		Allergens:		<input type="text" name="allergens">
		Brands:			<input type="text" name="brands">
		Brand Owner:	<input type="text" name="bowner">
		Categories:		<input type="text" name="categories">
		Countries:		<input type="text" name="countries">
		Labels:			<input type="text" name="labels">
		Quantity:		<input type="text" name="quantity">
		Traces:			<input type="text" name="traces">
		<input type=submit value="Add product">		<input type=reset>
</pre>
</form>
<br>
<p>Update or remove products/reviews</p>
<form action="SearchServlet" method="POST">
<pre>
	Search Product: <input type="text" id="searchTerm" name="searchTerm" required> <input type="submit" value="Search">
</pre>
</form>
<br>
</section>
<section>
<h3>Users</h3>
<p>Remove user</p>
<form action="SearchUsersServlet" method="POST">
<pre>
	Search user: <input type="text" id="searchTerm" name="searchTerm" required> <input type="submit" value="Search">
</pre>
</form>
</section>


<br>
<!-- Add a button to navigate to systemAnalysis.jsp -->
<form action="systemAnalysis.jsp">
    <input type="submit" value="System Analysis">
</form>

</body>
</html>