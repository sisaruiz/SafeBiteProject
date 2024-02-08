<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="model.Product" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Liked Products</title>
</head>
<body>
<%
    // Assuming 'userName' is the current user's name, replace it with the actual attribute or session value
    String userName = (String) session.getAttribute("uname");

    // Create an instance of Neo4jManager
    Neo4jManager neo4jManager = new Neo4jManager();

    // Retrieve liked products for the user
    List<Product> likedProducts = neo4jManager.getLikedProductsForUser(userName);
%>

<h1>Liked Products</h1>

<%
    if (likedProducts != null && !likedProducts.isEmpty()) {
        for (Product product : likedProducts) {
%>
            <a href="productDetails.jsp?productId=<%= product.getId() %>"><%= product.getName() %></a>
<%
        }
    } else {
%>
        <p>No liked products found for <%= userName %>.</p>
<%
    }
%>

<%
    // Close the Neo4j connection
    neo4jManager.closeNeo4jConnection();
%>
</body>
</html>