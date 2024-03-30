// IN2011 Computer Networks
// Coursework 2023/2024
//
// Construct the hashID for a string

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashID {

	/**
	 * Computes the SHA-256 hash of a string that ends with a newline character.
	 *
	 * @param line The input string.
	 * @return The computed hash as a byte array.
	 * @throws NoSuchAlgorithmException If the SHA-256 algorithm is not available.
	 */
	public static byte[] computeHashID(String line) throws NoSuchAlgorithmException {
		if (!line.endsWith("\n")) {
			line += "\n"; // Ensuring the line ends with a newline character
		}

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		return digest.digest(line.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Converts a byte array into a hex string.
	 *
	 * @param hash The byte array to convert.
	 * @return The corresponding hex string.
	 */
	public static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}
}