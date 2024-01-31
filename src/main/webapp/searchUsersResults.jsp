<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
</head>
<body>
    <h1>Search Results</h1>
    <% 
    	String searchTerm = (String) request.getAttribute("searchTerm");
        UserDAO userDAO = new UserDAO();
        List<User> allUsers = userDAO.searchUsers(request.getParameter("searchTerm"));
        int numberOfResults = allUsers.size();
    %>

    <p>Number of results: <%= numberOfResults %></p>
        <% 
            if (numberOfResults > 0) {
                for (int i = 0; i < numberOfResults; i++) {
                    User user = allUsers.get(i);
        %>
    				<p>
        			<a href="userDetails.jsp?user=<%= user.getName() %>"><%= user.getName() %></a>
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