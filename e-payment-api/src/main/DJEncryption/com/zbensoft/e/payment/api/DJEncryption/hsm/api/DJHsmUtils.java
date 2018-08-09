package com.zbensoft.e.payment.api.DJEncryption.hsm.api;

import java.util.regex.Pattern;

public class DJHsmUtils {	
	public static Pattern pattern = Pattern.compile("^[0-9]+$");	
	public static Pattern hexPattern = Pattern.compile("^[0-9a-fA-F]+$");
	
	/**
	 * 判断是否为16进制数或10进制数
	 * @param str
	 * @param digit
	 * @return
	 */
	public static boolean isNumeric(String str, int digit) {
		if (str != null) {
			if (digit == 16) {
				return hexPattern.matcher(str).matches();
			} else {
				return pattern.matcher(str).matches();
			}
		}
		
		return false;
	}
	
	/**
	 *  Z – 8字节DES密钥 
	 *	X/U – 16字节3DES密钥 
	 *	Y/T – 24字节3DES密钥 
	 *	P – 16字节SM1密钥 
	 *	R – 16字节SM4密钥 
	 *	L – 16字节AES密钥 
	 *	16 H /  
	 *  1A + 32H / 
	 *  1A + 48H 
	 * @param sLmkKeyFlag 密钥标识(LMK)
	 * @return
	 */
	public static int getSKeyLmkLength(byte sLmkKeyFlag) {
		int len = 0;
		
		if (sLmkKeyFlag == 0x00) {
			return len;
		}
		
		switch (sLmkKeyFlag){
			case 'Z': 
				len = 16;
				break;
			case 'X':
			case 'U':
			case 'P':
			case 'R':
			case 'L':
				len = 33;
				break;
			case 'K':
				len = 5;
				break;
			case 'Y':
			case 'T':
				len = 49;
				break;
			default:
				len = 16;
				break;
		}
		
		return len;
	}	
	
	public static byte[] getData(byte[] data, int dataLen) {
		byte[] newByte = null;
		
		if (dataLen>=0 && data!=null && data.length>dataLen) {
			newByte = new byte[dataLen];
			System.arraycopy(data, 0, newByte, 0, dataLen);
		} else {
			newByte = data;
		}
		
		return newByte;
	}	
	
	/**
	 * 截取data中16进制数 
	 * 可以直接返回H,N,A的字符
	 * @param data
	 * @return
	 */
	public static byte[] getHexByte(byte[] data) {
		int len = data.length;
		
		for (int i = 0; i < data.length; i++ ) {
			if (data[i] == 0x00) {
				len = i;
				break;
			}
		}
		
		byte[] newByte = getData(data, len);
		
		return newByte;
	} 
	
	/**
	 * 去掉最后一个字节
	 * @param bytes
	 * @return
	 */
	public static byte[] reduceTailBytes(byte[] bytes) {
		byte[] newByte = null;
		
		if (bytes != null && bytes.length > 0) {
			newByte = getData(bytes, bytes.length - 1);
		} else {
			newByte = bytes;
		}
		
		return newByte;
	}
	
	/**
	 * 获取RSA公钥有效字符
	 * @param sKeyCV
	 * @return
	 */
	public static byte[] getsPubRSAKey(byte[] bytes) {
		if (bytes==null || bytes.length<1) {
			return null;
		}
		
		int len = 0;
		int a = bytes[1]&0xff;
		
		if (a == 0x81) {
			len = (bytes[2]&0xff) + 3;
		} else if (a == 0x82) {
			len = (bytes[2]&0xff) * 256 + (bytes[3]&0xff) + 4; 
		} else if (a == 0x00) {
			len = 0;
		} else {
			len = 513;
		}
		
		return getData(bytes, len);
	}
	
	/**
	 * 获取SM2有效字符
	 */
	public static byte[] getsPubSM2Key(byte[] bytes) {
		if (bytes==null || bytes.length<1) {
			return null;
		}
		if ((bytes[0]&0xff) == 0x00 && (bytes[1]&0xff) == 0x00 && (bytes[2]&0xff) == 0x00) {
			return null;
		}
		
		int len = (bytes[1]&0xff) + 2;
		
		return DJHsmUtils.getData(bytes, len);
	}

	/**
	 * 判断一个字节数组是否所有数据都为空
	 * @param data
	 * @return
	 */
	public static boolean isEmpty(byte[] data) {
		if (data == null || data.length < 1) {
			return true;
		}
		
		for (byte b : data) {
			if ((b&0xff) != 0x00) {
				return false;
			}
		}
		
		return true;
	}
}
