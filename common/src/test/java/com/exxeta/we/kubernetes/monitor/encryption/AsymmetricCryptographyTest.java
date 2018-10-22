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

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class AsymmetricCryptographyTest {

	@BeforeClass
	public static void createKeyPair(){
		RSAKeyGenerator.main(null);
	}
	
	@Test
	
	public void encDecTest() throws Exception{
		
		String text = "Very secret text";
		
		AsymmetricCryptography crypto = new AsymmetricCryptography("KeyPair/privateKey", "KeyPair/publicKey");
		
		byte[] cipher = crypto.encrypt(text.getBytes());
		String decoded = new String(crypto.decrypt(cipher));
		
		assertTrue(text.equals(decoded));
		
	}
	
}
