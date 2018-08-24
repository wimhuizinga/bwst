package com.bwst;

import java.util.Comparator;

/**
 * v0.1.2 removed the if, added if statements to 0-reset to reduce if-checking 24-08-2018
 * v0.1.1 removed gcd and lcm code, added if that waits for iteration end 23-08-2018
 * v0.1.0 initial version 19-08-2018
 *
 * Compares byte arrays of virtually infinite length.
 * Stops comparing when least common multiple is found
 * (iteration of both arrays end at the same time)
 * to simulate the infinite length.
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

            if(x == o1.length) {
                if(y == o2.length) // lcm found x == o1.length and y == o2.length
                    break;
                x = 0;
            }
            if(y == o2.length) {
                if(x == o1.length) // lcm found x == o1.length and y == o2.length
                    break;
                y = 0;
            }
        }
        return 0; // a and b were equal
    }
}
