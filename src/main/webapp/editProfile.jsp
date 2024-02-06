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
	<input type="checkbox" id="celery" name="allergens" value="celery" <%=(user.getAllergens().contains("celery")) ? "checked" : "" %>/><label for="celery">Celery</label>
	<input type="checkbox" id="wheat" name="allergens" value="wheat" <%=(user.getAllergens().contains("wheat")) ? "checked" : "" %>/><label for="wheat">Wheat</label>	
	<input type="checkbox" id="rye" name="allergens" value="rye" <%=(user.getAllergens().contains("rye")) ? "checked" : "" %>/><label for="rye">Rye</label>
	<input type="checkbox" id="barley" name="allergens" value="barley" <%=(user.getAllergens().contains("barley")) ? "checked" : "" %>/><label for="barley">Barley</label>
	<input type="checkbox" id="oats" name="allergens" value="oats" <%=(user.getAllergens().contains("oats")) ? "checked" : "" %>/><label for="oats">Oats</label>	
	<input type="checkbox" id="spelt" name="allergens" value="spelt" <%=(user.getAllergens().contains("spelt")) ? "checked" : "" %>/><label for="spelt">Spelt</label>
	<input type="checkbox" id="kamut" name="allergens" value="kamut" <%=(user.getAllergens().contains("kamut")) ? "checked" : "" %>/><label for="kamut">Kamut</label>
	<input type="checkbox" id="cabbage" name="allergens" value="cabbage" <%=(user.getAllergens().contains("cabbage")) ? "checked" : "" %>/><label for="cabbage">Cabbage</label>
	<input type="checkbox" id="broccoli" name="allergens" value="broccoli" <%=(user.getAllergens().contains("broccoli")) ? "checked" : "" %>/><label for="broccoli">Broccoli</label>
	<input type="checkbox" id="cauliflower" name="allergens" value="cauliflower" <%=(user.getAllergens().contains("cauliflower")) ? "checked" : "" %>/><label for="cauliflower">Cauliflower</label>
	<input type="checkbox" id="kale" name="allergens" value="kale" <%=(user.getAllergens().contains("kale")) ? "checked" : "" %>/><label for="kale">Kale</label>
	<input type="checkbox" id="brussel sprouts" name="allergens" value="brussel sprouts" <%=(user.getAllergens().contains("brussel sprouts")) ? "checked" : "" %>/><label for="brussel sprouts">Brussel Sprouts</label>
	<input type="checkbox" id="collard greens" name="allergens" value="collard greens" <%=(user.getAllergens().contains("collard greens")) ? "checked" : "" %>/><label for="collard greens">Collard Greens</label>
	<input type="checkbox" id="crustaceans" name="allergens" value="crustaceans" <%=(user.getAllergens().contains("crustaceans")) ? "checked" : "" %>/><label for="crustaceans">Crustaceans</label>
	<input type="checkbox" id="eggs" name="allergens" value="eggs" <%=(user.getAllergens().contains("eggs")) ? "checked" : "" %>/><label for="eggs">Eggs</label>
	<input type="checkbox" id="fish" name="allergens" value="fish" <%=(user.getAllergens().contains("fish")) ? "checked" : "" %>/><label for="fish">Fish</label>
	<input type="checkbox" id="milk" name="allergens" value="milk" <%=(user.getAllergens().contains("milk")) ? "checked" : "" %>/><label for="milk">Milk (lactose)</label>
	<input type="checkbox" id="lupin" name="allergens" value="lupin" <%=(user.getAllergens().contains("lupin")) ? "checked" : "" %>/><label for="lupin">Lupin</label>
	<input type="checkbox" id="molluscs" name="allergens" value="molluscs" <%=(user.getAllergens().contains("molluscs")) ? "checked" : "" %>/><label for="molluscs">Molluscs</label>
	<input type="checkbox" id="mustard" name="allergens" value="mustard" <%=(user.getAllergens().contains("mustard")) ? "checked" : "" %>/><label for="mustard">Mustard</label>
	<input type="checkbox" id="nuts" name="allergens" value="nuts" <%=(user.getAllergens().contains("nuts")) ? "checked" : "" %>/><label for="nuts">Nuts (e.g. almond)</label>
	<input type="checkbox" id="peanuts" name="allergens" value="peanuts" <%=(user.getAllergens().contains("peanuts")) ? "checked" : "" %>/><label for="peanuts">Peanuts</label>
	<input type="checkbox" id="sesame" name="allergens" value="sesame" <%=(user.getAllergens().contains("sesame")) ? "checked" : "" %>/><label for="sesame">Sesame</label>
	<input type="checkbox" id="rye" name="allergens" value="soy" <%=(user.getAllergens().contains("soy")) ? "checked" : "" %>/><label for="soy">Soy</label>
	<input type="checkbox" id="apple" name="allergens" value="apple" <%=(user.getAllergens().contains("apple")) ? "checked" : "" %>/><label for="apple">Apple</label>
	<input type="checkbox" id="tomato" name="allergens" value="tomato" <%=(user.getAllergens().contains("tomato")) ? "checked" : "" %>/><label for="tomato">Tomato</label>
	<input type="checkbox" id="peaches" name="allergens" value="peaches" <%=(user.getAllergens().contains("peaches")) ? "checked" : "" %>/><label for="peaches">Peaches</label>
	<br>
    <!-- Add a submit button -->
    <input type="submit" value="Save Changes">
</form>

</body>
</html>
