package org.consistenthash.example;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * 创建日期:2015年6月26日
 * <br />一致性Hash 
 * @author leo
 * @mender：（文件的修改者，文件创建者之外的人）
 * @version 1.0
 * Remark：
 * <p>
 * 	<a href="http://blog.csdn.net/dreamrealised/article/details/12756671"> csdn consistent hash</a><br />
 * 	<a href="https://svn.apache.org/repos/asf/flume/branches/branch-0.9.5/flume-core/src/main/java/com/cloudera/util/consistenthash/ConsistentHash.java">apache ConsistentHash</a>
 * </p>
 */
public class HashGenerator {

	// hexDigits的用处是：将任意字节转换为十六进制的数字
	static final char hexDigits[] =  new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	/**
	 * 
	 * 功能:MD5 Hash
	 *<br /> 作者: leo
	 * <br />创建日期:2015年6月26日
	 * <br />修改者: mender
	 * <br />修改日期: modifydate
	 * @param key
	 * @return
	 */
	public String hash(String key){
		String result = null;
		MessageDigest md ;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
			byte [] temp = md.digest();
			result = toHex(temp);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * 
	 * 功能:将二进制的长整数转换为16进制的数字，以字符串表示
	 *<br /> 作者: leo
	 * <br />创建日期:2015年6月26日
	 * <br />修改者: mender
	 * <br />修改日期: modifydate
	 * @param temp
	 * @return
	 */
	private String toHex(byte[] bytes) {
		// MD5的结果：128位的长整数，用字节表示就是16个字节，用十六进制表示的话，使用两个字符，所以表示成十六进制需要32个字符
		char str[] = new char[16 * 2];
		int k = 0;
		for (int i = 0; i < 16; i++) {
			byte b = bytes[i];
			// 逻辑右移4位，与0xf（00001111）相与，为高四位的值，然后再hexDigits数组中找到对应的16进制值
			str[k++] = hexDigits[b >>> 4 & 0xf];
			// 与0xf（00001111）相与，为低四位的值，然后再hexDigits数组中找到对应的16进制值
			str[k++] = hexDigits[b & 0xf];

		}
		String s = new String(str);
		return s;
	}
	
	public static void main(String[] args) {
		System.out.println(new HashGenerator().hash("zhangsan"));
	}
}
