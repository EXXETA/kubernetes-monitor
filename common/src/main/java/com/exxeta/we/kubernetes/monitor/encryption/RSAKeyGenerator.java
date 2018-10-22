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
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RSAKeyGenerator {

		private KeyPairGenerator keyGen;
		private KeyPair pair;
		private PrivateKey privateKey;
		private PublicKey publicKey;

		public RSAKeyGenerator(int keylength) throws NoSuchAlgorithmException, NoSuchProviderException {
			this.keyGen = KeyPairGenerator.getInstance("RSA");
			this.keyGen.initialize(keylength);
		}

		public void createKeys() {
			this.pair = this.keyGen.generateKeyPair();
			this.privateKey = pair.getPrivate();
			this.publicKey = pair.getPublic();
		}

		public PrivateKey getPrivateKey() {
			return this.privateKey;
		}

		public PublicKey getPublicKey() {
			return this.publicKey;
		}

		public void writeToFile(String path, byte[] key) throws IOException {
			File f = new File(path);
			f.getParentFile().mkdirs();

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(key);
			fos.flush();
			fos.close();
		}

		public static void main(String[] args) {
			RSAKeyGenerator gk;
			try {
				gk = new RSAKeyGenerator(1024);
				gk.createKeys();
				gk.writeToFile("KeyPair/publicKey", gk.getPublicKey().getEncoded());
				gk.writeToFile("KeyPair/privateKey", gk.getPrivateKey().getEncoded());
			} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

		}

	}