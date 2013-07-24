/**
 *
 * The software in this package is published under the terms of the MPL v1.1 license.
 * 
 * You can get the newest version from http://github.com/neohope
 * 
 */

package neohope.charset;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * 
 * @author neohope <neohope@yahoo.com>
 * 
 */
public class UnknownBytesToString {

	/**
	 * Convert Unknown ByteBuffer(utf-8,gbk,gb2312,gb18030) to string.
	 * 
	 * Guess the charset, NOT ALWAYS WORKING!
	 * 
	 */
	public static String byteBufferToString(ByteBuffer buffer)
			throws CharacterCodingException {
		Charset charset = null;
		if (isUTF8(buffer)) {
			charset = Charset.forName("utf-8");
		} else {
			charset = Charset.forName("gb2312");
		}
		CharsetDecoder decoder = charset.newDecoder();
		CharBuffer charBuffer = decoder.decode(buffer);

		return charBuffer.toString();
	}

	/**
	 * ByteBuffer is utf-8 or not
	 * 
	 * As we know, utf-8 ascii charators and gb2312 ascii charators are the same
	 * in bytes.
	 * 
	 * However, one chinese charactor is three bytes in utf-8, but two bytes in
	 * gb2312, that means, they are totally different things.
	 * 
	 * To convert from one charset to another is simple, however, sometimes we
	 * have to guess the charset.
	 * 
	 * If there is only one charset in this world, our life will be easier. :)
	 * 
	 */
	public static boolean isUTF8(ByteBuffer buffer) {
		byte[] b = buffer.array();
		boolean beUTF8 = false;
		int nLen = b.length;

		// One chinese charactor is three bytes in utf-8,
		// so bytes less than 3 do not contain chinese charactor
		if (nLen >= 3) {
			byte U1, U2, U3;
			int nNow = 0;
			while (nNow < nLen) {
				U1 = b[nNow];
				if ((U1 & 0x80) == 0x80) {
					// One chinese charactor is three bytes in utf-8,
					// so bytes less than 3 do not contain chinese charactor
					if (nLen > nNow + 2) {
						U2 = b[nNow + 1];
						U3 = b[nNow + 2];
						// One chinese charactor is three bytes in utf-8,
						// Higher bits of these three bytes shoule be 0xE0 0xC0
						// 0xC0
						if (((U1 & 0xE0) == 0XE0) && ((U2 & 0xC0) == 0x80)
								&& ((U3 & 0xC0) == 0x80)) {
							// maybe UTF-8
							beUTF8 = true;
							nNow = nNow + 3;
						} else {
							// not UTF-8
							beUTF8 = false;
							break;
						}
					} else {
						// not UTF-8
						beUTF8 = false;
						break;
					}
				} else {
					// not chinese character
					nNow++;
				}
			}
		}
		return beUTF8;
	}

}
