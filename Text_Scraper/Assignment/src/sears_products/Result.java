package sears_products;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* HTML parsing */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * Result object is built to receive the input HTML stream
 * from searching sears.com products. The Result object extracts
 * desired information about the search query results. Result.java
 * is meant for use with Product.java and Assignment.java 
 * 
 * @see Product
 * @see Assignment
 * @see <a href="http://www.jsoup.org">Jsoup HTML parser</a>
 * 
 * @author Andrew Neville <br>
 *         June 20th 2013
 */
public class Result {
	/* element tags for text scraping sears.com */
	private static final String ALL_CLASS_TAG      = "div.cardContainer";
	private static final String PRODUCT_CLASS_TAG  = "div.cardContainer > div.cardInner > div.cardProdTitle > h4 > a";
	private static final String PRICE_CLASS_TAG    = "div.cardContainer > div.cardInner > div.cardProdPricing_v2 > span.price_v2";
	private static final String VENDOR_ID_TAG      = "div.cardContainer > div.cardInner > div#mrkplc > p:not([class])";
	private static final String NUM_RESULTS_ID_TAG = "div#nmbProdItems";
	
	/*
	 * Result fields
	 */
	private Set<Product> results;
	
	private Document dom;
	private String size;
	
	/**
	 * Constructor for new Result object.
	 * 
	 * @param inputStream - InputStream HTML results from searching a website
	 * @throws IOException
	 */
	public Result(InputStream inputStream) throws IOException {
		this.results = new HashSet<Product>();
		this.size = null;
		getData(inputStream);
	}

	/**
	 * Reads the HTML data from the InputStream and converts it to a String.
	 * Then generates a DOM Document structure using Jsoup to parse the HTML content.
	 * 
	 * @param inputStream - InputStream HTML results from searching a website
	 * @throws IOException
	 * @see <a href="http://www.jsoup.org">Jsoup HTML parser</a>
	 */
	public void getData(InputStream inputStream) throws IOException {
		StringBuilder data = new StringBuilder("");
		this.clearResults();
		BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while ((line = buffReader.readLine()) != null) {
			if (!line.trim().equals("")) {
				data.append(line + '\n');
			}
		}
		/* Jsoup converts a String to a DOM document */
		dom = Jsoup.parse(data.toString());
	}

	/**
	 * Queries the Result object DOM structure for
	 * all the desired Product information (Title/Price/Vendor).
	 * 
	 * @see <a href="http://www.jsoup.org">Jsoup HTML parser</a>
	 */
	private void parseProductInfo() {
		// parent DOM element for all products
		Elements allProducts = this.dom.select(ALL_CLASS_TAG);
		for (Element prod : allProducts) {
			Document p = Jsoup.parse(prod.toString());
			String title  = p.select(PRODUCT_CLASS_TAG).text();
			String price  = p.select(PRICE_CLASS_TAG).text();
			String vendor = p.select(VENDOR_ID_TAG).text();
			/* if no vendor tag exists, product is sold by sears */
			vendor = (vendor.equals("")) ? "Sold by Sears" : vendor;
			this.addResult(title, price, vendor);
		}
		
	}

	/**
	 * Searches the result document for the
	 * ID of the element that contains the information
	 * about the number of search results found. Then
	 * uses regular expressions to parse that string for
	 * the total number of products.
	 * 
	 * @see <a href="http://www.jsoup.org">Jsoup HTML parser</a>
	 */
	private void parseProductCount() {
		String numProd = "";
		String regex = "\\d+\\s*\\+?$";
		Elements numProdElem = this.dom.select(NUM_RESULTS_ID_TAG);
	    if (!numProdElem.isEmpty()) {
	    	numProd = numProdElem.first().text();
	    }
    	Matcher m = Pattern.compile(regex).matcher(numProd);
	    if (!m.find()) 
	        System.out.println("No match found.");
	    else {
	        numProd = m.group(0);
	    }
		this.size = (numProd.equals("")) ? "0" : numProd;
	}

	/**
	 * Gets the Product results in the Result object.
	 * 
	 * @return - Set of all Product objects in the Result
	 * @see Product
	 */
	public Set<Product> getProductSet() {
		if (this.results.isEmpty()) {
			parseProductInfo();
		} 
		return this.results;
	}
	
	/**
	 * Gets all desired Product information from the DOM
	 * structure and stores the data in the Result object.
	 * If this method is called by the user more than once, 
	 * and the Result object still contains the previous data,
	 * then this method will just return the string representation 
	 * of the Product info without a repetitive query. 
	 * 
	 * @return - A String representation of
	 *           all of the Products in the Result object
	 *           Product set.
	 */
	public String getProductInfo() {
		if (this.results.isEmpty()) {
			parseProductInfo();
		}
		return this.toString();
	}
	
	/**
	 * Gets the total number of products from the DOM structure.
	 * If this method is called by the user more than once, and
	 * the size field is already set, it will just return the Result
	 * objects size field without a repetitive query.
	 * 
	 * @return - A String representation of the total number of 
	 *           Products in the Result objects Product set.
	 */
	public String numResults() {
		if (this.size == null) {
			parseProductCount();
		}
		return this.size;
	}	
	
	/**
	 * Adds a new Product result to the Result set
	 * 
	 * @param title  - Product title / name
	 * @param price  - Product price
	 * @param vendor - Product vendor
	 */
	private void addResult(String title, String price, String vendor) {
		if (!(title.equals("") || price.equals(""))) {
			Product newProduct = new Product(title,price,vendor);
			this.results.add(newProduct);
		}
	}
	
	
	
	/**
	 * Removes all elements in the current Result set
	 */
	public void clearResults() {
		this.size = null;
		this.results.clear();
	}
	
	/**
	 * toString override method for the Result 
	 */
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder("");;
		Iterator<Product> allResults = this.results.iterator();
		while (allResults.hasNext()) {
			output.append(allResults.next().toString() + '\n');
		}
		return output.toString();
	}
	
	/**
	 * Determines if two Product objects are 
	 * equal (same title/price/vendor)
	 * 
	 * @param other - should be an object of type Product
	 * @see Product
	 */
	@Override
	public boolean equals(Object other) {
		return ((other instanceof Result) &&
    		    ((Result)other).results.equals(this.results));
	}
}
