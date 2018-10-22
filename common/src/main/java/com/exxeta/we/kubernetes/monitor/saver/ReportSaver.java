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
package com.exxeta.we.kubernetes.monitor.saver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.exxeta.we.kubernetes.monitor.StatusReport;
import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.encryption.AsymmetricCryptography;
import com.exxeta.we.kubernetes.monitor.saver.file.FileSaver;
import com.exxeta.we.kubernetes.monitor.saver.webdav.WebdavService;
import com.google.gson.GsonBuilder;

public abstract class ReportSaver {
	protected MonitorConfig config;

	public ReportSaver(MonitorConfig config) {
		this.config = config;
	}

	public abstract void saveRawData(byte[] rawData);

	public StatusReport load() {

		byte[] json = loadJson();
		if (json == null)
			return null;
		return parse(new String(loadJson()));
	}

	public byte[] loadJson() {
		byte[] rawData = loadRawData();
		if (config.getEncrypt() != null && config.getEncrypt()) {
			try {
				return decrypt(rawData);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException
					| InvalidKeySpecException | IllegalBlockSizeException | BadPaddingException | IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return rawData;
		}
	}

	protected abstract byte[] loadRawData();

	protected String serialize(StatusReport report) {
		return new GsonBuilder().create().toJson(report);
	}

	protected StatusReport parse(String reportData) {
		return new GsonBuilder().create().fromJson(reportData, StatusReport.class);
	}

	protected byte[] encrypt(byte[] data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, InvalidKeySpecException, NoSuchProviderException, IOException {
		return new AsymmetricCryptography(config.getPrivateKey(), config.getPublicKey()).encrypt(data);

	}

	protected byte[] decrypt(byte[] data)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException,
			InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, IOException {
		return AsymmetricCryptography.decrypt(config, data);

	}

	public static ReportSaver getSaver(MonitorConfig config) {
		if (config.getReportUrl().startsWith("http")) {
			return new WebdavService(config);
		} else {
			return new FileSaver(config);
		}
	}

	public void save(StatusReport result) {

		try {
			byte[] myStatus = serialize(result).getBytes();

			if (config.getEncrypt() != null && config.getEncrypt()) {
				myStatus = encrypt(myStatus);
			}
			saveRawData(myStatus);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException | InvalidKeySpecException | NoSuchProviderException | IOException e) {
			e.printStackTrace();
		}

	}
}
