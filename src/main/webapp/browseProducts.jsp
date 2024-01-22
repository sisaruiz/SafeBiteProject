<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Browse products</title>
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
    <form action="SearchServlet" method="POST">
        <label for="searchTerm">Search Term:</label>
        <input type="text" id="searchTerm" name="searchTerm" required>
        <br>
        <input type="submit" value="Search">
    </form>
</section>

<section>
    <h2>Browse by category</h2>
    <ul>
        <li><a href="Snacks.jsp">Snacks</a></li>
        <li><a href="Beverages.jsp">Beverages</a></li>
        <li><a href="Rices.jsp">Rices</a></li>
        <li><a href="Milks.jsp">Milks</a></li>
        <li><a href="PlantBasedFood.jsp">Plant-based Food</a></li>
        <li><a href="Vegetables.jsp">Vegetables</a></li>
        <li><a href="Cheese.jsp">Cheese</a></li>
    </ul>
</section>
<section>
    <h2>Browse by diet</h2>
    <ul>
        <li><a href="Vegan.jsp">Vegan</a></li>
        <li><a href="Vegetarian.jsp">Vegetarian</a></li>
        <li><a href="Halal.jsp">Halal</a></li>
        <li><a href="Pescatarian.jsp">Pescatarian</a></li>
    </ul>
</section>
</body>
</html>
