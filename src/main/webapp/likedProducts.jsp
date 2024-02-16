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

    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f8f9fa;
            margin: 20px;
        }

        h1 {
            color: #007bff;
        }

        a {
            text-decoration: none;
            color: #28a745;
            margin-right: 10px;
        }

        p {
            color: #888;
        }
    </style>
</head>
<body>
<%
    String userName = (String) session.getAttribute("uname");
    Neo4jManager neo4jManager = new Neo4jManager();
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
    neo4jManager.closeNeo4jConnection();
%>
</body>
</html>