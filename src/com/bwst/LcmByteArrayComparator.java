package com.bwst;

import java.util.Comparator;

/**
 * v0.1 initial version 19-08-2018
 * 
 * @author Wim Huizinga 
 * 
 */
public class LcmByteArrayComparator implements Comparator<byte[]> {
	
	@Override
	public int compare(byte[] o1, byte[] o2) {
		int lcm = lcm(o1.length, o2.length);
		int x = 0, y = 0;
        for(int i = 0; i < lcm; i++) {
			byte a = o1[x], b = o2[y];
            int c = Byte.compare(a, b);
			if(c != 0) {
                return c;
            }
			x++;
			if(x >= o1.length)
				x = 0;
			y++;
			if(y >= o2.length)
				y = 0;
		}
		return 0;
	}

	private int gcd(int m, int n) {
		int tmp;
		while(m != 0) {
			tmp = m;
			m = n % m;
			n = tmp;
		}
		return n;
	}
	
	private int lcm(int m, int n) {
		return m / gcd(m, n) * n;
	}
}
