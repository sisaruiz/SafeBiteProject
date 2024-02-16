<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.google.gson.Gson" %>



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
            background-color: green; 
            color: white;
        }
        .histogram-bar3 {
            background-color: orange; 
            color: white;
        }

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f7f7f7;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        h3 {
            font-size: 24px;
            margin-bottom: 20px;
            color: #333;
        }

        .pie-chart-container {
            width: 400px;
            height: 400px;
            margin: 0 auto;
            margin-top: 20px;
        }
    </style>
</head>
<body>

<%! 
    // Function to calculate the width of the histogram bar based on followers count
    private int calculateHistogramWidth(long followersCount) {
        return (int) (followersCount * 50); 
    }

//Function to calculate the width for the second section
private int calculateHistogramWidthForDiets(long userCount) {
    return (int) (userCount * 7); 
}

//Function to calculate the width for the third section
private int calculateHistogramWidthForAllergens(long userCount) {
    return (int) (userCount * 3); 
}

%>

<h1>System Analysis</h1>

<section>
    <h3>Most Popular Users</h3>
    <div class="histogram-container">
        <%
            Neo4jManager neo4jManager = new Neo4jManager();

            List<String[]> popularUsersWithFollowers = neo4jManager.getMostPopularUsersWithFollowersCount();

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
<div class="pie-chart-container">
    <canvas id="pieChart" width="400" height="400"></canvas>
</div>

<%
    List<String[]> mostFollowedDietsWithUserCount = neo4jManager.getMostFollowedDietsWithUserCount();

    String[] dietTypes = new String[mostFollowedDietsWithUserCount.size()];
    int[] userCounts = new int[mostFollowedDietsWithUserCount.size()];
    int i = 0;
    for (String[] dietWithUserCount : mostFollowedDietsWithUserCount) {
        dietTypes[i] = dietWithUserCount[0];
        userCounts[i] = Integer.parseInt(dietWithUserCount[1]);
        i++;
    }
%>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
    var dietTypes = <%= new Gson().toJson(dietTypes) %>;
    var userCounts = <%= new Gson().toJson(userCounts) %>;

    var ctx = document.getElementById('pieChart').getContext('2d');
    var pieChart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: dietTypes,
            datasets: [{
                data: userCounts,
                backgroundColor: [
                    'rgba(255, 99, 132, 0.5)',
                    'rgba(54, 162, 235, 0.5)',
                    'rgba(255, 206, 86, 0.5)',
                    'rgba(75, 192, 192, 0.5)',
                    'rgba(153, 102, 255, 0.5)',
                    'rgba(255, 159, 64, 0.5)'
                ],
                borderColor: [
                    'rgba(255, 99, 132, 1)',
                    'rgba(54, 162, 235, 1)',
                    'rgba(255, 206, 86, 1)',
                    'rgba(75, 192, 192, 1)',
                    'rgba(153, 102, 255, 1)',
                    'rgba(255, 159, 64, 1)'
                ],
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            legend: {
                position: 'right',
            },
            tooltips: {
                callbacks: {
                    label: function (tooltipItem, data) {
                        var dataset = data.datasets[tooltipItem.datasetIndex];
                        var total = dataset.data.reduce(function (previousValue, currentValue, currentIndex, array) {
                            return previousValue + currentValue;
                        });
                        var currentValue = dataset.data[tooltipItem.index];
                        var percentage = Math.floor(((currentValue / total) * 100) + 0.5);
                        return dietTypes[tooltipItem.index] + ': ' + percentage + '%';
                    }
                }
            }
        }
    });
</script>
</section>

<br>
<section>
    <h3>Most Popular Allergens</h3>
    <div class="histogram-container">
        <%
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
<form action="adminHome.jsp">
    <input type="submit" value="Return Home">
</form>
<br>
</body>
</html>