<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.User" %>
<%@ page import="dao.Neo4jManager" %>

<%
  // Assuming Neo4jManager is a singleton, you can get an instance
  Neo4jManager neo4jManager = new Neo4jManager();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Your Connections</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f4;
            margin: 20px;
        }

        h1, h2 {
            color: #333;
        }

        a {
            text-decoration: none;
            color: #007bff;
            margin-right: 10px;
        }

        p {
            color: #888;
        }
    </style>
</head>
<body>
    <h1>Your Connections</h1>

    <h2>Followers</h2>
    <%
      String currentUsername = (String) session.getAttribute("uname");
      List<String> followers = neo4jManager.getUserFollowers(currentUsername);

      if (followers != null && !followers.isEmpty()) {
        for (String follower : followers) {
    %>
          <a href="userDetails.jsp?user=<%= follower %>"><%= follower %></a>
    <%
        }
      } else {
    %>
        <p>No followers</p>
    <%
      }
    %>

    <h2>Following</h2>
    <%
      List<String> following = neo4jManager.getUserFollowing(currentUsername);

      if (following != null && !following.isEmpty()) {
        for (String followee : following) {
    %>
          <a href="userDetails.jsp?user=<%= followee %>"><%= followee %></a>
    <%
        }
      } else {
    %>
        <p>Not following anyone</p>
    <%
      }
    %>

    <%
      neo4jManager.closeNeo4jConnection();
    %>
</body>
</html>
