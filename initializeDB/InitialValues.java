package initializeDB;

import java.util.HashSet;
import java.util.Set;

import daoconnection.Domain;
import daoconnection.LinkQueue;
import daoconnection.Topic;
import daoconnection.Type;
import daoconnection.TypeWord;

public class InitialValues {
	public static Set<Type> initialTypesSet;
	static {
		initialTypesSet = new HashSet<Type>();
		
		initialTypesSet.add(new Type(1, "HOROLOGY"));
	}
	
	public static Set<LinkQueue> initialLinkQueuesSet;
	static {
		initialLinkQueuesSet = new HashSet<LinkQueue>();
		
		initialLinkQueuesSet.add(new LinkQueue(1, "http://www.hodinkee.com", 1, 0, 1, "00:00:00", "0000-00-00"));
		initialLinkQueuesSet.add(new LinkQueue(2, "http://www.ablogtowatch.com", 2, 0, 1, "00:00:00", "0000-00-00"));
		initialLinkQueuesSet.add(new LinkQueue(3, "http://www.chrono24.com", 3, 0, 1, "00:00:00", "0000-00-00"));
		initialLinkQueuesSet.add(new LinkQueue(4, "http://watchreport.com", 4, 0, 1, "00:00:00", "0000-00-00"));
	}
	
	public static Set<Domain> initialDomainsSet;
	static {
		initialDomainsSet = new HashSet<Domain>();
		
		initialDomainsSet.add(new Domain(1, "HODINKEE", "http://www.hodinkee.com"));
		initialDomainsSet.add(new Domain(2, "ABLOGTOWATCH", "http://www.ablogtowatch.com"));
		initialDomainsSet.add(new Domain(3, "CHRONO24", "http://www.chrono24.com"));
		initialDomainsSet.add(new Domain(4, "WATCHREPORT", "http://watchreport.com"));
	}
	
