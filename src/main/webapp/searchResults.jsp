<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Product" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <style>
        /* Set a fixed width and height for the product images */
        img.product-image {
            width: 80px; /* Set your preferred width */
            height: 80px; /* Set your preferred height */
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
        String searchTerm = (String) request.getAttribute("searchTerm");
        // Retrieve the search results from the request attribute
        List<Product> allProducts = (List<Product>) request.getAttribute("allProducts");

        // Retrieve the sessio
        HttpSession hs = request.getSession();
        // Check if the admin attribute is true in the session
        boolean isAdmin = (hs.getAttribute("admin") != null) && (Boolean) hs.getAttribute("admin");
    %>

    <p>Number of results: <%= allProducts.size() %></p>

    <div class="product-grid">
        <% 
            if (allProducts.size() > 0) {
                for (Product product : allProducts) {
        %>
            <div class="product-container">
                <img class="product-image" src="<%= product.getImgURL() %>">
                <p>
                    <a href="<%= isAdmin ? "productEdit.jsp" : "productDetails.jsp" %>?productId=<%= product.getId()%>"><%= product.getName() %></a>
                </p>
            </div>
            <%
                }
            }
            else {
        %>
            <p>No products found matching your search term.</p>
        <%
            }
        %>
    </div>
</body>
</html>
