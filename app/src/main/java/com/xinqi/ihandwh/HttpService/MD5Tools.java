package com.xinqi.ihandwh.HttpService;

import java.security.MessageDigest;

/**
 * Created by syd on 2015/11/16.
 */
public class MD5Tools {
    /**
     * @param args
     */
    public static void main(String[] args) {
    }

    /**
     * MD5
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  'a', 'b', 'c', 'd', 'e', 'f', 'g'};
        try {
            byte[] strTemp = s.getBytes();

//		   System.out.println("得到的字节长度是"+strTemp.length);
//		   for(byte str:strTemp)
//		   {
//			   System.out.println(str);
//		   }

            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();

//		   System.out.println("得到的字节长度是"+md.length);
//		   for(byte str:md)
//		   {
//			   System.out.println(str);
//		   }

            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }
}
