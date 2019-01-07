package com.exxeta.we.kubernetes.monitor.saver.webdav;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.DomainOverviewSaver;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class DomainOverviewWebdavService extends DomainOverviewSaver {

	public DomainOverviewWebdavService(MonitorConfig config) {
		super(config);
	}

	@Override
	public void saveRawData(byte[] rawData) {
		Sardine sardine = SardineFactory.begin(config.getWebDavUser(), config.getWebDavPassword());
		try {
			sardine.put(config.getOverviewUrl(), rawData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected byte[] loadRawData() {
		Sardine sardine = SardineFactory.begin(config.getWebDavUser(), config.getWebDavPassword());
		try {
			InputStream is = sardine.get(config.getOverviewUrl());
			return IOUtils.toByteArray(is);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
