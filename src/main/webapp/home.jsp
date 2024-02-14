<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login success</title>
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
    
 // Retrieve and set recommended products
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
        <input type="text" id="searchTerm" name="searchTerm" required>
        <br>
        <input type="submit" value="Search">
    </form>
</section>
<section>
    <h3>Explore recommended products ...</h3>
    <% if (recommendedProducts != null && !recommendedProducts.isEmpty()) {
           for (String recommendedProduct : recommendedProducts) {
               String[] productInfo = recommendedProduct.split("-"); // Assuming the delimiter is "-"
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
