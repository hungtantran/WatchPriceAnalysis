package commonlib;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TopicComparator implements Comparator<String> {
	// Map between the domain and its name as string
	public static Map<String, Integer> topicPriorityMap;
	static {
		Map<String, Integer> tempMap = new HashMap<String, Integer>();
		
		// Article ranks the highest
		tempMap.put("Hodinkee", 0);
		tempMap.put("ABlogToWatch", 0);
		tempMap.put("WatchReport", 0);
		
		// The rest rank according to brand famous
		tempMap.put("Rolex", 1);
		tempMap.put("Omega", 2);
		tempMap.put("Patek Philippe", 3);
		tempMap.put("Audemars Piguet", 4);
		
		topicPriorityMap = Collections.unmodifiableMap(tempMap);
	}

	// Return the priority of a string if it contains the substring with that
	// priority from topicPriorityMap
	public static Integer getStringPriority(String str) {
		if (str == null) {
			return null;
		}
		
		String lowerCaseStr = new String(str.toLowerCase());
		Iterator<Entry<String, Integer>> it = topicPriorityMap.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = (Map.Entry<String, Integer>) it.next();
			
			String lowerCaseTopic = pairs.getKey().toLowerCase();
			if (lowerCaseStr.indexOf(lowerCaseTopic) != -1)
				return pairs.getValue();
			
			String noSpaceTopic = lowerCaseTopic.replace(" ", "");
			if (lowerCaseStr.indexOf(noSpaceTopic) != -1)
				return pairs.getValue();
		}

		return null;
	}

	@Override
	public int compare(String x, String y) {
		Integer xPriority = TopicComparator.getStringPriority(x);
		Integer yPriority = TopicComparator.getStringPriority(y);
		
		// If both strings don't have specified priority, return them as equal
		if (xPriority == null && yPriority == null) {
			return 0;
		}
		
		// If y has priority, x doesn't, x < y
		if (xPriority == null) {
			return 1;
		}
		
		// If x has priority, y doesn't, x > y
		if (yPriority == null) {
			return -1;
		}
		
		return xPriority - yPriority;
	}
}