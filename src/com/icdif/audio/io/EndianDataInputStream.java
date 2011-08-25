package com.icdif.audio.io;

import java.io.DataInputStream;
import java.io.InputStream;

/**
 * This class provides ways of reading the bytes of the PCM
 * Converting them to String, Short or Integer 
 * @author wanderer 
 */
public class EndianDataInputStream extends DataInputStream {
	public EndianDataInputStream(InputStream stream) {
		super(stream);
	}

	/**
	 * Reads an array of 4 bytes as a string - it's useful for reading the wave metadata
	 * @return the String converted from the 4 bytes
	 * @throws Exception
	 */
	public String readStringFrom4Byte() throws Exception {
		byte[] bytes = new byte[4];
		readFully(bytes);
		return new String(bytes, "US-ASCII");
	}

	/**
	 * Reads an array of 2 bytes and converts it to a short
	 * @return The short converted from the array of 2 bytes
	 * @throws Exception
	 */
	public short readShortLittleEndian() throws Exception {
		int result = readUnsignedByte();
		result |= readUnsignedByte() << 8;
		return (short) result;
	}

	/**
	 * Reads an array of 4 bytes and converts it to an integer
	 * @return The integer, converted from the array of 4 bytes
	 * @throws Exception
	 */
	public int readIntLittleEndian() throws Exception {
		int result = readUnsignedByte();
		result |= readUnsignedByte() << 8;
		result |= readUnsignedByte() << 16;
		result |= readUnsignedByte() << 24;
		return result;
	}

	//OS PROXS N SAO USADOS, MAS PODEM DAR JEITO no futuro
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int readInt24BitLittleEndian() throws Exception {
		int result = readUnsignedByte();
		result |= readUnsignedByte() << 8;
		result |= readUnsignedByte() << 16;
		if ((result & (1 << 23)) == 8388608)
			result |= 0xff000000;
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public int readInt24Bit() throws Exception {
		int result = readUnsignedByte() << 16;
		result |= readUnsignedByte() << 8;
		result |= readUnsignedByte();
		return result;
	}
}
