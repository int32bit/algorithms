package mefoo.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.BitSet;

public class Simhash {
	private HashProvider hashProvider;
	private Spliter spliter;
	private static final int LONG_WIDTH = 64;
	public Simhash(HashProvider hashProvider, Spliter spliter) {
		this.hashProvider = hashProvider;
		this.spliter = spliter;
	}
	public Simhash() {
		this(new defaultHashProvider(), new defaultSpliter());
	}
	public long of(String document) {
		return getHashFromString(document);
	}
	public long of(InputStream in) {
		//FIXME 
		return 0L;
	}
	private long getHashFromString(String doc) {
		String[] features = spliter.split(doc);
		return getHashFromFeatures(features);
	}
	private long getHashFromFeatures(String[] features) {
		long[] vector = new long[LONG_WIDTH];
		Arrays.fill(vector, 0);
		for (String feature : features) {
			long hash = hashProvider.hash(feature);
			int mask = 0x1;
			int count = 0;
			while (count < 64) {
				if ((mask & hash) != 0)
					vector[count] += 1;
				else
					vector[count] -= 1;
				mask <<= 1;
				count++;
			}
		}
		BitSet set = new BitSet(LONG_WIDTH);
		int count = 0;
		while (count < 64) {
			if (vector[count] > 0) {
				set.set(count);
			}
			count++;
		}
		return set.toLongArray()[0];
	}
	public static int distance(long f1, long f2) {
		long n = f1 ^ f2;
		int sum = 0;
		while (n != 0) {
			sum++;
			n &= (n - 1);
		}
		return sum;
	}
	public static class defaultHashProvider implements HashProvider {

		@Override
		public long hash(String obj) {
			long hash = 0L;
			byte[] values = null;
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				values = md.digest(obj.getBytes());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			assert(values.length >= 8);
			for (int i = 0; i < 8; ++i) {
				hash |= values[i];
				hash <<= 8;
			}
			return hash;
		}
	}
	public static class defaultSpliter implements Spliter {
		@Override
		public String[] split(String document) {
			return document.split("\\W");
		}
	}
	public static void main(String[] args) {
		
	}
}
