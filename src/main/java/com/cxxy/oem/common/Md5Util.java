package com.cxxy.oem.common;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

public class Md5Util {
	//md5加密工具类
	public static String Md5(String crdentials,String credentialsSalt) {
		
        String hashAlgorithmName = "MD5";//加密方式  
        
        ByteSource salt = ByteSource.Util.bytes(credentialsSalt);//以账号作为盐值  
        
        int hashIterations = 1024;//加密1024次  
        
        SimpleHash hash = new SimpleHash(hashAlgorithmName,crdentials,salt,hashIterations);//进行md5盐值加密
        
        return hash.toString();
    }  
}
