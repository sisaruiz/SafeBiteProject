<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="model.User" %>
<%@ page import="java.util.Objects" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        h1 {
            color: #333;
        }
        .user-container {
            margin-bottom: 10px;
            border: 1px solid #ccc;
            padding: 10px;
            background-color: #f9f9f9;
        }
        .user-link {
            text-decoration: none;
            color: #0066cc;
        }
        .delete-form {
            display: inline;
        }
        .delete-button {
            background-color: #cc0000;
            color: #fff;
            border: none;
            padding: 5px 10px;
            cursor: pointer;
        }
        .delete-button:hover {
            background-color: #ff3333;
        }
    </style>
</head>
<body>
    <h1>Search Results</h1>
    <% 
        String searchTerm = (String) request.getAttribute("searchTerm");
        UserDAO userDAO = new UserDAO();
        List<User> allUsers = userDAO.searchUsers(request.getParameter("searchTerm"));
        int numberOfResults = allUsers.size();
        Boolean isAdmin = (Boolean)session.getAttribute("admin");
        String curUser = (String) session.getAttribute("uname");
    %>

    <p>Number of results: <%= numberOfResults %></p>
    <% 
        if (numberOfResults > 0) {
            for (int i = 0; i < numberOfResults; i++) {
                User user = allUsers.get(i);
                // Check if the current user's name matches the user in the loop
                boolean isCurrentUser = curUser.equals(user.getName());
    %>
                <p>
                    <% if (isCurrentUser) { %>
                        <a href="yourProfile.jsp"><%= user.getName() %></a>
                    <% } else if (!isAdmin) { %>
                        <a href="userDetails.jsp?user=<%= user.getName() %>"><%= user.getName() %></a>
                    <% } %>
                    <!-- Display delete button only if admin -->
                    <% if (Objects.nonNull(isAdmin) && isAdmin) { %>
                    	<%= user.getName() %>
                        <form method="post" action="DeleteUserServlet">
                            <input type="hidden" name="username" value="<%= user.getName() %>">
                            <input type="submit" value="Delete User">
                        </form>
                    <% } %>
                </p>
    <%
            }
        } else {
    %>
        <p>No users found matching your search term.</p>
    <%
        }
    %>

</body>
</html>
