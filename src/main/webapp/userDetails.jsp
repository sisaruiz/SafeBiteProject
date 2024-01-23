<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="dao.UserDAO" %>
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
    
    <%
	    String visitorUsername = (String) request.getSession().getAttribute("uname");
	    boolean areFriends = userDAO.areFriends(visitorUsername, user.getName());
	%>
	<form action="FriendServlet" method="post">
	    <input type="hidden" name="userToAdd" value="<%= user.getName() %>">
    <% if (areFriends) { %>
        <input type="button" value="Already friends" disabled>
    <% } else { %>
        <input type="submit" value="Be friend">
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