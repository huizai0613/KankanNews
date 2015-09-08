package com.kankan.kankanews.utils;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RsaUtils {
	private String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC9v9jo/TL2WmTTUaPV8QR0Htq5mkJOZTOCT3Rlic48jGkuZkPleQ6zPp8JtP6hmRBdc2W7wUwATPUZ0u7jw8+QjtQv3f7X/3gTSdA6G+n/EjIF3EzvtvPTfTTyzFAopqnvrXAwsy8u3giK4riDX7sTqI77h6kA+1IjYlpDCDIirwIDAQAB";

	public PublicKey getpublicksy() throws Exception {
		// BASE64Decoder base64Decoder = new BASE64Decoder();
		byte[] buffer = Base64.decode(publicKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		return keyFactory.generatePublic(keySpec);
	}

	public byte[] encrypt(PublicKey publicKey, byte[] data) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
		// System.out.println(encryptedData.length);
		// return new String(encryptedData);
	}

	public byte[] encrypt(byte[] data) throws Exception {
		PublicKey publicKey = getpublicksy();
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
		// System.out.println(encryptedData.length);
		// return new String(encryptedData);
	}

	public static void main(String[] args) throws Exception {
		RsaUtils s = new RsaUtils();
		String str = new String(Base64.encode(s.encrypt(s.getpublicksy(),
				"{tel:2387289317}".getBytes())));
		System.out.println(str);
	}
}
