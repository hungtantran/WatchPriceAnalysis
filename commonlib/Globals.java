package commonlib;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import newscrawler.Scheduler;
import dbconnection.MySqlConnection;

public class Globals {
	public static final boolean DEBUG = true;
	public static int DEFAULTLOWERBOUNDWAITTIMESEC = 5;
	public static int DEFAULTUPPERBOUNDWAITTIMESEC = 10;
	public static int NUMMAXTHREADS = 5;
	public static int NUMMAXQUEUE = 10;
	
	public static final String[] fileExtenstions = { "jpg", "xml", "gif",
			"pdf", "png", "jpeg" };

	// Map between the start url and domain
	public static Map<String, Domain> startUrlDomainMap;
	static {
		Map<String, Domain> tempMap = new HashMap<String, Domain>();
		tempMap.put("http://www.hodinkee.com", Domain.HODINKEE);
		tempMap.put("http://www.ablogtowatch.com/", Domain.ABLOGTOWATCH);
		tempMap.put("http://www.chrono24.com/", Domain.CHRONO24);
		tempMap.put("http://watchreport.com", Domain.WATCHREPORT);
		startUrlDomainMap = Collections.unmodifiableMap(tempMap);
	}

	public static Map<Integer, String> idTypeMap = null;
	public static Map<Integer, String> idDomainMap = null;
	public static Map<String, Integer> idTopicMap = null;
	
	public static LogManager crawlerLogManager = null;
	public static MySqlConnection con = null;
	public static Scheduler scheduler = null;

	// Type of links
	public static enum Type {
		HOROLOGY(1);

		public final int value;

		private Type(int value) {
			this.value = value;
		}
	};

	// Map between the type and its name as string
	public static Map<Type, String> typeNameMap;
	static {
		Map<Type, String> tempMap = new HashMap<Type, String>();
		tempMap.put(Type.HOROLOGY, "HOROLOGY");
		typeNameMap = Collections.unmodifiableMap(tempMap);
	}

	// Domain of links
	public static enum Domain {
		HODINKEE(1, "HODINKEE", "http://www.hodinkee.com"),
		ABLOGTOWATCH(2, "ABLOGTOWATCH", "http://www.ablogtowatch.com"),
		CHRONO24(3, "CHRONO24", "http://www.chrono24.com"),
		WATCHREPORT(4, "WATCHREPORT", "http://watchreport.com");

		public final int value;
		public final String string;
		public final String domain;
		
		private Domain(int value, String string, String domain) {
			this.value = value;
			this.string = string;
			this.domain = domain;
		}
	};

	// Map between the domain and its name as string
	public static Map<Domain, String> domainNameMap;
	static {
		Map<Domain, String> tempMap = new HashMap<Domain, String>();
		tempMap.put(Domain.HODINKEE, "HODINKEE");
		tempMap.put(Domain.ABLOGTOWATCH, "ABLOGTOWATCH");
		tempMap.put(Domain.CHRONO24, "CHRONO24");
		tempMap.put(Domain.WATCHREPORT, "WATCHREPORT");
		domainNameMap = Collections.unmodifiableMap(tempMap);
	}

