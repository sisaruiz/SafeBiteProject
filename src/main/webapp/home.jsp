<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Homepage</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f4f4f4;
        margin: 0;
        padding: 0;
    }
    h2 {
        text-align: center;
        margin-top: 50px;
    }
    p {
        text-align: center;
    }
    form {
        text-align: center;
    }
    input[type="submit"] {
        background-color: #4CAF50;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
        margin-top: 20px;
    }
    input[type="submit"]:hover {
        background-color: #45a049;
    }
    a {
        display: block;
        text-align: center;
        margin-top: 20px;
        text-decoration: none;
        color: #007bff;
    }
    a:hover {
        text-decoration: underline;
    }
    section {
        margin: 20px auto;
        width: 80%;
        border: 1px solid #ccc;
        border-radius: 5px;
        padding: 10px;
        background-color: #fff;
    }
    h3 {
        margin: 0;
        padding: 0;
        text-align: center;
    }
</style>
</head>
<body>
<%
	Neo4jManager neo4jManager = new Neo4jManager();
	if(session.getAttribute("uname")==null){
		response.sendRedirect("login.jsp");
	}
	
	String userName = (String) session.getAttribute("uname");
	
    List<String> potentialFriends = neo4jManager.findPotentialFriends(userName);
    request.setAttribute("potentialFriends", potentialFriends);
    
    List<String> recommendedProducts = neo4jManager.findRecommendedProducts(userName);
    request.setAttribute("recommendedProducts", recommendedProducts);
%>
<h2>
Welcome, <%=session.getAttribute("uname")%>
</h2>
<br>
<p><a href="yourProfile.jsp">Your profile</a></p>
<br>
<form action="logout.jsp">
    <input type="submit" value="Logout">
</form>
<br>
<section>
    <form action="SearchServlet" method="POST">
        <label for="searchTerm">Search Product:</label>
        <input placeholder="type at least 3 letters" type="text" id="searchTerm" name="searchTerm" required>
        <br>
        <input type="submit" value="Search">
    </form>
</section>
<section>
    <h3>Explore recommended products ...</h3>
    <% if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
           for (String recommendedProduct : recommendedProducts) {
               String[] productInfo = recommendedProduct.split("-");
               String productId = productInfo[0];
               String productName = productInfo[1];
    %>
               <p><a href="productDetails.jsp?productId=<%= productId %>"><%= productName %></a></p>
    <%  }
       } else { %>
           <p>No recommended products found. Try updating your preferences.</p>
    <% } %>
</section>
<br>
<section>
    <form action="SearchUsersServlet" method="POST">
        <label for="searchTerm">Search User:</label>
        <input type="text" id="searchTerm" name="searchTerm" required>
        <br>
        <input type="submit" value="Search">
    </form>
</section>
<section>
    <h3>Explore potential connections ...</h3>
    <% if (potentialFriends != null) {
    	for (String potentialFriend : (List<String>) request.getAttribute("potentialFriends")) { %>
        <p><a href="userDetails.jsp?user=<%= potentialFriend %>"><%= potentialFriend %></a></p>
    <% }
    } else {
    	%> <p>Follow more users to get suggestions. </p> <% 
    }
     %>
</section>
<%
	neo4jManager.closeNeo4jConnection();
%>
</body>
</html>
