package signup;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import org.bson.Document;

import com.mongodb.client.*;
import com.mongodb.ConnectionString;

/**
 * Servlet implementation class SignupServlet
 */
public class SignupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		//Create connection string
		ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
		//Create a mongoDB client
		MongoClient myClient = MongoClients.create(uri);
		//Connect to database
		MongoDatabase database = myClient.getDatabase("SafeBite");
		//Select the collection test
		MongoCollection<Document> usersCollection = database.getCollection("Users");
		
		//Get parameters
		String username = request.getParameter("name");
		String email = request.getParameter("email");
		String dateOfBirth = request.getParameter("birthday");
		String country = request.getParameter("country");
		String psw = request.getParameter("psw");
		String gender = request.getParameter("gender");
		
		//Check if username already exists
        Document omonymous = usersCollection.find(new Document("user_name", username)).first();
        if (omonymous != null) {
            //It exists
        	out.println("Sorry! Username already exists. Please, choose a different one.");
			RequestDispatcher rd = request.getRequestDispatcher("signup.jsp");
			rd.include(request, response);
        }
        else {
            //Create document with new user data			
            Document newUser = new Document("user_name", username)
            		.append("email", email)
            		.append("password", psw)
            		.append("country", country)
            		.append("date_of_birth", dateOfBirth)
            		.append("gender", gender);

    		//Insert new user into users dataset
            usersCollection.insertOne(newUser);
            
            //Save name to associate subsequent diet setup
        	HttpSession hs=request.getSession();
			hs.setAttribute("uname", username);
            
            //Redirect to profile setting page
            response.sendRedirect("profile_setup.jsp");
        }
  
		myClient.close();
	}

}
