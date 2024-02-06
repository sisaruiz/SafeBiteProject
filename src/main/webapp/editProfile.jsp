<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="dao.UserDAO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Profile</title>
</head>
<body>

<h1>Edit Your Profile</h1>

<%
    // Retrieve user details from the session or database
    String username = (String) session.getAttribute("uname");
    UserDAO userDAO = new UserDAO();
    User user = userDAO.getUserByUsername(username);
%>

<form action="UpdateProfileServlet" method="post">
    <!-- Add fields for modifying diet and allergens -->
    <label for="diet">Diet:</label>
    <select id="diet" name="diet" required>
        <option value="none">None</option>
		<option value="vegan">Vegan</option>
		<option value="vegetarian">Vegetarian</option>
		<option value="pescatarian">Pescatarian</option>
		<option value="halal">Halal</option>
    </select>

    <h4>Allergens:</h4>
    <!-- Display existing allergens with checkboxes -->
    <%
        for (String allergen : user.getListAllergens()) {
    %>
            <input type="checkbox" id="<%= allergen %>" name="allergens" value="<%= allergen %>" checked />
            <label for="<%= allergen %>"><%= allergen %></label>
    <%
        }
    %>
	<br>
    <!-- Add a submit button -->
    <input type="submit" value="Save Changes">
</form>

</body>
</html>
