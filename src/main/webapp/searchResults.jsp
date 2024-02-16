<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Product" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <style>
        img.product-image {
            width: 80px; 
            height: 80px; 
            object-fit: cover; 
        }

        .product-grid {
            display: grid;
            grid-template-columns: repeat(5, 1fr); 
            gap: 10px; 
            margin-top: 20px;
        }

        .product-container {
            text-align: center;
        }

        .active-page {
            font-weight: bold;
        }

        .pagination {
            margin-top: 20px;
            text-align: center;
        }

        .pagination a {
            display: inline-block;
            padding: 5px 10px;
            margin: 0 5px;
            border: 1px solid #ccc;
            border-radius: 3px;
            text-decoration: none;
            color: #333;
            transition: background-color 0.3s;
        }

        .pagination a:hover {
            background-color: #f5f5f5;
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
