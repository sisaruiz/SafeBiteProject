<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mongodb.client.AggregateIterable" %>
<%@ page import="org.bson.Document" %>
<%@ page import="mongo.MongoAggregations" %>

<html>

<%!  // Declaration of the method outside the scriptlet block
    String formatCategory(Object category) {
        if (category == null) {
            return "unspecified";
        } else if (category instanceof String) {
            // Remove the "en:" prefix
            return ((String) category).replace("en:", "");
        } else {
            return category.toString();
        }
    }
%>

<head>
    <title>MongoDB Aggregation Results</title>
</head>
<body>

<%
    String connectionString = "mongodb://localhost:27017";
    String databaseName = "SafeBite";
    String collectionName = "Products";

    MongoAggregations mongoAggregations = new MongoAggregations(connectionString, databaseName, collectionName);
    AggregateIterable<Document> result = mongoAggregations.getProductCategoryCounts();
    List<Document> results1 = mongoAggregations.getProductCountByBrandAndCountry();
%>

<h1>Product Category Counts</h1>

<table border="1">
    <tr>
        <th>Category</th>
        <th>Count</th>
    </tr>
    <% for (Document document : result) { %>
        <tr>
            <td><%= formatCategory(document.get("_id")) %></td>
            <td><%= document.get("count") %></td>
        </tr>
    <% } %>
</table>


<h1>Product Count by Brand and Country</h1>
<table border="1">
    <tr>
        <th>Brand Owner</th>
        <th>Country</th>
        <th>Product Count</th>
    </tr>
    <% for (Document doc : results1) { %>
        <tr>
            <td><%= doc.getString("brandOwner") %></td>
            <td><%= doc.getString("country") %></td>
            <td><%= doc.getInteger("productCount") %></td>
        </tr>
    <% } %>
</table>

<%
    // Close the MongoDB connection after using the results
    mongoAggregations.closeConnection();

%>
</body>
</html>