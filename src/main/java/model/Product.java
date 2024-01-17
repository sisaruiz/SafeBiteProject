package model;

import java.time.LocalDateTime;
import utilities.Constants;

public class Product {
    
	// Attributes
	String id;
    String name;
    String imgURL;
    String productURL;

    // Quantity
    Double quantity;
    String measureUnit;

    // Nutrition
    String[] ingredients;
    String[] allergens;
    String[] traces;
    String[] labels;
    String category;

    // Production scope
    String brandOwner;
    String brand;
    String[] categories;
    String[] countries;

    // Entry log
    String creator;
    LocalDateTime entryTimestamp;

    // Last update
    String lastUpdateBy;
    LocalDateTime lastUpdateTimestamp;

    // Default constructor
    public Product() {
    	
        // Initialize default values
        this.name = "";
        this.imgURL = "";
        this.quantity = 0.0;
        this.measureUnit = "";
        this.ingredients = new String[0];
        this.allergens = new String[0];
        this.traces = new String[0];
        this.labels = new String[0];
        this.category = "";
        this.brandOwner = "";
        this.brand = "";
        this.categories = new String[0];
        this.countries = new String[0];
        this.creator = "";
        this.entryTimestamp = LocalDateTime.now();
        this.lastUpdateBy = "";
        this.lastUpdateTimestamp = LocalDateTime.now();
    }

    // Parameterized constructor
    public Product(String id, String name, String imgURL, Double quantity, String measureUnit,
                   String[] ingredients, String[] allergens, String[] traces, String[] labels, String category,
                   String brandOwner, String brand, String[] categories, String[] countries,
                   String creator, LocalDateTime entryTimestamp, String lastUpdateBy, LocalDateTime lastUpdateTimestamp) {
    	
        // Initialize all attributes
    	this.id = id;
        this.name = name;
        this.imgURL = imgURL;
        this.productURL = Constants.BASE_URL+this.id;
        this.quantity = quantity;
        this.measureUnit = measureUnit;
        this.ingredients = ingredients;
        this.allergens = allergens;
        this.traces = traces;
        this.labels = labels;
        this.category = category;
        this.brandOwner = brandOwner;
        this.brand = brand;
        this.categories = categories;
        this.countries = countries;
        this.creator = creator;
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
    
    

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
