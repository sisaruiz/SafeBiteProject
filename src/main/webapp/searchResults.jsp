<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Product" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <style>
        /* Set a fixed width and height for the product images */
        img.product-image {
            width: 70px; /* Set your preferred width */
            height: 70px'; /* Set your preferred height */
            object-fit: cover; /* Optional: Maintain aspect ratio and cover entire container */
        }

        /* Create a grid layout with 5 columns */
        .product-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr); /* Adjust the number of columns as needed */
            gap: 10px; /* Adjust the gap between products */
        }

        /* Style for individual product container */
        .product-container {
            text-align: center;
        }
    </style>
</head>
<body>
    <h1>Search Results</h1>
    <% 
        List<Product> products = (List<Product>) request.getAttribute("products");
        int numberOfResults = products.size();
    %>

    <p>Number of results: <%= numberOfResults %></p>

    <div class="product-grid">
        <% 
            if (numberOfResults > 0) {
                for (Product product : products) {
        %>
            <div class="product-container">
                <img class="product-image" src=<%= product.getImgURL() %>>
                <p>
                    <a href="productDetails.jsp?productId=<%= product.getId() %>"><%= product.getName() %></a>
                </p>
            </div>
        <%
                }
            } else {
        %>
            <p>No products found matching your search term.</p>
        <%
            }
        %>
    </div>
</body>
</html>
