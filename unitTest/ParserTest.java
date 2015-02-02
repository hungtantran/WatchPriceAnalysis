package unitTest;

import static org.junit.Assert.assertEquals;
import newscrawler.CrawlerParserFactory;
import newscrawler.IParser;

import org.junit.Test;

import commonlib.Globals;
import commonlib.LogManager;

import daoconnection.DAOFactory;

public class ParserTest {
	LogManager crawlerLogManager = null;
	DAOFactory daoFactory = null;

	@Test
	public void test() throws Exception {
		this.crawlerLogManager = new LogManager("testCrawlerLog", "testCrawlerLog");
		this.daoFactory = DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database);

		this.TestHodinkeeParser();
		this.TestChrono24Parser();
		this.TestABlogToWatchParser();
		this.TestWatchReportParser();
	}

	private void TestHodinkeeParser() throws Exception {
		assertEquals(true, this.TestParserInternal("http://www.hodinkee.com/blog/graham-honors-namesake-with-new-geograham-tourbillon"));
		assertEquals(false, this.TestParserInternal("http://www.hodinkee.com/blog/graham-honors-namesake-with-new-geograham-tourbillon?offset=4"));
		assertEquals(false, this.TestParserInternal("http://www.hodinkee.com"));
	}

	private void TestChrono24Parser() {

	}

	private void TestABlogToWatchParser() throws Exception {
		assertEquals(true, this.TestParserInternal("http://www.ablogtowatch.com/buying-watches-boston-massachusetts-shreve-crump-low/"));
		assertEquals(false, this.TestParserInternal("http://www.ablogtowatch.com"));
	}

	private void TestWatchReportParser() throws Exception {
		assertEquals(true, this.TestParserInternal("http://watchreport.com/jaquet-droz-releases-three-limited-edition-paillonne-enamel-watches/"));
		assertEquals(false, this.TestParserInternal("http://watchreport.com"));
	}

	private boolean TestParserInternal(String link) throws Exception {
		CrawlerParserFactory parserFactory = new CrawlerParserFactory(this.daoFactory);

		IParser parser = parserFactory.getParser(link, null, this.crawlerLogManager, null);

		return parser.parseDoc();
	}
}
