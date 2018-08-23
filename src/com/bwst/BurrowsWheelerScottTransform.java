package com.bwst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Burrows-Wheeler-Scott transform
 *
 * The bijective variant of the Burrows-Wheeler transform.
 *
 * v0.1.1 no calculation LCM anymore 22-08-2018
 * v0.1.0 first working version 19-08-2018
 * v0.0.3 improved sorting 10-08-2018
 * v0.0.2 merged and ported methods from other code 05-08-2018
 * v0.0.1 initial version 25-07-2018
 * 
 * The pretty thing about this transform is that the input is of
 * the same length as the output.
 *
 * Functionality:
 * It basically tries to reduce entropy.
 * Works best if the input has some repetition, like text written in any language.
 * The text used in the example in the main function is Lorem Ipsum.
 *
 * With great help from https://github.com/zephyrtronium/bwst
 * for understanding BWST in plain English 
 * and http://www.allisons.org/ll/AlgDS/Strings/Factors/ for 
 * understanding Duval and giving a JavaScript example
 *
 * @author Wim Huizinga
 */
public class BurrowsWheelerScottTransform {

	private static final LcmByteArrayComparator LCM_BYTE_ARRAY_COMPARATOR = new LcmByteArrayComparator();
	
	/*
	 * Method to sort byte arrays (using least common multiple, not lexicographically)
	 * Java only (uses Collections API with a custom comparator)
	 */
	private static void lcmSortByteArrays(byte[][] byteArrays) {
		Collections.sort(Arrays.asList(byteArrays), LCM_BYTE_ARRAY_COMPARATOR);
	}
	
	/*
	 * Duval 1983, linear-time and O(1)-space. Converted from JavaScript
	 * 
	 * Ported from JavaScript:
	 * http://www.allisons.org/ll/AlgDS/Strings/Factors/
	 */
	private static List<Integer> duval(byte[] bytes) {
		int i, j, k = 0;
		ArrayList<Integer> ans = new ArrayList<Integer>();
		while (k < bytes.length) {
			i = k;
			j = k + 1;
			while (true) {
				if (j < bytes.length) {
					byte str_i = bytes[i], str_j = bytes[j];
					if (str_i < str_j) {
						i = k;
						j++;
						continue;
					} else if (str_i == str_j) {
						i++;
						j++;
						continue;
					}
				}
				do {
					k += (j - i);
					ans.add(k);
				} while (k <= i);
				break;
			}
		}
		return ans;
	}
	
	/*
	 * Duval 1983, Generates factors of breaks
	 * 
	 * modified to work in Java
	 */
	private static byte[][] factor(List<Integer> breaks, byte[] bytes) {
		byte[][] result = new byte[breaks.size()][];
		Iterator<Integer> iter = breaks.iterator();
		int i0 = 0, i1 = 0;
		while (iter.hasNext()) {
			int i2 = iter.next();
			int length = i2 - i0;
			byte[] arr = new byte[length];
			System.arraycopy(bytes, i0, arr, 0, length);
			result[i1] = arr;
			i0 = i2;
			i1++;
		}
		return result;
	}
		
	/*
	 * All rotations of single word
	 */
	private static byte[][] allRotations(byte[] bytes) {
		byte[][] result = new byte[bytes.length][];
		byte[] original = Arrays.copyOf(bytes, bytes.length);
		result[0] = original;
		byte[] next = Arrays.copyOf(original, original.length);
		for(int i = 1; i < bytes.length; i++) {	
			rotateRight(next);
			result[i] = Arrays.copyOf(next, next.length);
		}
		return result;
	}
	
	/*
	 * All rotations of multiple words
	 */
	private static byte[][] allRotations(byte[][] bytesArr) {
		int length = 0;
		for(int i = 0; i < bytesArr.length; i++)
			length += bytesArr[i].length;
		byte[][] result = new byte[length][];
		int index = 0;
		for(int i = 0; i < bytesArr.length; i++) {
			byte[][] rotations = allRotations(bytesArr[i]);
			for(int j = 0; j < rotations.length; j++)
				result[index++] = rotations[j]; 
		}
		return result;
	}
	
	/*
	 * Rotates single word to the right 
	 * 
	 * ABC -> CAB
	 */
	private static void rotateRight(byte[] bytes) {
		byte temp = bytes[bytes.length - 1];
		System.arraycopy(bytes, 0, bytes, 1, bytes.length - 1);
		bytes[0] = temp;
	}
	
	/*
	 * Returns last bytes in words:
	 * 
	 * ABC
	 * AC
	 * AB
	 * 
	 * returns [ C, C, B ]
	 */
	private static byte[] lastOf(byte[][] bytesArr) {
		byte[] result = new byte[bytesArr.length];
		for(int i = 0; i < bytesArr.length; i++)
			result[i] = bytesArr[i][bytesArr[i].length - 1];
		return result;
	}
	
	
	/**
	 * Encodes bytes to Burrows-Wheeler-Scott Transform
	 * 
	 * @param bytes Bytes to encode
	 * @return Encoded bytes
	 */
	public static byte[] encode(byte[] bytes) {
		// 
		byte[][] factors = factor(duval(bytes), bytes);
		byte[][] rotations = allRotations(factors);
		
		lcmSortByteArrays(rotations);
		return lastOf(rotations);
	}
	
