package model;

import java.util.ArrayList;
import java.util.List;

public class User extends Account {

	String picURL;
	String country;
	String dateOfBirth;
	String gender;
	String diet;
	List<String> allergens;
	List<String> friends;
	
	public User(String us, String em, String ps, String picURL,	String country, String dateOfBirth,
			String gender, String diet, List<String> f, List<String> l ) {
		super(us, em, ps);
		this.picURL = picURL;
		this.country = country;
		this.dateOfBirth = dateOfBirth;
		this.gender = gender;
		this.diet = diet;
		this.allergens = l;
		this.friends = f;
	}
	
	public User(String us) {
		super(us);	
	}
	
	// Getters
    public String getName() {
        return this.username;
    }
    
    public String getPic() {
        return this.picURL;
    }
    
    public String getDOB() {
        return this.dateOfBirth;
    }
    
    public String getEmail() {
        return this.email;
    }
    
    public String getGender() {
        return this.gender;
    }
    
    public String getDiet() {
        return this.diet;
    }
    
    public String getCountry() {
        return this.country;
    }

    public String getAllergens() {
    	List<String> stringList = this.allergens;

        // Using System.lineSeparator() as the separator
        String result = String.join(System.lineSeparator(), stringList);
        
        return result;
    }
    
    public List<String> getListAllergens() {
    	return this.allergens;
    }
    
    // Setters
    public void setName(String s) {
        this.username = s;
    }
    
    public void setPic(String url) {
    	this.picURL = url;
    }
    
    public void setDOB(String dob) {
        this.dateOfBirth = dob;
    }
    
    public void setGender(String g) {
        this.gender = g;
    }
    
    public void setDiet(String d) {
        this.diet = d;
    }
    
    public void setCountry(String c) {
        this.country = c;
    }

    public void setAllergens(List<String> l) {
    	this.allergens = l;
    }
    
    public List<String> getFriends() {
        return this.friends != null ? this.friends : new ArrayList<>();
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

}
