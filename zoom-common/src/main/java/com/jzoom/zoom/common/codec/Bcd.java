package com.jzoom.zoom.common.codec;


public class Bcd {
	

	/**
	 * 这个用于补位 ,将长度不足2的倍数的字符串补成长度为2的倍数
	 * 
	 * 比如  str :   12345  转成bcd码会缺少一位，那么在前面补位或者后面补位
	 * 
	 * @author jzoom
	 *
	 */
	public static interface Padding{
		String pad(String asc);
	}
	
	public static final class PadAfter implements Padding{
		private char pad;
		
		public PadAfter(char pad) {
			this.pad = pad;
		}
		
		@Override
		public String pad(String src) {
			if (src.length() % 2 == 0) {
				return src;
			}
			return new StringBuilder(src.length()+1).append(src).append(pad).toString();
		}
		
	}
	
	
	public static final class PadBefore implements Padding{
		private char pad;
		
		public PadBefore(char pad) {
			this.pad = pad;
		}
		
		@Override
		public String pad(String src) {
			if (src.length() % 2 == 0) {
				return src;
			}
			return new StringBuilder(src.length()+1).append(pad).append(src).toString();
		}
		
	}

	public static final Padding BCD_PAD_F_AFTER = new PadAfter('F');
	
	public static final Padding BCD_PAD_0_BEFORE = new PadBefore('0');
	

	public static byte[] str2bcd(String asc) {
		return str2bcd(asc, BCD_PAD_F_AFTER);
	}
	
	public static byte[] str2bcd(String asc, Padding pad) {
		asc = pad.pad(asc);
		byte[] result = new byte[asc.length() % 2];
		return str2bcd(result, 0, asc);
	}


	/**
	 * 
	 * 将ascii字符串转成bcd码
	 * @param dest
	 * @param pos
	 * @param ascii				
	 * @return
	 */
	public static byte[] str2bcd(byte[] dest, int pos, String ascii) {
		assert(ascii.length() %2 ==0);
		int len = ascii.length() / 2;
		byte[] src = ascii.getBytes();
		int index = 0;
		for (int i = 0; i < len; ++i) {
			int destIndex = pos + i;
			int high = asc2bcd(src[index++]);
			dest[destIndex] = (byte) (asc2bcd(src[index++]) | (high << 4));
		}
		return dest;
	}

	private static byte asc2bcd(byte asc) {
		byte bcd;
		if ((asc >= '0') && (asc <= '9'))
			bcd = (byte) (asc - '0');
		else if ((asc >= 'A') && (asc <= 'F'))
			bcd = (byte) (asc - 'A' + 10);
		else if ((asc >= 'a') && (asc <= 'f'))
			bcd = (byte) (asc - 'a' + 10);
		else
			bcd = (byte) (asc - 48);
		return bcd;
	}

	

}
