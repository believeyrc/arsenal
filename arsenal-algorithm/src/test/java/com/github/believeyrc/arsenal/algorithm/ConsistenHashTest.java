package com.github.believeyrc.arsenal.algorithm;


import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.github.believeyrc.arsenal.algorithm.distributed.ConsistentHash;

public class ConsistenHashTest {
	
	@Test
	public void consistentHashTest() {
		Set<String> nodes = new HashSet<String>();
		nodes.add("192.168.12.5");
		nodes.add("192.168.12.15");
		nodes.add("192.168.12.25");
		nodes.add("192.168.12.35");
		int virtualNum = 3;
		String key = "productId_12345678";
		ConsistentHash<String> consistentHash = new ConsistentHash<String>(nodes, virtualNum);
		consistentHash.printNodes();
		System.out.println(consistentHash.getHashNode(key));
		consistentHash.removeNode("192.168.12.15");
		consistentHash.printNodes();
		System.out.println(consistentHash.getHashNode(key));
		
	}

}
