package com.github.believeyrc.arsenal.algorithm.distributed;


import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.github.believeyrc.arsenal.algorithm.encryption.MD5;

/**
 * 
 * @author yangrucheng
 * @created 2015年4月7日 下午2:27:51
 * @since 1.0
 * @version 1.0
 *
 * @param <T>
 */
public class ConsistentHash<T> {
	
	private final SortedMap<String, T> nodesMap = new TreeMap<String, T>();
	
	private int virtualNum = 0;
	
	public ConsistentHash(Set<T> nodes, int virtualNum) {
		this.virtualNum = virtualNum;
		for (T node : nodes) {
			addNode(node);
		}
		
	}
	
	public void addNode(T node) {
		if (node == null) {
			return;
		}
		for (int i = 0; i < virtualNum; i++) {
			nodesMap.put(MD5.getMD5(node.toString() + "#" + i), node);
		}
	}
	
	public void removeNode(T node) {
		if (node == null) {
			return;
		}
		for (int i = 0; i < virtualNum; i++) {
			nodesMap.remove(MD5.getMD5(node.toString() + "#" + i));
		}
	}
	
	public T getHashNode(String key) {
		if (nodesMap.isEmpty()) {
			return null;
		}
		String hash = MD5.getMD5(key);
		SortedMap<String, T> tailMap = nodesMap.tailMap(hash);
		if (tailMap.isEmpty()) {
			hash = nodesMap.firstKey();
		} else {
			hash = tailMap.firstKey();
		}
		return nodesMap.get(hash);
	}
	
	public void printNodes() {
		for (Map.Entry<String, T> node : nodesMap.entrySet()) {
			System.out.println(node.getKey() + " : " + node.getValue());
		}
	}
}
