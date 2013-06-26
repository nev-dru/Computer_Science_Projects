package sears_products;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Assignment.java takes in argument (keyword) and optional argument (page number).
 * Then searches the keyword as a query on the sears.com website. The results
 * of the query are stored in a Result object. Assignment.java then prints
 * results from the Result object to the console.
 * Assignment.java and Result.java are designed so that Assignment.java can
 * be modified in the future to create multiple Result objects with different 
 * keyword/page-number parameters and compare/print the Results efficiently.
 * 
 * @see Result
 * @see Product
 * 
 * @author Andrew Neville <br>
 *         June 20th 2013
 */
public class Assignment {
	
	/** 
	 * main method that validates user input and 
	 * prints results.
	 * 
	 * @param args - input arguments (keyword) [(page number)]
	 * @see Result
	 */
	public static void main(String[] args) {
		/* returns number of results for keyword args[0] */
		if (args.length == 1) {
			/* check that input is valid */
			System.out.println("keyword: " + args[0]);
			Result res = getResult(args[0],"1");
			if (checkResult(res)) {
				System.out.println("Searching \"" + 
			                        args[0] + 
			                        "\" resulted in " + 
			                        res.numResults() + " products.");
			}
			
		} else if (args.length == 2) {
			try {
				int pageNum = Integer.parseInt(args[1]);
				if (pageNum <= 0) {
					System.out.println("ERROR: Page number must be an integer larger than zero.");
					return;
				}
				Result res = getResult(args[0],pageNum + "");
				if (checkResult(res)) {
					System.out.println("********** All Product Results on page number " + pageNum + " **********");
					if (res.getProductInfo().equals("")) {
						System.out.println("No products found. Either keyword has no results OR page number is to large");
					} else {
						System.out.println(res.getProductInfo());
					}
				}
			} catch (NumberFormatException error) {
				System.out.println("NumberFormatException: Possible Error - Second argument Page number " + 
						           "must be an integer larger than zero.");
			}
		} else {
			System.out.println("ERROR: incorrect number of input arguments\n" + 
		                       "Format: java -jar Assignment.jar <keyword> [<page number>]");
		}
	}
	
	/**
	 * Checks if the Result object is null.
	 * If Result is null this method returns false, 
	 * otherwise it returns true.
	 * 
	 * @param res - Result object
	 * @return - boolean: true if res is not null, otherwise false
	 */
	private static boolean checkResult(Result res) {
		if (res == null) {
			System.out.println("\nERROR: failed to retrieve search results.");
			return false;
		}
		return true;
	}
	
	/**
	 * Uses the user input to connect to sears.com
	 * and create a new Result object. Contains all
	 * Error handling.
	 * 
	 * @param arg0 - keyword to search 
	 * @param arg1 - optional page number (default page size is 25 products)
	 * @return - a new Result object which contains information about 
	 * the product search results.
	 * 
	 * @see Result
	 */
	private static Result getResult(String arg0, String arg1) {
		HttpURLConnection conn = null;
		Result resultData;
		try {
			/* generate url */
			String encodeArg0 = URLEncoder.encode(arg0.trim(),"UTF-8");
			String encodeArg1 = URLEncoder.encode(arg1.trim(),"UTF-8"); 
			URL url = new URL("http://www.sears.com/search=" + 
						      encodeArg0 + "?keywordSearch=false&catPrediction=false" + 
						      "&previousSort=ORIGINAL_SORT_ORDER&pageNum=" + 
						      encodeArg1 + "&autoRedirect=false&viewItems=25");
			
			/* initialize connection */
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setReadTimeout(10000);
			conn.connect();
			
			/* get results */
			resultData = new Result(conn.getInputStream());
			
			return resultData;
			
		} catch (MalformedURLException error) {
			//error.printStackTrace();
			System.out.println("\nMalformedURLException: Check the URL object string");
			return null;
		} catch (UnsupportedEncodingException error) {
			//error.printStackTrace();
			System.out.println("\nUnsupportedEncodingException: Possible Error - issue trying to encode URLEncoder.encode");
			return null;
		} catch (ProtocolException error) {
			//error.printStackTrace();
			System.out.println("\nProtocolException: Possible Error - issue setting request method .setRequestMethod");
			return null;
		} catch (IllegalStateException error) {
			//error.printStackTrace();
			System.out.println("\nIllegalStateException: Possible Error - trying to connect when already connected");
			return null;
		} catch (IOException error) {
			//error.printStackTrace();
			System.out.println("\nIOException!!!");
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
}
