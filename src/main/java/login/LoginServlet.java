package login;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.*;

/**
 * Servlet implementation class LoginServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String n = request.getParameter("t1");
		String pwd = request.getParameter("t2");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		//Create connection string
		ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
		//Create a mongoDB client
		MongoClient myClient = MongoClients.create(uri);
		//Connect to SafeBite database
		MongoDatabase database = myClient.getDatabase("SafeBite");
		//Select the collection test
		MongoCollection<Document> usersCollection = database.getCollection("Users");
		
        // Perform authentication logic
        Document user = usersCollection.find(new Document("user_name", n).append("password", pwd)).first();

        if (user != null) {
            // Authentication successful
        	HttpSession hs=request.getSession();
			hs.setAttribute("uname", n);
			response.sendRedirect("success.jsp");
        } else {
            // Authentication failed
        	out.println("<font color=red size=14 face=verdana>Sorry! username or password incorrect ... Try again!");
			RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
			rd.include(request, response);
        }
		
	}

}
