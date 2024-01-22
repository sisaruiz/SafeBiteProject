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
        <option value="None">None</option>
		<option value="Vegan">Vegan</option>
		<option value="Vegetarian">Vegetarian</option>
		<option value="Pescatarian">Pescatarian</option>
		<option value="Halal">Halal</option>
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
    <!-- Container to append new checkboxes -->
	<div id="newAllergensContainer"></div>
    
    <!-- Add a text input for adding new allergen -->
    <input type="text" id="newAllergen" name="newAllergen" placeholder="Add New Allergen">
    <button type="button" onclick="addNewAllergen()">Add</button>
	<br>
	<br>
    <!-- Add a submit button -->
    <input type="submit" value="Save Changes">
</form>

<script>
    // Function to add a new allergen dynamically
    function addNewAllergen() {
        var newAllergenInput = document.getElementById("newAllergen");
        var newAllergen = newAllergenInput.value;

     	// Validate if the input is not empty
        if (newAllergen.trim() !== "") {
            // Create a new checkbox element
            var checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.id = newAllergen;
            checkbox.name = "allergens";
            checkbox.value = newAllergen;

            // Create a label for the new checkbox
            var label = document.createElement("label");
            label.htmlFor = newAllergen;
            label.appendChild(document.createTextNode(newAllergen));

            // Append the new checkbox and label to the container
            document.getElementById("newAllergensContainer").appendChild(checkbox);
            document.getElementById("newAllergensContainer").appendChild(label);

       		// Clear the input field
        	newAllergenInput.value = "";
       }
    }
</script>

</body>
</html>
