<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Login Page</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>
  <div id="login-container">
    <h1>Login</h1>
    <form action="loginServlet" method="post">
      <label for="username">Username:</label>
      <input type="text" id="username" name="username" required>
      <br>
      <label for="password">Password:</label>
      <input type="password" id="password" name="password" required>
      <br>
      <input type="submit" value="Login">
    </form>
    <p>
      Not registered yet? <a href="signup.jsp">Create a new account.</a>
    </p>
  </div>
</body>
</html>
