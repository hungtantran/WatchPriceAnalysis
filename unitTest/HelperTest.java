package unitTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import commonlib.Globals;
import commonlib.Helper;
import daoconnection.DAOFactory;
import daoconnection.Topic;
import daoconnection.TopicDAO;
import daoconnection.TopicDAOJDBC;
import daoconnection.TypeWord;
import daoconnection.TypeWordDAO;
import daoconnection.TypeWordDAOJDBC;

public class HelperTest {

	@Test
	public void test() {
		try {
			DAOFactory daoFactory = DAOFactory.getInstance(Globals.username, Globals.password, Globals.server + Globals.database);
			TypeWordDAO typeWordDAO = new TypeWordDAOJDBC(daoFactory);
			TopicDAO topicDAO = new TopicDAOJDBC(daoFactory);
			
			List<TypeWord> typeWordList = typeWordDAO.getTypeWords();
			List<Topic> topicList = topicDAO.getTopics();
			
			String[] topics = new String[topicList.size()];
			for (int i = 0; i < topics.length; ++i) {
				topics[i] = topicList.get(i).getTopic();
			}
			
			Set<String> typeWords = new HashSet<String>();
			for (int i = 0; i < typeWordList.size(); ++i) {
				typeWords.add(typeWordList.get(i).getTypeWord());
			}
		
			testTopicIdentifier(topics, typeWords);
			// testTopicIdentifierHard(topics, typeWords);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Test the Helper.identifyTopicOfName method
	public void testTopicIdentifier(String[] topics, Set<String> typeWords) {
		Set<String> resultedTopic = null;
		
		// Test null
		assertEquals(null, Helper.identifyTopicOfName(null, topics, typeWords));
		assertEquals(null, Helper.identifyTopicOfName("Rolex Daytona", null, typeWords));
		assertEquals(null, Helper.identifyTopicOfName(null, null, null));
		
		// Test simple with stop word like steel
		resultedTopic = Helper.identifyTopicOfName("Rolex Submariner Stainless Steel PVD/DLC Black Index Dial Black 60min Bezel Oyster Band", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		
		// Test with complicated name like A. Lange & Sohne or FP Journe
		resultedTopic = Helper.identifyTopicOfName("A. Lange & Söhne 1815 Chronograph Flyback Weißgold 402.026", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("A.Lange & Sohne"));
		
		resultedTopic = Helper.identifyTopicOfName("F.P.Journe Octa Sport Indy 500, Black Dial, Limited Edition to 99 Pieces - Black Aluminium on Strap", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("FP Journe"));
		
		// Test stop word like original
		resultedTopic = Helper.identifyTopicOfName("Rolex 1803 DATE-DAY 18K Gold with Original Silver Linen Sigma Dial", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		
		resultedTopic = Helper.identifyTopicOfName("Piaget Protocle Gent's Original \"Black Tie\" Watch 18K Yellow Gold", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Piaget"));
		
		// Test uppercase, lowercase difference
		resultedTopic = Helper.identifyTopicOfName("ROLEX Submariner Stainless Steel PVD/DLC Black Index Dial Black 60min Bezel Oyster Band", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		
		// Test with 2 brands with the same word Glashütte: Glashütte Original, Union Glashütte. Test accent removal
		resultedTopic = Helper.identifyTopicOfName("Glashütte Original PanoMatic Chrono XL 95-01-03-03-04 Pre-Owned", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Glashutte Original"));
		
		resultedTopic = Helper.identifyTopicOfName("Union Glashütte CHRONOGRAPH LIMITIERT MANUFAKTUR", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Union Glashutte"));
		
		// Test with 2 full brands
		resultedTopic = Helper.identifyTopicOfName("Tudor Certified Pre-Owned Stainless Steel & Yellow Gold Rolex- Princess Date", topics, typeWords);
		assertEquals(2, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		assertTrue(resultedTopic.contains("Tudor"));
		
		// Test with mix upper and lower case and half match branch (Zenith => Zenith (LVMH))
		resultedTopic = Helper.identifyTopicOfName("Rolex DAYTONA STEEL ZENiTH MOVEMENT -NEW- BLACK DiAL", topics, typeWords);
		assertEquals(2, resultedTopic.size());
		assertTrue(resultedTopic.contains("Rolex"));
		assertTrue(resultedTopic.contains("Zenith (LVMH)"));
		
		// Test with "rado" is a substring of Emperador
		resultedTopic = Helper.identifyTopicOfName("Piaget Black Tie Emperador Moon-phase Cushion-Shaped, Blue Dial - Rose Gold on Strap", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Piaget"));
		
		// Test with "gant" is a substring of elegant
		resultedTopic = Helper.identifyTopicOfName("Hublot Yellow Gold Classic Elegant Diamond 1391.3.054", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Hublot"));
			
		// Test non-letter character in topic name
		resultedTopic = Helper.identifyTopicOfName("B.R.M V8-44-GULF Occasion", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("B.R.M"));
		
		resultedTopic = Helper.identifyTopicOfName("Parmigiani Fleurier Kalpa Skeleton Tourbillon PFH159-2002800-HA1441", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Parmigiani Fleurier"));
		
		resultedTopic = Helper.identifyTopicOfName("Wempe Glashütte / SA Weltzeituhr", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Wempe"));
		
		resultedTopic = Helper.identifyTopicOfName("A. Lange & Söhne 18k rose gold Richard Lange", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("A.Lange & Sohne"));
		
		resultedTopic = Helper.identifyTopicOfName("Fortis Spaceleader Design by Volkswagen Stahl/Kautschuk Chronograph Automatik 43mm Limitiert", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Fortis"));
		
		resultedTopic = Helper.identifyTopicOfName("IWC \"Porsche Design\" Chronograph Titan", topics, typeWords);
		assertEquals(2, resultedTopic.size());
		assertTrue(resultedTopic.contains("IWC"));
		assertTrue(resultedTopic.contains("Porsche Design"));
		
		resultedTopic = Helper.identifyTopicOfName("Philip Stein Teslar Dual Time Stainless Steel Mid Size Diamonds Watch", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Philip Stein"));
		
		resultedTopic = Helper.identifyTopicOfName("Philip Watch Caribbean Professional 3000 Oversize Steel Dive Watch Ref. 4990 Top Condition + Box & Papers", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Philip Watch"));
		
		// Can be Gant and Schwarz Etienne 
		resultedTopic = Helper.identifyTopicOfName("Gant Windsor W10782 Herren Chrono silber schwarz Leder 42 mm", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Gant"));
		
		// Can be Jacques Etoile and Schwarz Etienne 
		resultedTopic = Helper.identifyTopicOfName("Jacques Etoile Atlantis schwarz", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Jacques Etoile"));
		
	}
	
	// Hard Test, don't need to pass all
	public void testTopicIdentifierHard(String[] topics, Set<String> typeWords) {
		Set<String> resultedTopic = null;
		
		// Hard Test
		// Can be Christiaan and Louis Vuitton
		resultedTopic = Helper.identifyTopicOfName("Christiaan v.d. Klaauw Elephant watch same Manchester United coach Louis van Gaal", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Christiaan van der Klaauw"));
		
		// Can be Jaeger LeCoultre and Martin Braun
		resultedTopic = Helper.identifyTopicOfName("Jaeger-LeCoultre Amvox1 Aston Martin Titanio LIMITED EDITION 12/", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Jaeger LeCoultre"));
		
		// Can be Chopard and Richard Mille
		resultedTopic = Helper.identifyTopicOfName("Chopard Mille Miglia GMT Chronograph, Silver Dial - Stainless Steel on Bracelet", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Chopard"));
		
		// Can be Philip Stein and Philip Watch
		resultedTopic = Helper.identifyTopicOfName("Philip Stein Watch Teslar Dual Time Stainless Steel Mid Size Diamonds Watch", topics, typeWords);
		assertEquals(1, resultedTopic.size());
		assertTrue(resultedTopic.contains("Philip Stein"));
	}
}