	public static Set<Topic> initialTopicsSet;
	static {
		initialTopicsSet = new HashSet<Topic>();
		
		initialTopicsSet.add(new Topic(1, 1, "A.Lange & Sohne"));
		initialTopicsSet.add(new Topic(2, 1, "A.Manzoni & Fils"));
		initialTopicsSet.add(new Topic(3, 1, "Armin Strom"));
		initialTopicsSet.add(new Topic(4, 1, "Arnold & Son"));
		initialTopicsSet.add(new Topic(5, 1, "Audemars Piguet"));
		initialTopicsSet.add(new Topic(6, 1, "Ball"));
		initialTopicsSet.add(new Topic(7, 1, "Balmain"));
		initialTopicsSet.add(new Topic(8, 1, "Baume & Mercier"));
		initialTopicsSet.add(new Topic(9, 1, "Bell & Ross"));
		initialTopicsSet.add(new Topic(10, 1, "Blancpain"));
		initialTopicsSet.add(new Topic(11, 1, "Breguet"));
		initialTopicsSet.add(new Topic(12, 1, "Breitling"));
		initialTopicsSet.add(new Topic(13, 1, "Bremont"));
		initialTopicsSet.add(new Topic(14, 1, "Bulgari"));
		initialTopicsSet.add(new Topic(15, 1, "Bulova"));
		initialTopicsSet.add(new Topic(16, 1, "Bvlgari"));
		initialTopicsSet.add(new Topic(17, 1, "Calvin Klein"));
		initialTopicsSet.add(new Topic(18, 1, "Cartier"));
		initialTopicsSet.add(new Topic(19, 1, "Casio"));
		initialTopicsSet.add(new Topic(20, 1, "Certina"));
		initialTopicsSet.add(new Topic(21, 1, "Chanel"));
		initialTopicsSet.add(new Topic(22, 1, "Chopard"));
		initialTopicsSet.add(new Topic(23, 1, "Christophe Claret"));
		initialTopicsSet.add(new Topic(24, 1, "Citizen"));
		initialTopicsSet.add(new Topic(25, 1, "Concord"));
		initialTopicsSet.add(new Topic(26, 1, "Corum"));
		initialTopicsSet.add(new Topic(27, 1, "De Bethune"));
		initialTopicsSet.add(new Topic(28, 1, "DeWitt"));
		initialTopicsSet.add(new Topic(29, 1, "Flik Flak"));
		initialTopicsSet.add(new Topic(30, 1, "Fossil"));
		initialTopicsSet.add(new Topic(31, 1, "FP Journe"));
		initialTopicsSet.add(new Topic(32, 1, "Frank Muller"));
		initialTopicsSet.add(new Topic(33, 1, "Frederique Constant"));
		initialTopicsSet.add(new Topic(34, 1, "George Daniels"));
		initialTopicsSet.add(new Topic(35, 1, "Girard Perregaux"));
		initialTopicsSet.add(new Topic(36, 1, "Glashutte Original"));
		initialTopicsSet.add(new Topic(37, 1, "Greubel Forsey"));
		initialTopicsSet.add(new Topic(38, 1, "Gucci"));
		initialTopicsSet.add(new Topic(39, 1, "H Moser"));
		initialTopicsSet.add(new Topic(40, 1, "Hamilton"));
		initialTopicsSet.add(new Topic(41, 1, "Harry Winston"));
		initialTopicsSet.add(new Topic(42, 1, "HD3"));
		initialTopicsSet.add(new Topic(43, 1, "Hermes"));
		initialTopicsSet.add(new Topic(44, 1, "Hublot"));
		initialTopicsSet.add(new Topic(45, 1, "Invicta"));
		initialTopicsSet.add(new Topic(46, 1, "IWC"));
		initialTopicsSet.add(new Topic(47, 1, "Jacob�&�Co"));
		initialTopicsSet.add(new Topic(48, 1, "Jaeger LeCoulter"));
		initialTopicsSet.add(new Topic(49, 1, "Jaquet Droz"));
		initialTopicsSet.add(new Topic(50, 1, "Jean Richard"));
		initialTopicsSet.add(new Topic(51, 1, "Junghans"));
		initialTopicsSet.add(new Topic(52, 1, "Laurent Ferrier"));
		initialTopicsSet.add(new Topic(53, 1, "Laurice De Mauriac"));
		initialTopicsSet.add(new Topic(54, 1, "L�on Hatot"));
		initialTopicsSet.add(new Topic(55, 1, "Linde Werdelin"));
		initialTopicsSet.add(new Topic(56, 1, "Longines"));
		initialTopicsSet.add(new Topic(57, 1, "Louis Vuitton"));
		initialTopicsSet.add(new Topic(58, 1, "Maitres Du Temps"));
		initialTopicsSet.add(new Topic(59, 1, "Maurice Lacroix"));
		initialTopicsSet.add(new Topic(60, 1, "MB&F"));
		initialTopicsSet.add(new Topic(61, 1, "Michael Kors"));
		initialTopicsSet.add(new Topic(62, 1, "Mido"));
		initialTopicsSet.add(new Topic(63, 1, "Montblanc"));
		initialTopicsSet.add(new Topic(64, 1, "Movado"));
		initialTopicsSet.add(new Topic(65, 1, "Nomos"));
		initialTopicsSet.add(new Topic(66, 1, "Omega"));
		initialTopicsSet.add(new Topic(67, 1, "Orient"));
		initialTopicsSet.add(new Topic(68, 1, "Panerai"));
		initialTopicsSet.add(new Topic(69, 1, "Parmigiani Fleurier"));
		initialTopicsSet.add(new Topic(70, 1, "Patek Philippe"));
		initialTopicsSet.add(new Topic(71, 1, "Perellet"));
		initialTopicsSet.add(new Topic(72, 1, "Peter Speake Marin"));
		initialTopicsSet.add(new Topic(73, 1, "Philippe Dufour"));
		initialTopicsSet.add(new Topic(74, 1, "Piaget"));
		initialTopicsSet.add(new Topic(75, 1, "Pinion"));
		initialTopicsSet.add(new Topic(76, 1, "Rado"));
		initialTopicsSet.add(new Topic(77, 1, "Ralph Lauren"));
		initialTopicsSet.add(new Topic(78, 1, "Raymond Weil"));
		initialTopicsSet.add(new Topic(79, 1, "Ressence"));
		initialTopicsSet.add(new Topic(80, 1, "Richard Mille"));
		initialTopicsSet.add(new Topic(81, 1, "Roger Dubuis"));
		initialTopicsSet.add(new Topic(82, 1, "Roger W.Smith"));
		initialTopicsSet.add(new Topic(83, 1, "Rolex"));
		initialTopicsSet.add(new Topic(84, 1, "Seiko"));
		initialTopicsSet.add(new Topic(85, 1, "Shinola"));
		initialTopicsSet.add(new Topic(86, 1, "Sinn"));
		initialTopicsSet.add(new Topic(87, 1, "Tag Heuer"));
		initialTopicsSet.add(new Topic(88, 1, "TechnoMarine"));
		initialTopicsSet.add(new Topic(89, 1, "Thomas Prescher"));
		initialTopicsSet.add(new Topic(90, 1, "Timex"));
		initialTopicsSet.add(new Topic(91, 1, "Tissot"));
		initialTopicsSet.add(new Topic(92, 1, "Tudor"));
		initialTopicsSet.add(new Topic(93, 1, "Ulysse Nardin"));
		initialTopicsSet.add(new Topic(94, 1, "Union Glashutte"));
		initialTopicsSet.add(new Topic(95, 1, "Universal Geneve"));
		initialTopicsSet.add(new Topic(96, 1, "Urwerk"));
		initialTopicsSet.add(new Topic(97, 1, "Vacheron Constantin"));
		initialTopicsSet.add(new Topic(98, 1, "Victorinox"));
		initialTopicsSet.add(new Topic(99, 1, "Vulcain"));
		initialTopicsSet.add(new Topic(100, 1, "Xeric"));
		initialTopicsSet.add(new Topic(101, 1, "Zeitwinkel"));
		initialTopicsSet.add(new Topic(102, 1, "Zenith (LVMH)"));
		initialTopicsSet.add(new Topic(103, 1, "Bovet"));
		initialTopicsSet.add(new Topic(104, 1, "Buccellati"));
		initialTopicsSet.add(new Topic(105, 1, "Christopher Ward"));
		initialTopicsSet.add(new Topic(106, 1, "Ebel"));
		initialTopicsSet.add(new Topic(107, 1, "Fortis"));
		initialTopicsSet.add(new Topic(108, 1, "Graham"));
		initialTopicsSet.add(new Topic(109, 1, "Lemania"));
		initialTopicsSet.add(new Topic(110, 1, "Samsung"));
		initialTopicsSet.add(new Topic(111, 1, "Stowa"));
		initialTopicsSet.add(new Topic(112, 1, "Pebble"));
		initialTopicsSet.add(new Topic(113, 1, "Hautlence"));
		initialTopicsSet.add(new Topic(114, 1, "Wempe"));
		initialTopicsSet.add(new Topic(115, 1, "Minerva"));
		initialTopicsSet.add(new Topic(116, 1, "TB Buti"));
		initialTopicsSet.add(new Topic(117, 1, "Chronoswiss"));
		initialTopicsSet.add(new Topic(118, 1, "Eterna"));
		initialTopicsSet.add(new Topic(119, 1, "Van Der Bauwede"));
		initialTopicsSet.add(new Topic(120, 1, "Wyler"));
		initialTopicsSet.add(new Topic(121, 1, "Daniel Roth"));
		initialTopicsSet.add(new Topic(122, 1, "Jacques Lemans"));
		initialTopicsSet.add(new Topic(123, 1, "Jacob & Co"));
		initialTopicsSet.add(new Topic(124, 1, "Cvstos"));
		initialTopicsSet.add(new Topic(125, 1, "Tutima"));
		initialTopicsSet.add(new Topic(126, 1, "Pierre Deroche"));
		initialTopicsSet.add(new Topic(127, 1, "Revue Thommen"));
		initialTopicsSet.add(new Topic(128, 1, "U-Boat"));
		initialTopicsSet.add(new Topic(129, 1, "Waltham"));
		initialTopicsSet.add(new Topic(130, 1, "Versace"));
		initialTopicsSet.add(new Topic(131, 1, "Cuervo Y Sobrinos"));
		initialTopicsSet.add(new Topic(132, 1, "Eberhard & Co."));
		initialTopicsSet.add(new Topic(133, 1, "Meistersinger"));
		initialTopicsSet.add(new Topic(134, 1, "Chaumet"));
		initialTopicsSet.add(new Topic(135, 1, "Bruno Sohnle"));
		initialTopicsSet.add(new Topic(136, 1, "Oris"));
		initialTopicsSet.add(new Topic(137, 1, "Alain Silberstein"));
		initialTopicsSet.add(new Topic(138, 1, "Michele"));
		initialTopicsSet.add(new Topic(139, 1, "Ingersoll"));
		initialTopicsSet.add(new Topic(140, 1, "Jaermann & Stubi"));
		initialTopicsSet.add(new Topic(141, 1, "Election"));
		initialTopicsSet.add(new Topic(142, 1, "Raidillon"));
		initialTopicsSet.add(new Topic(143, 1, "Armand Nicolet"));
		initialTopicsSet.add(new Topic(144, 1, "Formex"));
		initialTopicsSet.add(new Topic(145, 1, "Benzinger"));
		initialTopicsSet.add(new Topic(146, 1, "TW Steel"));
		initialTopicsSet.add(new Topic(147, 1, "Ikepod"));
		initialTopicsSet.add(new Topic(148, 1, "Ventura"));
		initialTopicsSet.add(new Topic(149, 1, "Catorex"));
		initialTopicsSet.add(new Topic(150, 1, "Haemmer"));
		initialTopicsSet.add(new Topic(151, 1, "Valbray"));
		initialTopicsSet.add(new Topic(152, 1, "Gruen"));
		initialTopicsSet.add(new Topic(153, 1, "Hugo Boss"));
		initialTopicsSet.add(new Topic(154, 1, "Davosa"));
		initialTopicsSet.add(new Topic(155, 1, "Lindburgh & Benson"));
		initialTopicsSet.add(new Topic(156, 1, "B.R.M"));
		initialTopicsSet.add(new Topic(157, 1, "Giuliano Mazzuoli"));
		initialTopicsSet.add(new Topic(158, 1, "Enicar"));
		initialTopicsSet.add(new Topic(159, 1, "Azimuth"));
		initialTopicsSet.add(new Topic(160, 1, "Blacksand"));
		initialTopicsSet.add(new Topic(161, 1, "Bunz"));
		initialTopicsSet.add(new Topic(162, 1, "Squale"));
		initialTopicsSet.add(new Topic(163, 1, "Romain Jerome"));
		initialTopicsSet.add(new Topic(164, 1, "Technos"));
		initialTopicsSet.add(new Topic(165, 1, "Michel Herbelin"));
		initialTopicsSet.add(new Topic(166, 1, "Di Lenardo & Co"));
		initialTopicsSet.add(new Topic(167, 1, "Xemex"));
		initialTopicsSet.add(new Topic(168, 1, "Lorus"));
		initialTopicsSet.add(new Topic(169, 1, "Orfina"));
		initialTopicsSet.add(new Topic(170, 1, "Skagen"));
		initialTopicsSet.add(new Topic(171, 1, "Aristo"));
		initialTopicsSet.add(new Topic(172, 1, "DeLaCour"));
		initialTopicsSet.add(new Topic(173, 1, "Damasko"));
		initialTopicsSet.add(new Topic(174, 1, "Kienzle"));
		initialTopicsSet.add(new Topic(175, 1, "Jacques Etoile"));
		initialTopicsSet.add(new Topic(176, 1, "Devon"));
		initialTopicsSet.add(new Topic(177, 1, "Van Cleef & Arpels"));
		initialTopicsSet.add(new Topic(178, 1, "Junkers"));
		initialTopicsSet.add(new Topic(179, 1, "Harwood"));
		initialTopicsSet.add(new Topic(180, 1, "Cyma"));
		initialTopicsSet.add(new Topic(181, 1, "Viceroy"));
		initialTopicsSet.add(new Topic(182, 1, "Auricoste"));
		initialTopicsSet.add(new Topic(183, 1, "Rothenschild"));
		initialTopicsSet.add(new Topic(184, 1, "Nixon"));
		initialTopicsSet.add(new Topic(185, 1, "Gant"));
		initialTopicsSet.add(new Topic(186, 1, "Milus"));
		initialTopicsSet.add(new Topic(187, 1, "Edox"));
		initialTopicsSet.add(new Topic(188, 1, "David Yurman"));
		initialTopicsSet.add(new Topic(189, 1, "Joop"));
		initialTopicsSet.add(new Topic(190, 1, "Brior"));
		initialTopicsSet.add(new Topic(191, 1, "Traser"));
		initialTopicsSet.add(new Topic(192, 1, "Christiaan van der Klaauw"));
		initialTopicsSet.add(new Topic(193, 1, "Lacoste"));
		initialTopicsSet.add(new Topic(194, 1, "Leonidas"));
		initialTopicsSet.add(new Topic(195, 1, "Wittnauer"));
		initialTopicsSet.add(new Topic(196, 1, "Favre-Leuba"));
		initialTopicsSet.add(new Topic(197, 1, "Fludo"));
		initialTopicsSet.add(new Topic(198, 1, "Aerowatch"));
		initialTopicsSet.add(new Topic(199, 1, "Bertolucci"));
		initialTopicsSet.add(new Topic(200, 1, "Poljot"));
		initialTopicsSet.add(new Topic(201, 1, "Auguste Reymond"));
		initialTopicsSet.add(new Topic(202, 1, "Aigner"));
		initialTopicsSet.add(new Topic(203, 1, "Angelus"));
		initialTopicsSet.add(new Topic(204, 1, "Atlantic"));
		initialTopicsSet.add(new Topic(205, 1, "Epos"));
		initialTopicsSet.add(new Topic(206, 1, "Erwin Sattler"));
		initialTopicsSet.add(new Topic(207, 1, "De Grisogono"));
		initialTopicsSet.add(new Topic(208, 1, "Cyclos"));
		initialTopicsSet.add(new Topic(209, 1, "Claude Meylan"));
		initialTopicsSet.add(new Topic(210, 1, "Gevril"));
		initialTopicsSet.add(new Topic(211, 1, "Fendi"));
		initialTopicsSet.add(new Topic(212, 1, "Mercure"));
		initialTopicsSet.add(new Topic(213, 1, "Martin Braun"));
		initialTopicsSet.add(new Topic(214, 1, "Marvin"));
		initialTopicsSet.add(new Topic(215, 1, "Longio"));
		initialTopicsSet.add(new Topic(216, 1, "Locman"));
		initialTopicsSet.add(new Topic(217, 1, "Kelek"));
		initialTopicsSet.add(new Topic(218, 1, "Porsche Design"));
		initialTopicsSet.add(new Topic(219, 1, "Perrelet"));
		initialTopicsSet.add(new Topic(220, 1, "Tiffany & Co"));
		initialTopicsSet.add(new Topic(221, 1, "Temption"));
		initialTopicsSet.add(new Topic(222, 1, "Sothis"));
		initialTopicsSet.add(new Topic(223, 1, "Schwarz Etienne"));
		initialTopicsSet.add(new Topic(224, 1, "Philip Stein"));
		initialTopicsSet.add(new Topic(225, 1, "DuBois"));
		initialTopicsSet.add(new Topic(226, 1, "Itay Noy"));
		initialTopicsSet.add(new Topic(227, 1, "Dior"));
		initialTopicsSet.add(new Topic(228, 1, "Quinting"));
		initialTopicsSet.add(new Topic(229, 1, "Ollech & Wajs"));
		initialTopicsSet.add(new Topic(230, 1, "Philip Watch"));
		initialTopicsSet.add(new Topic(231, 1, "Luminox"));
		initialTopicsSet.add(new Topic(232, 1, "Illinois"));
	}
	
