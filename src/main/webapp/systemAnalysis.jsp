<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
 <meta charset="UTF-8">
    <title>System Analysis</title>
    <style>
        .histogram-bar {
            margin: 5px;
            background-color: blue;
            color: white;
            padding: 5px;
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

            // Close the Neo4j connection
            neo4jManager.closeNeo4jConnection();

            // Display the most popular users as a histogram in descending order
            for (String[] userWithFollowers : popularUsersWithFollowers) {
                String username = userWithFollowers[0];
                long followersCount = Long.parseLong(userWithFollowers[1]);
                int barWidth = calculateHistogramWidth(followersCount);
        %>
                <div class="histogram-bar" style="width: <%= barWidth %>px;">
                    <span><%= username %></span>
                    <div class="bar-fill" style="width: <%= (followersCount * 5) %>px;"></div>
                    <span><%= followersCount %> Followers</span>
                </div>
        <%
            }
        %>
    </div>
</section>

</body>
</html>