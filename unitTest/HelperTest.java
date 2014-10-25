package unitTest;

import static org.junit.Assert.*;

import java.util.Set;

import newscrawler.MainCrawler;

import org.junit.Test;

import commonlib.Globals;
import commonlib.Helper;

public class HelperTest {

	@Test
	public void test() {
		MainCrawler.startUpState();
		testTopicIdentifier();
		// testTopicIdentifierHard();
	}
	
	// Test the Helper.identifyTopicOfName method
	public void testTopicIdentifier() {
		Set<String> resultedTopic = null;
		
		// Test null
		assertEquals(null, Helper.identifyTopicOfName(null, Globals.HOROLOGYTOPICS));
		assertEquals(null, Helper.identifyTopicOfName("Rolex Daytona", null));
		assertEquals(null, Helper.identifyTopicOfName(null, null));
		
		// Test simple with stop word like steel
		resultedTopic = Helper.identifyTopicOfName("Rolex Submariner Stainless Steel PVD/DLC Black Index Dial Black 60min Bezel Oyster Band", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		
		// Test with complicated name like A. Lange & Sohne or FP Journe
		resultedTopic = Helper.identifyTopicOfName("A. Lange & Söhne 1815 Chronograph Flyback Weißgold 402.026", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("A.Lange & Sohne"));
		
		resultedTopic = Helper.identifyTopicOfName("F.P.Journe Octa Sport Indy 500, Black Dial, Limited Edition to 99 Pieces - Black Aluminium on Strap", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("FP Journe"));
		
		// Test stop word like original
		resultedTopic = Helper.identifyTopicOfName("Rolex 1803 DATE-DAY 18K Gold with Original Silver Linen Sigma Dial", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		
		resultedTopic = Helper.identifyTopicOfName("Piaget Protocle Gent's Original \"Black Tie\" Watch 18K Yellow Gold", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Piaget"));
		
		// Test uppercase, lowercase difference
		resultedTopic = Helper.identifyTopicOfName("ROLEX Submariner Stainless Steel PVD/DLC Black Index Dial Black 60min Bezel Oyster Band", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		
		// Test with 2 brands with the same word Glashütte: Glashütte Original, Union Glashütte. Test accent removal
		resultedTopic = Helper.identifyTopicOfName("Glashütte Original PanoMatic Chrono XL 95-01-03-03-04 Pre-Owned", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Glashutte Original"));
		
		resultedTopic = Helper.identifyTopicOfName("Union Glashütte CHRONOGRAPH LIMITIERT MANUFAKTUR", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Union Glashutte"));
		
		// Test with 2 full brands
		resultedTopic = Helper.identifyTopicOfName("Tudor Certified Pre-Owned Stainless Steel & Yellow Gold Rolex- Princess Date", Globals.HOROLOGYTOPICS);
		assertEquals(2, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		assertTrue(resultedTopic.contains("Tudor"));
		
		// Test with mix upper and lower case and half match branch (Zenith => Zenith (LVMH))
		resultedTopic = Helper.identifyTopicOfName("Rolex DAYTONA STEEL ZENiTH MOVEMENT -NEW- BLACK DiAL", Globals.HOROLOGYTOPICS);
		assertEquals(2, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		assertTrue(resultedTopic.contains("Zenith (LVMH)"));
		
		// Test with "rado" is a substring of Emperador
		resultedTopic = Helper.identifyTopicOfName("Piaget Black Tie Emperador Moon-phase Cushion-Shaped, Blue Dial - Rose Gold on Strap", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Piaget"));
		
		// Test with "gant" is a substring of elegant
		resultedTopic = Helper.identifyTopicOfName("Hublot Yellow Gold Classic Elegant Diamond 1391.3.054", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Hublot"));
			
		// Test non-letter character in topic name
		resultedTopic = Helper.identifyTopicOfName("B.R.M V8-44-GULF Occasion", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("B.R.M"));
		
		resultedTopic = Helper.identifyTopicOfName("Parmigiani Fleurier Kalpa Skeleton Tourbillon PFH159-2002800-HA1441", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Parmigiani Fleurier"));
		
		resultedTopic = Helper.identifyTopicOfName("Wempe Glashütte / SA Weltzeituhr", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Wempe"));
		
		resultedTopic = Helper.identifyTopicOfName("A. Lange & Söhne 18k rose gold Richard Lange", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("A.Lange & Sohne"));
		
		resultedTopic = Helper.identifyTopicOfName("Fortis Spaceleader Design by Volkswagen Stahl/Kautschuk Chronograph Automatik 43mm Limitiert", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Fortis"));
		
		resultedTopic = Helper.identifyTopicOfName("IWC \"Porsche Design\" Chronograph Titan", Globals.HOROLOGYTOPICS);
		assertEquals(2, resultedTopic.size());
		assertTrue(resultedTopic.contains("IWC"));
		assertTrue(resultedTopic.contains("Porsche Design"));
		
		resultedTopic = Helper.identifyTopicOfName("Philip Stein Teslar Dual Time Stainless Steel Mid Size Diamonds Watch", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Philip Stein"));
		
		resultedTopic = Helper.identifyTopicOfName("Philip Watch Caribbean Professional 3000 Oversize Steel Dive Watch Ref. 4990 Top Condition + Box & Papers", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Philip Watch"));
		
		// Can be Gant and Schwarz Etienne 
		resultedTopic = Helper.identifyTopicOfName("Gant Windsor W10782 Herren Chrono silber schwarz Leder 42 mm", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Gant"));
		
		// Can be Jacques Etoile and Schwarz Etienne 
		resultedTopic = Helper.identifyTopicOfName("Jacques Etoile Atlantis schwarz", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Jacques Etoile"));
		
	}
	
	// Hard Test, don't need to pass all
	public void testTopicIdentifierHard() {
		Set<String> resultedTopic = null;
		
		// Hard Test
		// Can be Christiaan and Louis Vuitton
		resultedTopic = Helper.identifyTopicOfName("Christiaan v.d. Klaauw Elephant watch same Manchester United coach Louis van Gaal", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Christiaan van der Klaauw"));
		
		// Can be Jaeger LeCoultre and Martin Braun
		resultedTopic = Helper.identifyTopicOfName("Jaeger-LeCoultre Amvox1 Aston Martin Titanio LIMITED EDITION 12/", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Jaeger LeCoultre"));
		
		// Can be Chopard and Richard Mille
		resultedTopic = Helper.identifyTopicOfName("Chopard Mille Miglia GMT Chronograph, Silver Dial - Stainless Steel on Bracelet", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Chopard"));
		
		// Can be Philip Stein and Philip Watch
		resultedTopic = Helper.identifyTopicOfName("Philip Stein Watch Teslar Dual Time Stainless Steel Mid Size Diamonds Watch", Globals.HOROLOGYTOPICS);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Philip Stein"));
	}
}
