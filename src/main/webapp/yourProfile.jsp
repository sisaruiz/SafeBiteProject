<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="dao.UserDAO" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
        String username = (String) session.getAttribute("uname");
        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);
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
<p>Your last reviews:</p>

<input type="submit" value="Edit Profile">

</body>
</html>
