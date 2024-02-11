<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mongodb.client.AggregateIterable" %>
<%@ page import="org.bson.Document" %>
<%@ page import="mongo.MongoAggregations" %>

<html>
<head>
    <title>MongoDB Aggregation Results</title>
</head>
<body>

<%
    String connectionString = "mongodb://localhost:27017";
    String databaseName = "yourDatabaseName";
    String collectionName = "Products";

    MongoAggregations mongoAggregations = new MongoAggregations(connectionString, databaseName, collectionName);
    AggregateIterable<Document> result = mongoAggregations.getProductCategoryCounts();
%>

<h1>Product Category Counts</h1>

<table border="1">
    <tr>
        <th>Category</th>
        <th>Count</th>
    </tr>
    <% for (Document document : result) { %>
        <tr>
            <td><%= document.get("_id") %></td>
            <td><%= document.get("count") %></td>
        </tr>
    <% } %>
</table>

<%
    // Close the MongoDB connection after using the results
    mongoAggregations.closeConnection();
%>

</body>
</html>
