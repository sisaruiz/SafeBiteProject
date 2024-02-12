<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="model.User" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="mongo.MongoAggregations" %>
<%@ page import="model.Review" %>
<%@ page import="dao.ReviewDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.bson.Document" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Your Profile</title>
</head>
<body>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://cdn.jsdelivr.net/npm/moment@^2"></script>
<script src="https://cdn.jsdelivr.net/npm/chartjs-adapter-moment@^1"></script>


	<%
		String mongoConString = "mongodb://localhost:27017/";
		String mongoDB = "SafeBite";
		String mongoCol = "Reviews";
		MongoAggregations userAggregations = new MongoAggregations(mongoConString, mongoDB, mongoCol);
	    String username = (String) session.getAttribute("uname");
	    UserDAO userDAO = new UserDAO();
	    User user = userDAO.getUserByUsername(username);
	    ReviewDAO reviewDAO = new ReviewDAO();
	    Object reviewsObject = reviewDAO.getLastThreeReviewsWithDates(username);
	    List<Document> userRatingDistribution = userAggregations.getUserRatingDistributionOverTime(username);
    %>
    <h1>Your profile</h1>
    <img class="user-img" src=<%= user.getPic() %>>
    <p>Username:<%= user.getName() %></p>
    <p>Email:<%= user.getEmail() %></p>
    <p>Country:<%= user.getCountry() %></p>
    <p>Gender:<%= user.getGender() %></p>
    <p>Birthday:<%= user.getDOB() %></p>
    <p>Diet:<%= user.getDiet() %></p>
    <p>Allergens:<%= user.getAllergens() %></p>
    <br>
    <a href="likedProducts.jsp">Your liked products</a>
    <br>
    <a href="connections.jsp">Your connections</a>
    <br>
    <a href="editProfile.jsp">Edit profile</a>
    <br>
    <canvas id="lineChart" style="width: 400px; height: 300px;"></canvas>

<script>
    // Prepare data for Chart.js
    var labels = [];
    var data = [];

    <% for (Document document : userRatingDistribution) { %>
        labels.push('<%= new SimpleDateFormat("dd MMM yyyy").format(document.getDate("Review Date")) %>');
        data.push(<%= ((Number)document.get("averageRating")).doubleValue() %>); // Convert to double
    <% } %>

    // Configure the line chart
    var config = {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Average Rating',
                data: data,
                borderColor: 'blue',
                borderWidth: 2,
                fill: false
            }]
        },
        options: {
        	responsive: false, // Ensure the chart doesn't resize to fit the container
            maintainAspectRatio: false, // Maintain the aspect ratio
            scales: {
                x: {
                    type: 'category',
                    labels: labels
                },
                y: {
                    beginAtZero: false,
                    min: 1,
                    max: 5 // Assuming the rating scale is from 0 to 5
                }
            }
        }
    };

    // Create and render the chart
    var ctx = document.getElementById('lineChart').getContext('2d');
    var myChart = new Chart(ctx, config);
</script>
    
    <%
    if (reviewsObject != null && !((Map<?, ?>) reviewsObject).isEmpty()) {
        Map<String, Object> result = (Map<String, Object>) reviewsObject;
        List<Review> userReviews = (List<Review>) result.get("reviews");
        List<Date> reviewDates = (List<Date>) result.get("reviewDates");
    %>
            <p><strong>Your last reviews</strong></p>
            <%
            for (int i = 0; i < userReviews.size(); i++) {
                Review review = userReviews.get(i);
                Date date = reviewDates.get(i);
            %>
            	<p>Product: <%= review.getProductName() %></p>
            	<p>Date: <%= new SimpleDateFormat("dd MMMM yyyy").format(date) %></p>
       			<p>Heading: <%= review.getReviewHeading() %></p>
                <p>Review: <%= review.getReviewText() %></p>
                <br>
            <%
            }
            %>
    <%
    } else {
    %>
        <p>No reviews yet.</p>
    <%
    }
    %>

</body>
</html>
