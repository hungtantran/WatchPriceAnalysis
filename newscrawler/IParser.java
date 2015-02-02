package newscrawler;

import daoconnection.Domain;

public interface IParser {
	// Parse the current document / link
	boolean parseDoc();
	
	// Methods below can only be called after the parseDoc() function is called
	
	// Check if the current document / link is an valid link or not
	boolean isValidLink(String link);
	
	// Check if the current document / link is a content link or not
	boolean isContentLink();
	
	// Get the current link
	String getLink();
	
	// Gets links inside content
	String[] getLinksInContent();
	
	// Get the string content of the document / link
	String getContent();
	
	// Get the domain of the document / link
	Domain getDomain();
	
	// Add content currently inside the parser into the database
	boolean addCurrentContentToDatabase() throws Exception;
}
