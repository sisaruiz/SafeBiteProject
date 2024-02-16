<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>SafeBite</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-image: url('https://i.postimg.cc/25vRNkxQ/article-gf-grocerylist-hero-TTW-CR0-0-1139-641-1.jpg');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        form {
            background-color: #fff;
            width: 60%; /* Adjusted width */
            max-width: 400px; 
            padding: 40px; /* Adjusted padding */
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            text-align: center;
        }
        h1 {
            color: #8B4513; /* Dark brown color */
            text-align: center;
        }
        input[type="text"],
        input[type="password"] {
            width: 70%;
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
            color: #fff;
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
    <form action="LoginServlet" method="post">
        <h1>SafeBite</h1>
        <pre>
            Enter user-name:    <input type="text" name="t1">
            
            Enter password:     <input type="password" name="t2">
            <input type="submit" value="Login">        <input type="reset">
        </pre>
    </form>
    <br>
    <p>Not registered yet? <a href="signup.jsp">Create</a> an account.</p>
</body>
</html>
