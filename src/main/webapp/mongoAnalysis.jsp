<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.mongodb.client.AggregateIterable" %>
<%@ page import="org.bson.Document" %>
<%@ page import="mongo.MongoAggregations" %>
<%@ page import="org.bson.types.ObjectId" %> 

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
    String productsCollectionName = "Products";
    String reviewsCollectionName = "Reviews";

    MongoAggregations productsAggregations = new MongoAggregations(connectionString, databaseName, productsCollectionName);
    MongoAggregations reviewsAggregations = new MongoAggregations(connectionString, databaseName, reviewsCollectionName);

    AggregateIterable<Document> productCategoryCounts = productsAggregations.getProductCategoryCounts();
    List<Document> productCountByBrandAndCountry = productsAggregations.getProductCountByBrandAndCountry();
    Document mostReviewedProduct = reviewsAggregations.getMostReviewedProduct(reviewsAggregations.getCollection());
%>

<h1>Most Reviewed Product</h1>
<table border="1">
    <tr>
        <th>Product ID</th>
        <th>Product Name</th>
        <th>Review Count</th>
    </tr>
    <tr>
        <td>
            <% 
                Object productIdObj = mostReviewedProduct.get("_id");
                if (productIdObj instanceof ObjectId) {
                    out.print(((ObjectId) productIdObj).toString());
                }
            %>
        </td>
        <td><%= mostReviewedProduct.getString("ProductName") %></td>
        <td><%= mostReviewedProduct.getInteger("reviewCount") %></td>
    </tr>
</table>

<h1>Product Category Counts</h1>
<table border="1">
    <tr>
        <th>Category</th>
        <th>Count</th>
    </tr>
    <% for (Document document : productCategoryCounts) { %>
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
    <% for (Document doc : productCountByBrandAndCountry) { %>
        <tr>
            <td><%= doc.getString("brandOwner") %></td>
            <td><%= doc.getString("country") %></td>
            <td><%= doc.getInteger("productCount") %></td>
        </tr>
    <% } %>
</table>

<%
    // Close the MongoDB connections after using the results
    productsAggregations.closeConnection();
    reviewsAggregations.closeConnection();
%>
</body>
</html>
