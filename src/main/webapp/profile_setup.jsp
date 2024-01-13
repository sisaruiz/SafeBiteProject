<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Diet profile setup</title>
</head>
<body>

<h1>Diet preferences</h1>
<form action=DietServlet method=post>
    <h3>Diet type</h3>
	<select id="diet_type" name="diet" required>
		<option value="None">None</option>
		<option value="Vegan">Vegan</option>
		<option value="Vegetarian">Vegetarian</option>
		<option value="Pescatarian">Pescatarian</option>
		<option value="Halal">Halal</option>
	</select>
	<p>Please note that all products in the catalogue are already compliant with a celiac(gluten-free) diet.</p>
	<br>
	<h3>Allergens and intolerances</h3>
	<input type="checkbox" id="celery" name="allergens" value="celery" /><label for="celery">Celery</label>
	<input type="checkbox" id="wheat" name="allergens" value="wheat" /><label for="wheat">Wheat</label>	
	<input type="checkbox" id="rye" name="allergens" value="rye" /><label for="rye">Rye</label>
	<input type="checkbox" id="barley" name="allergens" value="barley" /><label for="barley">Barley</label>
	<input type="checkbox" id="oats" name="allergens" value="oats" /><label for="oats">Oats</label>	
	<input type="checkbox" id="spelt" name="allergens" value="spelt" /><label for="spelt">Spelt</label>
	<input type="checkbox" id="kamut" name="allergens" value="kamut" /><label for="kamut">Kamut</label>
	<input type="checkbox" id="cabbage" name="allergens" value="cabbage" /><label for="cabbage">Cabbage</label>
	<input type="checkbox" id="broccoli" name="allergens" value="broccoli" /><label for="broccoli">Broccoli</label>
	<input type="checkbox" id="cauliflower" name="allergens" value="cauliflower" /><label for="cauliflower">Cauliflower</label>
	<input type="checkbox" id="kale" name="allergens" value="kale" /><label for="kale">Kale</label>
	<input type="checkbox" id="brussel sprouts" name="allergens" value="brussel sprouts" /><label for="brussel sprouts">Brussel Sprouts</label>
	<input type="checkbox" id="collard greens" name="allergens" value="collard greens" /><label for="collard greens">Collard Greens</label>
	<input type="checkbox" id="crustaceans" name="allergens" value="crustaceans" /><label for="crustaceans">Crustaceans</label>
	<input type="checkbox" id="eggs" name="allergens" value="eggs" /><label for="eggs">Eggs</label>
	<input type="checkbox" id="fish" name="allergens" value="fish" /><label for="fish">Fish</label>
	<input type="checkbox" id="milk" name="allergens" value="milk" /><label for="milk">Milk (lactose)</label>
	<input type="checkbox" id="lupin" name="allergens" value="lupin" /><label for="lupin">Lupin</label>
	<input type="checkbox" id="molluscs" name="allergens" value="molluscs" /><label for="molluscs">Molluscs</label>
	<input type="checkbox" id="mustard" name="allergens" value="mustard" /><label for="mustard">Mustard</label>
	<input type="checkbox" id="nuts" name="allergens" value="nuts" /><label for="nuts">Nuts (e.g. almond)</label>
	<input type="checkbox" id="peanuts" name="allergens" value="peanuts" /><label for="peanuts">Peanuts</label>
	<input type="checkbox" id="sesame" name="allergens" value="sesame" /><label for="sesame">Sesame</label>
	<input type="checkbox" id="rye" name="allergens" value="soy" /><label for="soy">Soy</label>
	<p>If an allergen is not displayed here you can add it later in your profile settings.</p>
	<br>
	<input type=submit value="Set up profile">
</form>
</body>
</html>