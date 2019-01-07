package com.exxeta.we.kubernetes.monitor.saver;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.exxeta.we.kubernetes.monitor.DomainOverview;
import com.exxeta.we.kubernetes.monitor.StatusReport;
import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.encryption.AsymmetricCryptography;
import com.exxeta.we.kubernetes.monitor.saver.file.DomainOverviewFileSaver;
import com.exxeta.we.kubernetes.monitor.saver.webdav.DomainOverviewWebdavService;
import com.google.gson.GsonBuilder;

public abstract class DomainOverviewSaver {

	protected MonitorConfig config;

	public DomainOverviewSaver(MonitorConfig config) {
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

	protected String serialize(DomainOverview report) {
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

	public static DomainOverviewSaver getSaver(MonitorConfig config) {
		if (config.getReportUrl().startsWith("http")) {
			return new DomainOverviewWebdavService(config);
		} else {
			return new DomainOverviewFileSaver(config);
		}
	}

	public void save(DomainOverview result) {
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