	/**
	 * Decodes bytes from Burrows-Wheeler-Scott Transform
	 *
	 * @param bytes Bytes to encode
	 * @return Encoded bytes
	 */
	public static byte[] decode(byte[] bytes) {
		byte[] sort = Arrays.copyOf(bytes, bytes.length);
		Arrays.sort(sort);
		int[] map = new int[bytes.length];
		for(int i0 = 0; i0 < bytes.length; i0++) {
			byte b = bytes[i0];
			int s = 0;
			for(int i1 = 0; i1 < sort.length; i1++) {
				if(b == sort[i1]) {
					s = i1;
					break;
				}
			}
			int c = 0;
			for(int j = 0; j < i0; j++)
				if(bytes[j] == b)
					c++;
			int r = s + c;
			map[r] = i0;
		}
		int s = 0, index = 0;
		boolean[] visited = new boolean[map.length];
		byte[] result = new byte[bytes.length];
		int length = 0, next = bytes.length;
		for(int i = map.length - 1; i > -1; i--) {
			visited[index] = true;
			length++;			
			result[i] = sort[index];
			if(map[index] == s) {
				// word found
				next -= length;
				byte[] reversed = new byte[length];
				for(int k = 0; k < length; k++)
					reversed[length - 1 - k] = result[next + k];
				System.arraycopy(reversed, 0, result, next, length);
				int j = 0;
				while(j < map.length && visited[j]) // find next start
					j++;
				s = j;
				index = j;
				length = 0;
			} else
				index = map[index];
		}
		return result;
	}
	
	private static String str(byte[] bytes) {
		return new String(bytes);
	}
	
	public static void main(String[] args) {
	    String original = "BANANA";

//		String original = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut vitae ex auctor lectus gravida pretium. Vestibulum at condimentum neque. Mauris libero augue, mattis et pretium eget, fermentum vel ante. Phasellus consectetur diam dignissim dignissim ornare. Suspendisse potenti. Ut arcu lacus, aliquam id dolor a, viverra facilisis augue. Ut vitae tincidunt justo. Nullam dignissim metus in eros cursus imperdiet. Praesent aliquam dapibus elit et iaculis. Fusce enim magna, venenatis ut molestie et, gravida at nunc. Integer consectetur nisi et luctus porttitor. In eros odio, euismod sed neque eu, euismod bibendum ligula. In in dui in nisl ultrices vehicula nec id enim. Praesent sed dolor sit amet odio dictum bibendum." +
//						  "Sed dapibus dui eu velit scelerisque scelerisque. Pellentesque et elit risus. Morbi luctus metus sollicitudin, pretium libero vel, faucibus mauris. Proin arcu nisl, feugiat et gravida vitae, convallis at dolor. Fusce pharetra tempus pellentesque. Nam eget ultricies mi. Nullam tempor ultrices nibh, nec viverra sapien imperdiet ac. Nulla molestie pulvinar est, at tempor quam sagittis non. Mauris sollicitudin ullamcorper metus. Sed ac consectetur mi. Pellentesque imperdiet imperdiet dolor et pellentesque." +
//						  "Phasellus imperdiet massa eget felis vehicula, in sollicitudin justo rhoncus. Suspendisse mollis mauris eu tortor viverra, ut posuere lacus posuere. Donec venenatis aliquet lectus non volutpat. In consectetur orci a sem porta, sed iaculis eros consequat. Cras imperdiet et mauris eu sollicitudin. Donec vitae ante tellus. Aliquam laoreet enim libero, at tristique massa blandit eu. Donec scelerisque ultrices lacus ut auctor. Maecenas efficitur tincidunt tellus. Pellentesque non lobortis dolor. Aenean feugiat elementum metus in tincidunt. Etiam et tellus vel mi iaculis venenatis eu id mi. Donec semper dignissim orci ut scelerisque. Mauris pharetra nunc sit amet urna finibus, vitae efficitur purus pretium. Nam in pretium neque. Sed luctus hendrerit lectus, ac ultrices sapien condimentum vitae." +
//						  "Nam eu egestas leo, condimentum tristique tortor. Donec eget ex ac lacus condimentum feugiat et et purus. Aliquam a mauris molestie, fringilla massa ac, facilisis quam. Cras hendrerit risus nulla, vel ornare tellus condimentum at. Sed turpis sapien, rutrum sed ligula sit amet, venenatis interdum justo. Fusce sodales sit amet mauris sollicitudin malesuada. Aliquam luctus mauris vel pulvinar elementum. Pellentesque vitae laoreet metus. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed lacinia lacus sit amet risus feugiat varius. Pellentesque fringilla elit nec accumsan faucibus. Suspendisse sodales elit nec mi pellentesque, in sodales eros venenatis." +
//						  "Duis gravida ante non tincidunt placerat. Morbi congue tempor tortor, vel congue lectus volutpat hendrerit. Suspendisse sit amet arcu hendrerit, mattis purus et, tincidunt leo. Aliquam finibus sem sed tortor vulputate faucibus. Etiam id dui ac ante finibus malesuada. Aenean tempor risus augue, et ullamcorper turpis sollicitudin eu. Mauris dignissim posuere ipsum vel bibendum. Fusce ante ligula, iaculis at quam in, congue ultrices nisl. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam sit amet leo vel mi rutrum posuere. Donec ornare, eros non condimentum lobortis, tellus ex feugiat diam, eu lobortis felis erat et lectus.";

//		String original = "ABCBDBDBBABDBBABCDBCDDABCDBBBCDBBDBDBBABBCBD";

        byte[] encoded = encode(original.getBytes());

		System.out.println("original: " + original);
		System.out.println("encoded:  " + str(encoded));
		System.out.println();
		System.out.println("decoded:  " + str(decode(encoded)));
		System.out.println("original: " + original);
	}
}
