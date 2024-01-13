package signup;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Servlet implementation class DietServlet
 */
public class DietServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Create connection string
		ConnectionString uri = new ConnectionString("mongodb://localhost:27017");
		//Create a mongoDB client
		MongoClient myClient = MongoClients.create(uri);

		//Connect to database
		MongoDatabase database = myClient.getDatabase("SafeBite");
		//Select the collection test
		MongoCollection<Document> usersCollection = database.getCollection("Users");
		
		//Get parameter values
		HttpSession session = request.getSession(false);
		if (session == null) {
		    // Redirect to login page
		    response.sendRedirect("login.jsp");
		}
		String username = (String) session.getAttribute("uname");
		String diet = request.getParameter("diet");
		String[] array = request.getParameterValues("allergens");
		List<String> allergies = null;
		if (array != null) {
		    allergies = Arrays.asList(array);
		} else {
		    allergies = new ArrayList<>();
		}

		//Update related doc
		usersCollection.updateOne(new Document("user_name", username), new Document("$set",
		    new Document("diet_type", diet)
		        .append("allergy", new Document("allergens", allergies))));

		
		//Redirect to success page
		response.sendRedirect("success.jsp");
		
		myClient.close();
	}

}
