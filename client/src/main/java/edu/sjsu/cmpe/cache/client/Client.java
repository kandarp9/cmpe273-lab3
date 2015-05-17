
package edu.sjsu.cmpe.cache.client;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

/**
 * @author Kandarp
 *
 */
public class Client {
    
    private static final Funnel<CharSequence> strFunnel = Funnels
	    .stringFunnel(Charset.defaultCharset());
    private final static HashFunction hasher = Hashing.md5();
    public static ArrayList<String> nodes = new ArrayList<String>();
    
    public static void main(String[] args) throws Exception {
	System.out.println("Starting Cache Client...");
	
	SortedMap<Long, String> circle = new TreeMap<Long, String>();
	
	nodes.add("http://localhost:3000");
	nodes.add("http://localhost:3001");
	nodes.add("http://localhost:3002");
	
	for (int i = 0; i < nodes.size(); i++) {
	    //System.out.println( hasher.hashString(nodes.get(i), Charsets.UTF_8).padToLong() );
	    circle.put( hasher.hashString(nodes.get(i), Charsets.UTF_8).padToLong(), nodes.get(i));
	}
	
	List<Character> listChar = new ArrayList<Character>();
	
	listChar.add('a');
	listChar.add('b');
	listChar.add('c');
	listChar.add('d');
	listChar.add('e');
	listChar.add('f');
	listChar.add('g');
	listChar.add('h');
	listChar.add('i');
	listChar.add('j');
	
	for (int i = 0; i < listChar.size(); i++) {
	    
	    // For consistent hash
	    //String node = consistentHash( hasher.hashString(listChar.get(i).toString(), Charsets.UTF_8).padToLong(), circle);
	    
	    // For rendezvous hash
	    String node = rendezvousHash(listChar.get(i).toString());
	    
	    CacheServiceInterface cache = new DistributedCacheService(node);
	    
	    cache.put(i + 1, listChar.get(i).toString());
	    System.out.println("put(" + (i + 1) + " => " + listChar.get(i) + ")");
	}
	System.out.println("Starting Cache Client...");
	for (int i = 0; i < listChar.size(); i++) {
	    
	    // For consistent hash
	    //String node = consistentHash( hasher.hashString( listChar.get(i).toString(), Charsets.UTF_8 ).padToLong(), circle);
	    
	    // For rendezvous hash
	    String node = rendezvousHash(listChar.get(i).toString());
	    
	    CacheServiceInterface cache = new DistributedCacheService(node);
	    String value = cache.get(i + 1);
	    System.out.println("get(" + (i + 1) + ") => " + value);
	}
	System.out.println("Existing Cache Client...");
    }
    
    /**
     * Implementation of Consistent hashing
     * 
     * @param hashfunction
     * @param circle
     * @return
     */
    public static String consistentHash(Long hashfunction,
	    SortedMap<Long, String> circle) {
	
	if (!circle.containsKey(hashfunction)) {
	    
	    SortedMap<Long, String> tailMap = circle.tailMap(hashfunction);
	    hashfunction = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
	    
	}
	
	return circle.get(hashfunction);
    }
    
    /**
     * Implementation of Rendezvous or Highest Random Weight (HRW)
     * 
     * @param key
     * @return value
     */
    public static String rendezvousHash(String key) {
	long maxValue = Long.MIN_VALUE;
	String max = null;
	for (String node : nodes) {
	    long nodesHash = hasher.newHasher().putObject(key, strFunnel)
		    .putObject(node, strFunnel).hash().asLong();
	    if (nodesHash > maxValue) {
		max = node;
		maxValue = nodesHash;
	    }
	}
	return max;
    }
    
}