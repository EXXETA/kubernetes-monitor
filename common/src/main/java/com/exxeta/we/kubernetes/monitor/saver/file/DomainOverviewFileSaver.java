package com.exxeta.we.kubernetes.monitor.saver.file;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.DomainOverviewSaver;

public class DomainOverviewFileSaver extends DomainOverviewSaver {
	
	public DomainOverviewFileSaver(MonitorConfig config) {
		super(config);
	}

	@Override
	public void saveRawData(byte[] rawData) {
		try {
			FileUtils.writeByteArrayToFile(FileUtils.toFile(new URL(config.getOverviewUrl())), rawData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected byte[] loadRawData() {
		try {
			return FileUtils.readFileToByteArray(new File(config.getOverviewUrl()));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
