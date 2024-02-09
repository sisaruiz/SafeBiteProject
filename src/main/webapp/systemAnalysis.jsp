<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>

<!DOCTYPE html>
<html>
<head>
 <meta charset="UTF-8">
    <title>System Analysis</title>
    <style>
        .histogram-bar {
            margin: 5px;
            padding: 5px;
        }

        .histogram-bar1 {
            background-color: blue;
            color: white;
        }

        .histogram-bar2 {
            background-color: green; /* Change the color as needed */
            color: white;
        }
        .histogram-bar3 {
            background-color: orange; /* Change the color as needed */
            color: white;
        }
    </style>
</head>
<body>

<%! // Declaration tag for method
    // Function to calculate the width of the histogram bar based on followers count
    private int calculateHistogramWidth(long followersCount) {
        // Adjust the multiplier or any other logic based on your requirements
        return (int) (followersCount * 50); // Adjust the multiplier as needed
    }

//Function to calculate the width for the second section
private int calculateHistogramWidthForDiets(long userCount) {
    // Adjust the multiplier or any other logic based on your requirements
    return (int) (userCount * 7); // Adjust the multiplier as needed
}

//Function to calculate the width for the third section
private int calculateHistogramWidthForAllergens(long userCount) {
    // Adjust the multiplier or any other logic based on your requirements
    return (int) (userCount * 3); // Adjust the multiplier as needed
}


%>

<h1>System Analysis</h1>

<section>
    <h3>Most Popular Users</h3>
    <div class="histogram-container">
        <!-- Add your histogram display logic here -->
        <%
            // Initialize Neo4j Driver
            Neo4jManager neo4jManager = new Neo4jManager();

            // Get the most popular users with followers count
            List<String[]> popularUsersWithFollowers = neo4jManager.getMostPopularUsersWithFollowersCount();

            // Display the most popular users as a histogram in descending order
            for (String[] userWithFollowers : popularUsersWithFollowers) {
                String username = userWithFollowers[0];
                long followersCount = Long.parseLong(userWithFollowers[1]);
                int barWidth = calculateHistogramWidth(followersCount);
        %>
                <div class="histogram-bar histogram-bar1" style="width: <%= barWidth %>px;">
                    <span><%= username %></span>
                    <div class="bar-fill" style="width: <%= (followersCount * 5) %>px;"></div>
                    <span><%= followersCount %> Followers</span>
                </div>
        <%
            }
        %>
    </div>
</section>

<br>

<section>
    <h3>Most Followed Diets</h3>
    <div class="histogram-container">
        <!-- Add your horizontal bar chart display logic here -->
        <%
            List<String[]> mostFollowedDietsWithUserCount = neo4jManager.getMostFollowedDietsWithUserCount();

            // Display the most followed diets as a horizontal bar chart
            for (String[] dietWithUserCount : mostFollowedDietsWithUserCount) {
                String dietType = dietWithUserCount[0];
                long userCount = Long.parseLong(dietWithUserCount[1]);
                int barWidth = calculateHistogramWidthForDiets(userCount);
        %>
                <div class="histogram-bar histogram-bar2" style="width: <%= barWidth %>px;">
                    <span><%= dietType %></span>
                    <div class="bar-fill" style="width: <%= (userCount * 5) %>px;"></div>
                    <span><%= userCount %> Users</span>
                </div>
        <%
            }
        %>
    </div>
</section>

<br>
<section>
    <h3>Most Popular Allergens</h3>
    <div class="histogram-container">
        <!-- Add your histogram display logic here -->
        <%
            // Get allergens with the number of users allergic to each allergen
            List<String[]> allergensWithUserCount = neo4jManager.getAllergensWithUserCount();

            // Display allergens and users count as a histogram
            for (String[] allergenWithUserCount : allergensWithUserCount) {
                String allergenName = allergenWithUserCount[0];
                long userCount = Long.parseLong(allergenWithUserCount[1]);
                int barWidth = calculateHistogramWidthForAllergens(userCount);
        %>
                <div class="histogram-bar histogram-bar3" style="width: <%= barWidth %>px;">
                    <span><%= allergenName %></span>
                    <div class="bar-fill" style="width: <%= (userCount * 5) %>px;"></div>
                    <span><%= userCount %> Users</span>
                </div>
        <%
            }
        %>
    </div>
</section>


<br>
<!-- Add a button to navigate to systemAnalysis.jsp -->
<form action="adminHome.jsp">
    <input type="submit" value="Return Home">
</form>
<br>
</body>
</html>