	public static Set<TypeWord> initialTypeWordsSet;
	static {
		initialTypeWordsSet = new HashSet<TypeWord>();
		
		initialTypeWordsSet.add(new TypeWord(1, 1, "steel"));
		initialTypeWordsSet.add(new TypeWord(2, 1, "gold"));
		initialTypeWordsSet.add(new TypeWord(3, 1, "titanium"));
		initialTypeWordsSet.add(new TypeWord(4, 1, "platinum"));
		initialTypeWordsSet.add(new TypeWord(5, 1, "original"));
		initialTypeWordsSet.add(new TypeWord(6, 1, "stainless"));
		initialTypeWordsSet.add(new TypeWord(7, 1, "automatic"));
		initialTypeWordsSet.add(new TypeWord(8, 1, "manual"));
		initialTypeWordsSet.add(new TypeWord(9, 1, "diver"));
		initialTypeWordsSet.add(new TypeWord(10, 1, "pilot"));
		initialTypeWordsSet.add(new TypeWord(11, 1, "glashutte"));
		initialTypeWordsSet.add(new TypeWord(12, 1, "geneve"));
		initialTypeWordsSet.add(new TypeWord(13, 1, "design"));
		initialTypeWordsSet.add(new TypeWord(14, 1, "schwarz"));
		initialTypeWordsSet.add(new TypeWord(15, 1, "watch"));
	}
}
