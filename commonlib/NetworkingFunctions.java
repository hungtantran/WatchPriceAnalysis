package commonlib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;

public class NetworkingFunctions {
	public static class NetPkg {
		public Document doc;
		public Exception e;
		
		public NetPkg (Document doc, Exception e) {
			this.doc = doc;
			this.e = e;
		}
	}
	
	// Download the html content into a private Document variable "doc"
	public static NetPkg downloadHtmlContent(String url, int numRetries) {
		if (url == null)
			return null;
		
		Exception result = null;
		Document doc = null;
		for (int i = 0; i < numRetries; i++) {
			try {
				Response response = Jsoup
						.connect(url)
						.userAgent(
								"Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
						.timeout(10000).followRedirects(true).execute();
				
				doc = response.parse();
			} catch (MalformedURLException e) {
				Globals.crawlerLogManager.writeLog("Try to download malformed url "+url);
				result = e;
				break;
			} catch (HttpStatusException e) {
				Globals.crawlerLogManager.writeLog("The response is error "+e.getStatusCode()+", http status exception from "+url);
				result = e;
				break;
			} catch (UnsupportedMimeTypeException e) {
				Globals.crawlerLogManager.writeLog("Unsupported mime from "+url);
				result = e;
				break;
			} catch (SocketTimeoutException e) {
				Globals.crawlerLogManager.writeLog("Socket time out from "+url);
				result = e;
			} catch (UnknownHostException e) {
				Globals.crawlerLogManager.writeLog("Unknown Host Exception from "+url);
				result = e;
				break;
			} catch (IllegalArgumentException e) {
				Globals.crawlerLogManager.writeLog("Malformed URL from "+url);
				result = e;
				break;
			} catch (IOException e) {
				Globals.crawlerLogManager.writeLog("Generic error from "+url);
				result = e;
			}
		}
		
		NetPkg response = new NetPkg(doc, result);

		return response;
	}

	// Send an http request given url and the parameters
	public static void sendHttpRequest(String requestUrl, String urlParameters) {
		URL dbUrl;
		HttpURLConnection connection = null;

		try {
			System.out.println("Request URL = " + requestUrl);
			dbUrl = new URL(requestUrl);
			connection = (HttpURLConnection) dbUrl.openConnection();
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
		}
	}

	public static String excuteGet(String executedUrl) {
		HttpURLConnection connection = null;

		try {
			// Create connection
			URL url = new URL(executedUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection
					.setRequestProperty("Accept",
							"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			connection.setRequestProperty("Accept-Encoding",
					"gzip,deflate,sdch");
			connection.setRequestProperty("Accept-Language",
					"en-US,en;q=0.8,de;q=0.6,vi;q=0.4");
			connection.setRequestProperty("Cache-Control", "max-age=0");
			connection.setRequestProperty("Connection", "keep-alive");
			connection
					.setRequestProperty(
							"User-Agent",
							"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");

			// connection.setUseCaches (false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.flush();
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			// System.out.println(response.toString());
			return response.toString();

		} catch (Exception e) {
			Globals.crawlerLogManager.writeLog(e.getMessage());
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
}
