package unitTest;

import java.net.UnknownHostException;
import java.util.Set;

import static org.junit.Assert.*;
import newscrawler.MainCrawler;

import org.jsoup.HttpStatusException;
import org.junit.Test;

import commonlib.NetworkingFunctions;
import commonlib.NetworkingFunctions.NetPkg;

public class NetworkingTest {
	@Test
	public void test() {
		MainCrawler.startUpState();
		testNetworkingError();
	}
	
	// Test the Helper.identifyTopicOfName method
	public void testNetworkingError() {
		String url = "http://www.chrono24.com/en/rolex/116515ln-oyster-perpetual-cosmograph-daytona-neu-lc-100--id2085130.htm";
		NetPkg pkg = NetworkingFunctions.downloadHtmlContent(url, 2);
		System.out.println(pkg.e.getClass());
		assertEquals(HttpStatusException.class, pkg.e.getClass());
		
		url = "http://www.chrodqwdno24.com/en/rolex/116515ln-oyster-perpetual-cosmograph-daytona-neu-lc-100--id2085130.htm";
		pkg = NetworkingFunctions.downloadHtmlContent(url, 2);
		System.out.println(pkg.e.getClass());
		assertEquals(UnknownHostException.class, pkg.e.getClass());
		
		url = "hasdasdttp://www.chrodqwdno24.com/en/rolex/116515ln-oyster-perpetual-cosmograph-daytona-neu-lc-100--id2085130.htm";
		pkg = NetworkingFunctions.downloadHtmlContent(url, 2);
		System.out.println(pkg.e.getClass());
		assertEquals(IllegalArgumentException.class, pkg.e.getClass());
		
		url = "http://www.google.com";
		pkg = NetworkingFunctions.downloadHtmlContent(url, 2);
		assertEquals(null, pkg.e);
	}
}
