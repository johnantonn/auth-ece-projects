package online.jgibblda;

import java.util.HashMap;
import java.util.Map;

public class test {

	public static void main(String args[]){
		
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(1, 0.23);
		map.put(0, 0.001);
		map.put(2, 0.444);
		
		System.out.println(map.get(2));
	}
}
