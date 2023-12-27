<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Sign Up Page</title>
  <link rel="stylesheet" href="style.css">
</head>
<body>
  <div id="signup-container">
    <h1>Sign Up</h1>
    <form action="register" method="post">
      <label for="username">Username:</label>
      <input type="text" id="username" name="username" required>
      <br>
      <label for="password">Password:</label>
      <input type="password" id="password" name="password" required>
      <br>
      <label for="confirmPassword">Confirm Password:</label>
      <input type="password" id="confirmPassword" name="confirmPassword" required>
      <br>
      <input type="submit" value="Sign Up">
    </form>
    <p>
      Already have an account? <a href="login.jsp">Login here.</a>
    </p>
  </div>
</body>
</html>
