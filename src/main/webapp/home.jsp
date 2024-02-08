<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="dao.Neo4jManager" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login success</title>
</head>
<body>
<%
	Neo4jManager neo4jManager = new Neo4jManager();
	if(session.getAttribute("uname")==null){
		response.sendRedirect("login.jsp");
	}
	
	String userName = (String) session.getAttribute("uname");
    List<String> potentialFriends = neo4jManager.findPotentialFriends(userName);

    request.setAttribute("potentialFriends", potentialFriends);
%>
<h2>
Welcome, <%=session.getAttribute("uname")%>
</h2>
<br>
<p><a href="yourProfile.jsp">Your profile</a></p>
<br>
<form action="logout.jsp">
    <input type="submit" value="Logout">
</form>
<br>
<a href="browseProducts.jsp">Search products</a>
<a href="browseUsers.jsp">Search users</a>
<section>
    <h3>Products you may want to try ...</h3>
</section>
<section>
    <h3>Explore potential connections ...</h3>
    <% if (potentialFriends != null) {
    	for (String potentialFriend : (List<String>) request.getAttribute("potentialFriends")) { %>
        <p><a href="userDetails.jsp?user=<%= potentialFriend %>"><%= potentialFriend %></a></p>
    <% }
    } else {
    	%> <p>Follow more users to get suggestions. </p> <% 
    }
     %>
</section>
<%
	neo4jManager.closeNeo4jConnection();
%>
</body>
</html>
