package com.bwst;

import java.util.Comparator;

/**
 * v0.1 initial version 19-08-2018
 *
 * Compares byte arrays of virtually infinite length.
 * Stops comparing when least common multiple is found.
 *
 * @author Wim Huizinga 
 * 
 */
public class LcmByteArrayComparator implements Comparator<byte[]> {

	private int x, y, c;
	private byte a, b;

	@Override
	public int compare(byte[] o1, byte[] o2) {
		x = 0;
		y = 0;
        while(true) { // lcm will break this loop
			a = o1[x];
			b = o2[y];
            c = Byte.compare(a, b);
			if(c != 0) {
                return c;
            }
			x++;
            y++;

            if(x == o1.length && y == o2.length) // lcm found
                break;

			if(x == o1.length)
				x = 0;
			if(y == o2.length)
				y = 0;
		}
		return 0; // they were equal
	}
}
