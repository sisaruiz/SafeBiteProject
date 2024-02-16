<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="model.User" %>
<%@ page import="dao.ReviewDAO" %>
<%@ page import="model.Review" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User profile</title>
<style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            padding: 20px;
        }
        h1 {
            color: #333;
            font-size: 24px;
            margin-bottom: 10px;
        }
        .user-img {
            width: 100px;
            height: 100px;
            border-radius: 50%;
            margin-bottom: 20px;
        }
        p {
            margin-bottom: 5px;
        }
        form {
            margin-top: 20px;
        }
        input[type="submit"], input[type="button"] {
            padding: 10px 20px;
            background-color: #007bff;
            color: #fff;
            border: none;
            cursor: pointer;
            border-radius: 5px;
        }
        input[type="submit"]:disabled, input[type="button"]:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        .review {
            border-bottom: 1px solid #ccc;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
</style>
</head>
<body>
	<%
	    String username = request.getParameter("user");
	    UserDAO userDAO = new UserDAO();
	    User user = userDAO.getUserByUsername(username);
	    ReviewDAO reviewDAO = new ReviewDAO();
	    Object reviewsObject = reviewDAO.getLastThreeReviewsWithDates(username);
	%>
	<h1><%= user.getName()%></h1>
	<img class="user-img" src=<%= user.getPic() %>>
	<p>Username:<%= user.getName() %></p>
	<p>Country:<%= user.getCountry() %></p>
	<p>Gender:<%= user.getGender() %></p>
	<p>Diet:<%= user.getDiet() %></p>
    <p>Allergens:<%= user.getAllergens() %></p>
    <br>
    
    <%	Neo4jManager neo4j = new Neo4jManager();
	    String visitorUsername = (String) request.getSession().getAttribute("uname");
	    boolean follows = neo4j.checkNeo4jFollowRelationship(visitorUsername, user.getName());
	    neo4j.closeNeo4jConnection();
	%>
	<form action="FollowServlet" method="post">
	    <input type="hidden" name="userToFollow" value="<%= user.getName() %>">
    <% if (follows) { %>
        <input type="submit" value="Unfollow">
        <input type="hidden" name="type" value="unfollow">
    <% } else { %>
        <input type="submit" value="Follow">
        <input type="hidden" name="type" value="follow">
    <% } %>
	</form>
    <br>
    <%
    if (reviewsObject != null && !((Map<?, ?>) reviewsObject).isEmpty()) {
        Map<String, Object> result = (Map<String, Object>) reviewsObject;
        List<Review> userReviews = (List<Review>) result.get("reviews");
        List<Date> reviewDates = (List<Date>) result.get("reviewDates");
    %>
            <p><strong><%= user.getName()%>'s last reviews</strong></p>
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