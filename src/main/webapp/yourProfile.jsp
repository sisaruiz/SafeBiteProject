<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="model.User" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="model.Review" %>
<%@ page import="dao.ReviewDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Your Profile</title>
</head>
<body>
<%
    String username = (String) session.getAttribute("uname");
    UserDAO userDAO = new UserDAO();
    User user = userDAO.getUserByUsername(username);
    ReviewDAO reviewDAO = new ReviewDAO();
    Object reviewsObject = reviewDAO.getLastThreeReviewsWithDates(username);

    %>
    <h1>Your profile</h1>
    <img class="user-img" src=<%= user.getPic() %>>
    <p>Username:<%= user.getName() %></p>
    <p>Email:<%= user.getEmail() %></p>
    <p>Country:<%= user.getCountry() %></p>
    <p>Gender:<%= user.getGender() %></p>
    <p>Birthday:<%= user.getDOB() %></p>
    <br>
    <p>Diet:<%= user.getDiet() %></p>
    <p>Allergens:<%= user.getAllergens() %></p>
    <br>
    <a href="editProfile.jsp">Edit Profile</a>
    <br>
    <%
    if (reviewsObject != null && !((Map<?, ?>) reviewsObject).isEmpty()) {
        Map<String, Object> result = (Map<String, Object>) reviewsObject;
        List<Review> userReviews = (List<Review>) result.get("reviews");
        List<Date> reviewDates = (List<Date>) result.get("reviewDates");
    %>
            <p>Your last reviews:</p>
            <%
            for (int i = 0; i < userReviews.size(); i++) {
                Review review = userReviews.get(i);
                Date date = reviewDates.get(i);
            %>
                <p>Review: <%= review.getReviewText() %></p>
                <p>Date: <%= new SimpleDateFormat("dd MMMM yyyy").format(date) %></p>
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
