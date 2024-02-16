<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SafeBite</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background-image: url('your_image_path.jpg'); /* Replace 'your_image_path.jpg' with the path to your background image */
        background-size: cover;
        background-position: center;
        background-repeat: no-repeat;
        margin: 0;
        padding: 0;
    }
    form {
        background-color: #fff;
        width: 300px;
        margin: 50px auto;
        padding: 20px;
        border-radius: 5px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }
    input[type="text"],
    input[type="password"] {
        width: 100%;
        padding: 10px;
        margin: 10px 0;
        border: 1px solid #ccc;
        border-radius: 3px;
        box-sizing: border-box;
    }
    input[type="submit"],
    input[type="reset"] {
        background-color: #4CAF50;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 3px;
        cursor: pointer;
        margin-right: 10px;
    }
    input[type="submit"]:hover,
    input[type="reset"]:hover {
        background-color: #45a049;
    }
    p {
        text-align: center;
        margin-top: 20px;
    }
    a {
        color: #007bff;
        text-decoration: none;
    }
    a:hover {
        text-decoration: underline;
    }
</style>
</head>
<body>
<form action=LoginServlet method=post>
<pre>
			Enter user-name:	<input type="text" name="t1">
			
			Enter password:		<input type="password" name="t2">
				<input type=submit value=Login>		<input type=reset>
</pre>
</form>
<p>Not registered yet? <a href="signup.jsp">Create</a> an account.</p>
</body>
</html>