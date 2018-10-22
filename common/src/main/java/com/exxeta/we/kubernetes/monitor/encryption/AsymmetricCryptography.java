/*
 * Kubernetes Monitor
 * Copyright (C) 2018 Thomas Pohl and EXXETA AG
 * http://www.exxeta.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exxeta.we.kubernetes.monitor.encryption;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;

public class AsymmetricCryptography {

	private Cipher cipher;
	private String privateKeyPath;
	private String publicKeyPath;

	public AsymmetricCryptography(String privateKeyPath, String publicKeyPath)
			throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {

		this.cipher = Cipher.getInstance("RSA");
		this.privateKeyPath = privateKeyPath;
		this.publicKeyPath = publicKeyPath;
	}

	private PrivateKey getPrivate() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		return getPrivate(privateKeyPath);
	}

	private PublicKey getPublic() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException  {
		return getPublic(publicKeyPath);
	}

	// https://docs.oracle.com/javase/8/docs/api/java/security/spec/PKCS8EncodedKeySpec.html
	private PrivateKey getPrivate(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}

	// https://docs.oracle.com/javase/8/docs/api/java/security/spec/X509EncodedKeySpec.html
	private PublicKey getPublic(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	public byte[] encrypt(byte[] input) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, IOException{
		KeyGenerator generator = KeyGenerator.getInstance("AES");
		generator.init(128); // The AES key size in number of bits
		SecretKey secKey = generator.generateKey();
		//System.out.println("AESKey b64:"+new String(Base64.getEncoder().encode(secKey.getEncoded())));

		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
		byte[] byteCipherText = aesCipher.doFinal(input);
		byte[] encryptedCipherText = Base64.getEncoder().encode(byteCipherText);
		
		this.cipher.init(Cipher.ENCRYPT_MODE, getPublic());
		byte[] encryptedKey = Base64.getEncoder().encode(this.cipher.doFinal(secKey.getEncoded()));
		byte[] result = new byte[encryptedCipherText.length+encryptedKey.length+1];
		System.arraycopy(encryptedKey, 0, result, 0, encryptedKey.length);
		result[encryptedKey.length]=10;
		System.arraycopy(encryptedCipherText, 0, result, encryptedKey.length+1, encryptedCipherText.length);
		return result;
	}

	public byte[] decrypt(byte[] input) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException,
			IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {
		int linebreak = indexOf(input,10);
		byte[] encryptedKey = new byte[linebreak];
		byte[] encryptedText = new byte[input.length-linebreak-1];
		System.arraycopy(input, 0, encryptedKey, 0, linebreak);
		System.arraycopy(input, linebreak+1, encryptedText, 0, encryptedText.length);
		this.cipher.init(Cipher.DECRYPT_MODE, getPrivate());
		byte[] decryptedKey =  this.cipher.doFinal(Base64.getDecoder().decode(encryptedKey));
		
		SecretKey originalKey = new SecretKeySpec(decryptedKey , 0, decryptedKey .length, "AES");
		Cipher aesCipher = Cipher.getInstance("AES");
		aesCipher.init(Cipher.DECRYPT_MODE, originalKey);

		return aesCipher.doFinal(Base64.getDecoder().decode(encryptedText));

	}

	private int indexOf(byte[] input, int needle) {
		for (int i=0;i<input.length;i++){
			if (input[i]==needle) return i;
		}
		return -1;
	}
	
	public static byte[] decrypt(MonitorConfig config, byte[] encryptedData)
			throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException, InvalidKeyException,
			InvalidKeySpecException, IOException, IllegalBlockSizeException, BadPaddingException {
		AsymmetricCryptography crypto = new AsymmetricCryptography(config.getPrivateKey(), config.getPublicKey());

		return crypto.decrypt(encryptedData);
	}
}