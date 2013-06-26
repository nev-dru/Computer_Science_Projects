package sears_products;

/**
 * Holds the data for a single product result. 
 * Product data consists of:
 * Title, Price and Vendor.
 * Currently meant for use with Result.java and Assignment.java
 * for text scraping sears.com 
 * 
 * @see Result
 * @see Assignment
 * 
 * @author Andrew Neville <br>
 *         June 20th 2013
 *
 */
public class Product {
	/*
	 * Product fields
	 */
	private String title;
	private String price;
	private String vendor;
	
	
	/**
	 * constructor for a new Product object
	 * 
	 * @param title - Product title / name
	 * @param price - Product price
	 * @param vendor - Product vendor
	 */
	public Product(String title, String price, String vendor) {
		this.title = title.trim();
		this.price = price.trim();
		this.vendor = vendor.trim();
	}
	
	/**
	 * constructor for a new Product object
	 * with no title, price or vendor
	 */
	public Product() {
		this("","","");
	}

	/**
	 * Test if the Product fields match the given parameters.
	 * For use in debugging and the Product equals method.
	 * 
	 * @param title - Product title / name
	 * @param price - Product price
	 * @param vendor - Product vendor
	 * @return - true if this objects private fields (title/price/vendor) match the
	 *           string parameters
	 */
	private boolean testFields(String title, String price, String vendor) {
		return this.title.toLowerCase().equals(title.toLowerCase()) &&
			   this.price.toLowerCase().equals(price.toLowerCase()) &&
			   this.vendor.toLowerCase().equals(vendor.toLowerCase());
	}

	/**
	 * sets the title field for this product
	 *  
	 * @param title - name of the product
	 */
	public void setTitle(String title) {
		this.title = title.trim();
	}
	
	/**
	 * sets the price field for this product 
	 * 
	 * @param price - price of the product
	 */
	public void setPrice(String price) {
		this.price = price.trim();
	}
	
	/**
	 * sets the vendor field for this product 
	 * 
	 * @param vendor - vendor of the product
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor.trim();
	}
	
	/**
	 * toString method override for Product object
	 * 
	 * @return string representation of a single product 
	 * 
	 * <pre>
	 *  Title: (title/name of product)
	 *  Price: (price of product)
	 * Vendor: (seller/vendor of product)
	 * </pre>
	 */
	@Override
	public String toString() {
		return "\n----Product Information----" +
			   "\n Title:  "  + this.title + 
			   "\n Price:  "  + this.price +
			   "\nVendor:  "  + this.vendor;
	}
	
	/**
	 * Two products are considered equal 
	 * if they have the same title, price and vendor
	 * 
	 * @param other - should be an object of type Product
	 * @see Product
	 * @return - boolean
	 */
	@Override
	public boolean equals(Object other) {
		return ((other instanceof Product) &&
    		    ((Product)other).testFields(this.title, this.price, this.vendor));
	}
}
