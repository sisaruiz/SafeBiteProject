package model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dao.ReviewDAO;
import utilities.Constants;

public class Product {
    
	// Attributes
	String id;
    String name;
    String imgURL;
    String productURL;

    // Quantity
    String quantity;

    // Nutrition
    String ingredients;
    String allergens;
    String traces;
    String labels;
    String categories;

    // Production scope
    String brandOwner;
    String brand;
    String countries;

    // Entry log
    Date entryTimestamp;

    // Last update
    String lastUpdateBy;
    Date lastUpdateTimestamp;

    // Default constructor
    public Product() {
    	
        // Initialize default values
        this.name = "";
        this.imgURL = "";
        this.quantity = "";
        this.ingredients = "";
        this.allergens = "";
        this.traces = "";
        this.labels = "";
        this.brandOwner = "";
        this.brand = "";
        this.categories = "";
        this.countries = "";
        this.lastUpdateBy = "";
    }

    // Parameterized constructor
    public Product(String id, String name, String imgURL, String quantity, String categories,
                   String ingredients, String allergens, String traces, String labels,
                   String brandOwner, String brand, String countries,
                   Date entryTimestamp, String lastUpdateBy, Date lastUpdateTimestamp) {
    	
        // Initialize all attributes
    	this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.productURL = Constants.BASE_URL+this.id;
        this.quantity = quantity;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.traces = traces;
        this.labels = labels;
        this.brandOwner = brandOwner;
        this.brand = brand;
        this.categories = categories;
        this.countries = countries;
        this.entryTimestamp = entryTimestamp;
        this.lastUpdateBy = lastUpdateBy;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }
    
    // Browsing constructor
    public Product(String id, String name, String imgURL) {
    	this.id = id;
    	this.name = name;
        this.imgURL = imgURL;
        this.productURL = Constants.BASE_URL + id;
    }
    
 // Add this method to your Product class
    public List<Review> getReviews() {
        // Use the product ID to fetch reviews from the ReviewDAO
        ReviewDAO reviewDAO = new ReviewDAO();
        return reviewDAO.getReviewsByProductId(this.id);
    }
    
    // Helper method to convert Date to String
    private String convertDateToString(Date date) {
        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(date);
        }
        return null;
    }
    
    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getProductURL() {
        return productURL;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getAllergens() {
        return allergens;
    }

    public String getTraces() {
        return traces;
    }

    public String getLabels() {
        return labels;
    }

    public String getCategories() {
        return categories;
    }

    public String getBrandOwner() {
        return brandOwner;
    }

    public String getBrand() {
        return brand;
    }

    public String getCountries() {
        return countries;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public Date getEntryTS() {
    	return entryTimestamp;
    }
    
    public Date getLastUpdateTS() {
    	return lastUpdateTimestamp;
    }
    
    public String getStringLastUpdateTS() {
    	return convertDateToString(lastUpdateTimestamp);
    }
    
    public String getStringEntryTS() {
    	return convertDateToString(entryTimestamp);
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
    
    public void setQuantity(String q) {
        this.quantity = q;
    }
    
    public void setIngredients(String i) {
        this.ingredients = i;
    }
    
    public void setAllergens(String a) {
        this.allergens = a;
    }
    
    public void setTraces(String t) {
        this.traces = t;
    }
    
    public void setLabels(String l) {
        this.labels = l;
    }
    
    public void setCategories(String c) {
        this.categories = c;
    }
    
    public void setBrandOwner(String bo) {
        this.brandOwner = bo;
    }
    
    public void setBrand(String b) {
        this.brand = b;
    }
    
    public void setCountries(String c) {
        this.countries = c;
    }
    
    public void setLUB(String un) {
        this.lastUpdateBy = un;
    }
    
    public void setLUTS(Date d) {
        this.lastUpdateTimestamp = d;
    }
}