	// Map between the type and keywords associated with the type
	public static Map<Type, String[]> typeTopicMap;
	public static final String[] HOROLOGYTOPICS = { "A.Lange & Sohne",
			"A.Manzoni & Fils", "Armin Strom", "Arnold & Son",
			"Audemars Piguet", "Ball", "Balmain", "Baume & Mercier",
			"Bell & Ross", "Blancpain", "Breguet", "Breitling", "Bremont",
			"Bulgari", "Bulova", "Bvlgari", "Calvin Klein", "Cartier", "Casio",
			"Certina", "Chanel", "Chopard", "Christophe Claret", "Citizen",
			"Concord", "Corum", "De Bethune", "DeWitt", "Flik Flak", "Fossil",
			"FP Journe", "Frank Muller", "Frederique Constant",
			"George Daniels", "Girard Perregaux", "Glashutte Original",
			"Greubel Forsey", "Gucci", "H Moser", "Hamilton", "Harry Winston",
			"HD3", "Hermes", "Hublot", "Invicta", "IWC", "Jacob & Co",
			"Jaeger LeCoulter", "Jaquet Droz", "Jean Richard", "Junghans",
			"Laurent Ferrier", "Laurice De Mauriac", "Léon Hatot",
			"Linde Werdelin", "Longines", "Louis Vuitton", "Maitres Du Temps",
			"Maurice Lacroix", "MB&F", "Michael Kors", "Mido", "Montblanc",
			"Movado", "Nomos", "Omega", "Orient", "Panerai",
			"Parmigiani Fleurier", "Patek Philippe", "Perellet",
			"Peter Speake Marin", "Philippe Dufour", "Piaget", "Pinion",
			"Rado", "Ralph Lauren", "Raymond Weil", "Ressence",
			"Richard Mille", "Roger Dubuis", "Roger W.Smith", "Rolex", "Seiko",
			"Shinola", "Sinn", "Tag Heuer", "TechnoMarine", "Thomas Prescher",
			"Timex", "Tissot", "Tudor", "Ulysse Nardin", "Union Glashutte",
			"Universal Geneve", "Urwerk", "Vacheron Constantin", "Victorinox",
			"Vulcain", "Xeric", "Zeitwinkel", "Zenith (LVMH)", "Bovet",
			"Buccellati", "Christopher Ward", "Ebel", "Fortis", "Graham",
			"Lemania", "Samsung", "Stowa", "Pebble", "Hautlence", "Wempe",
			"Minerva", "TB Buti", "Chronoswiss", "Eterna", "Van Der Bauwede",
			"Wyler", "Daniel Roth", "Jacques Lemans", "Jacob & Co", "Cvstos",
			"Tutima", "Pierre Deroche", "Revue Thommen", "U-Boat", "Waltham",
			"Versace", "Cuervo Y Sobrinos", "Eberhard & Co.", "Meistersinger",
			"Chaumet", "Bruno Sohnle", "Oris", "Alain Silberstein", "Michele",
			"Ingersoll", "Jaermann & Stubi", "Election", "Raidillon",
			"Armand Nicolet", "Formex", "Benzinger", "TW Steel", "Ikepod",
			"Ventura", "Catorex", "Haemmer", "Valbray", "Gruen", "Hugo Boss",
			"Davosa", "Lindburgh & Benson", "B.R.M", "Giuliano Mazzuoli",
			"Enicar", "Azimuth", "Blacksand", "Bunz", "Squale",
			"Romain Jerome", "Technos", "Michel Herbelin", "Di Lenardo & Co",
			"Xemex", "Lorus", "Orfina", "Skagen", "Aristo", "DeLaCour",
			"Damasko", "Kienzle", "Jacques Etoile", "Devon",
			"Van Cleef & Arpels", "Junkers", "Harwood", "Cyma", "Viceroy",
			"Auricoste", "Rothenschild", "Nixon", "Gant", "Milus", "Edox",
			"David Yurman", "Joop", "Brior", "Traser",
			"Christiaan van der Klaauw", "Lacoste", "Leonidas", "Wittnauer",
			"Favre-Leuba", "Fludo", "Aerowatch", "Bertolucci", "Poljot",
			"Auguste Reymond", "Aigner", "Angelus", "Atlantic", "Epos",
			"Erwin Sattler", "De Grisogono", "Cyclos", "Claude Meylan",
			"Gevril", "Fendi", "Mercure", "Martin Braun", "Marvin", "Longio",
			"Locman", "Kelek", "Porsche Design", "Perrelet", "Tiffany & Co",
			"Temption", "Sothis", "Schwarz Etienne", "Philip Stein", "DuBois",
			"Itay Noy", "Dior", "Quinting", "Ollech & Wajs", "Philip Watch",
			"Luminox", "Illinois" };

	// The list needs to be all in lower-case
	public static final HashSet<String> HOROLOGYTOPICSSTOPWORDS = new HashSet<String>(
			Arrays.asList("steel", "gold", "titanium", "platinum", "original",
					"stainless", "automatic", "manual", "diver", "pilot",
					"glashutte", "geneve", "design", "schwarz", "watch"));

	static {
		Map<Type, String[]> tempMap = new HashMap<Type, String[]>();
		tempMap.put(Type.HOROLOGY, HOROLOGYTOPICS);
		typeTopicMap = Collections.unmodifiableMap(tempMap);
	}
}
