/**
 * Copyright(c)2015 bw30 Co., Ltd.
 * All right reserved.
 * <br />创建日期:2015年6月26日
 */

package org.consistenthash.example;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 创建日期:2015年6月26日
 * <br />Description：对本文件的详细描述，原则上不能少于50字
 * @author 张凯
 * @mender：（文件的修改者，文件创建者之外的人）
 * @version 1.0
 * Remark：认为有必要的其他信息
 */

public class ConsistentHash<T> {

	/**
	 * consistent hash algorithm
	 */
	private final HashGenerator hashGenerator;
	//virtual nodes
	private final int numberOfReplicas;
	
	// 建立有序的map  
    private final SortedMap<String, T> circle = new TreeMap<String, T>();  
	public ConsistentHash(HashGenerator hashGenerator,int numberOfReplicas,Collection<T> nodes){
		this.hashGenerator = hashGenerator;
		this.numberOfReplicas = numberOfReplicas;
		for (T node : nodes) {
			add(node);
		}
	}
	
	/**
	 * 
	 * 功能:添加虚拟节点到闭环
	 *<br /> 作者: 张凯
	 * <br />创建日期:2015年6月26日
	 * <br />修改者: mender
	 * <br />修改日期: modifydate
	 * @param node
	 */
	public void add(T node){
		for(int i = 0;i < numberOfReplicas;i++){
			String key = node.toString() + i;
			String k = hashGenerator.hash(key);
			circle.put(k, node);
		}
	}
	
	/**
	 * 
	 * 功能:删除节点
	 *<br /> 作者: 张凯
	 * <br />创建日期:2015年6月26日
	 * <br />修改者: mender
	 * <br />修改日期: modifydate
	 * @param node
	 */
	public void remove (T node){
		for(int i = 0;i < numberOfReplicas;i++){
			String key = node.toString() + i;
			String k = hashGenerator.hash(key);
			circle.remove(k);
		}
	}
	
	/**
	 * 
	 * 功能:通过Key获取服务节点
	 *<br /> 作者: 张凯
	 * <br />创建日期:2015年6月26日
	 * <br />修改者: mender
	 * <br />修改日期: modifydate
	 * @param key
	 * @return
	 */
	public T get(Object key){
		if(circle.isEmpty()){
			return null;
		}
		// 得到对象的hash值，根据该hash值找hash值最接近的服务器 
		String hash = hashGenerator.hash((String)key);
		 // 以下为核心部分，寻找与上面hash最近的hash指向的服务器  
		// 如果hash表circle中没有该hash值  
		if(!circle.containsKey(hash)){
			// tailMap为大于该hash值的circle的部分 
			SortedMap<String, T> tailMap = circle.tailMap(hash);
			System.out.println(String.format("tailMap size %d", tailMap.size()));
			 // tailMap.isEmpty()表示没有大于该hash的hash值  
            // 如果没有大于该hash的hash值，那么从circle头开始找第一个；如果有大于该hash值得hash，那么就是第一个大于该hash值的hash为服务器  
            // 既逻辑上构成一个环，如果达到最后，则从头开始 
			hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();  
		}
		
		return circle.get(hash);
	}
	
	public static void main(String[] args) {
		Collection<String> nodes = new HashSet<String>(6);
		nodes.add("192.0.0.1");  
        nodes.add("192.0.0.2");  
        nodes.add("192.0.0.3");  
        nodes.add("192.0.0.4");  
        nodes.add("192.0.0.5");  
        nodes.add("192.0.0.6");
        HashGenerator hashGenerator = new HashGenerator();
        final ConsistentHash<String> cHash = new ConsistentHash<String>(hashGenerator, 4, nodes);
        new Thread(new Runnable() {
			
			public void run() {
				System.out.println("executor1");
				String [] key = {"google","163","baidu","sina","alibaba","qunar","meituan","tuniu","tujia","micro"};
		        for (String k : key) {
					System.out.println(String.format("对象 %s 存放与 %s 服务器", k,cHash.get(k)));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
        
        new Thread(new Runnable() {
			
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("executor2");
				cHash.add("192.0.0.7");
			}
		}).start();;
	}
	
}
