package jniosemu;

import java.util.Vector;

public class Utilities
{
	public static int vectorToInt(Vector<Boolean> vector) {
		int ret = 0;
		int i = 0;
		for (Boolean value: vector) {
			if (value)
				ret |= (int)Math.pow(2, i);
			i++;
		}
		return ret;
	}

	public static byte vectorToByte(Vector<Boolean> vector) {
		byte ret = 0;
		int i = 0;
		for (Boolean value: vector) {
			if (value)
				ret |= (byte)Math.pow(2, i);
			i++;
		}
		return ret;
	}

	public static byte[] shortToByteArray(short value) {
		byte[] ret = new byte[2];

		for (int i = 0; i < 2; i++)
			ret[i] = (byte)(value >>> (8 * i) & 0xFF);

		return ret;
	}

	public static byte[] intToByteArray(int value) {
		byte[] ret = new byte[4];

		for (int i = 0; i < 4; i++)
			ret[i] = (byte)(value >>> (8 * i) & 0xFF);

		return ret;
	}

	public static byte[] longToByteArray(long value) {
		byte[] ret = new byte[8];

		for (int i = 0; i < 8; i++)
			ret[i] = (byte)(value >>> (8 * i) & 0xFF);

		return ret;
	}

	public static int byteArrayToInt(byte[] value) {
		int ret = 0;

		for (int i = 0; i < 4; i++)
			ret |= ((long)value[i] << (8 * i));

		return ret;
	}

	public static long byteArrayToLong(byte[] value) {
		long ret = 0;

		for (int i = 0; i < 8; i++)
			ret |= ((long)value[i] << (8 * i));

		return ret;
	}

	public static Vector<Boolean> intToVector(int value, int size) {
		Vector<Boolean> ret = new Vector<Boolean>(size);

		for (int i = 0; i < size; i++)
			ret.add(i, (value & (int)Math.pow(2, i)) > 0);

		return ret;
	}

	public static String byteArrayToBitString(byte[] value) {
		String ret = "";

		for (int i = value.length - 1; i >= 0; i--) {
			for (int j = 7; j >= 0; j--) {
				if ((value[i] & (int)Math.pow(2, j)) > 0)
					ret.concat("1");
				else
					ret.concat("0");
			}

			if (i > 0)
				ret.concat(" ");
		}

		return ret;
	}

	public static int unsignedbyteToInt(byte value) {
		int ret = 0;
		return (int)(ret | value);
	}

	/**
	 * Converts integer to a string of the hexadecimal value.
	 *
	 * @param   value  decimal value to convert
	 * @return         hexadecimal value as string
	 */
	public static String intToHexString(int value) {
		String hex = "00000000" + Integer.toHexString(value);
		return "0x" + hex.substring(hex.length()-8, hex.length());
	}
		
}
