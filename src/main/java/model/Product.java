package model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

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

    public String getEntryTS() {
        return convertDateToString(entryTimestamp);
    }

    public String getLastUpdateTS() {
        return convertDateToString(lastUpdateTimestamp);
    }

    
    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
