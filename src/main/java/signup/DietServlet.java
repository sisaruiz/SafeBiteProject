package signup;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

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
		
		//Add dietary profile to 
		//HEREEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
	}

}
