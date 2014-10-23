package commonlib;

import dbconnection.MySqlConnection;

public class HTMLCompressor {
	public HTMLCompressor() {
	}
	
	// Remove html content
	public static String removeHtmlComment(String htmlContent) {
		if (htmlContent == null)
			return null;
		
		String openComment = "<!--";
		String closeComment = "-->";
		String compressedHtmlContent = "";
		
		while (true) {
			int index = htmlContent.indexOf(openComment);
			
			if (index != -1) {
				String part = htmlContent.substring(0, index);
				compressedHtmlContent += part;
				
				int closeIndex = htmlContent.indexOf(closeComment);
				if (closeIndex != -1) {
					htmlContent = htmlContent.substring(closeIndex+closeComment.length());
				} else {
					break;
				}
			} else {
				compressedHtmlContent += htmlContent;
				break;
			}
		}
		
		return compressedHtmlContent;
	}
	
	// Remove white spaces and new lines
	public static String trimLineWhiteSpace(String htmlContent) {
		if (htmlContent == null)
			return null;
		
		String compressedHtml = "";
		String lines[] = htmlContent.split("\\r?\\n");
		
		for (int i = 0; i < lines.length; i++) {
			compressedHtml += lines[i].trim();
		}
		
		return compressedHtml;
	}
	
	public static String compressHtmlContent(String htmlContent) {
		if (htmlContent != null) {
			Globals.crawlerLogManager.writeLog("Original length = "+htmlContent.length());
			htmlContent = HTMLCompressor.removeHtmlComment(htmlContent);
			htmlContent = HTMLCompressor.trimLineWhiteSpace(htmlContent);
			Globals.crawlerLogManager.writeLog("Compressed length = "+htmlContent.length());
		} else {
			Globals.crawlerLogManager.writeLog("Error: attempted to compress null string");
		}
		
		return htmlContent;
	}
	
	public static void main(String[] args) {
		MySqlConnection mysqlConnection = new MySqlConnection();
		String content = mysqlConnection.getArticleContent(1);
		HTMLCompressor.compressHtmlContent(content);
	}
}
