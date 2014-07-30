package com.iresearch.android.utils;

import java.io.IOException;
import com.iresearch.android.utils.support.Base64;

import android.os.Build;

/**
 * Utility class for Base64 encoding. Froyo and newer versions of Android include 
 * Base64 support. If the environment is determined to be Froyo or later, then this 
 * class delegates to {@link android.util.Base64}. If the version is older than
 * Froyo, then Robert Harder's public domain Base64 class is used.
 * @see org.springframework.util.support.Base64
 * @author Roy Clarkson
 * @since 1.0
 */
public class Base64Utils {

	private static final Boolean froyoOrNewer = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO);

	/**
	 * Base64 encodes the input. If the version of Android is Froyo or newer, 
	 * then this method delegates to {@link android.util.Base64#encode(byte[], int)}.
	 * Otherwise {@link org.springframework.util.support.Base64#encodeBytesToBytes(byte[])}
	 * is used.
	 * @param input the byte array to encode
	 * @return encoded byte array
	 */
	public static byte[] encode(byte[] input) {
		if (froyoOrNewer) {
			return android.util.Base64.encode(input, android.util.Base64.DEFAULT | android.util.Base64.NO_WRAP);
		} else {
			return Base64.encodeBytesToBytes(input);
		}
	}

	/**
	 * Base64 encodes the input. If the version of Android is Froyo or newer, 
	 * then this method delegates to {@link android.util.Base64#encodeToString(byte[], int)}.
	 * Otherwise {@link org.springframework.util.support.Base64#encodeBytes(byte[])}
	 * is used.
	 * @param input the byte array to encode
	 * @return encoded String
	 */
	public static String encodeToString(byte[] input) {
		if (froyoOrNewer) {
			return android.util.Base64.encodeToString(input, android.util.Base64.DEFAULT | android.util.Base64.NO_WRAP);
		} else {
			return Base64.encodeBytes(input);
		}
	}

	/**
	 * Base64 decodes the input. If the version of Android is Froyo or newer, 
	 * then this method delegates to {@link android.util.Base64#decode(byte[], int)}.
	 * Otherwise {@link org.springframework.util.support.Base64#decode(byte[])}
	 * is used.
	 * @param input the byte array to decode
	 * @return decoded byte array
	 */
	public static byte[] decode(byte[] input) {
		if (froyoOrNewer) {
			return android.util.Base64.decode(input, android.util.Base64.DEFAULT | android.util.Base64.NO_WRAP);
		} else {
			try {
				return Base64.decode(input);
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Base64 decodes the input. If the version of Android is Froyo or newer, 
	 * then this method delegates to {@link android.util.Base64#decode(String, int)}.
	 * Otherwise {@link org.springframework.util.support.Base64#decode(String)}
	 * is used.
	 * @param str the String the decode
	 * @return decoded byte array
	 */
	public static byte[] decode(String str) {
		if (froyoOrNewer) {
			return android.util.Base64.decode(str, android.util.Base64.DEFAULT | android.util.Base64.NO_WRAP);
		} else {
			try {
				return Base64.decode(str);
			} catch (IOException e) {
				throw new IllegalArgumentException(e.getLocalizedMessage());
			}
		}
	}

}
