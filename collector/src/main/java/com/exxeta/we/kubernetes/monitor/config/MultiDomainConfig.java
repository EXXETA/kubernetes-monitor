package com.exxeta.we.kubernetes.monitor.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import com.google.gson.Gson;

public class MultiDomainConfig {
	private String[] domainConfigs;

	public static MultiDomainConfig getConfigFromResource(String resource) throws IOException {
		return getConfigFromReader(new InputStreamReader(MonitorConfig.class.getResourceAsStream(resource)));
	}

	public static MultiDomainConfig getConfigFromReader(Reader reader) throws IOException {
		return new Gson().fromJson(reader, MultiDomainConfig.class);

	}

	public static MultiDomainConfig getConfigFromString(String json) throws IOException {
		return getConfigFromReader(new StringReader(json));
	}

	public static MultiDomainConfig getConfig() throws IOException {
		return getConfigFromResource("/config.json");
	}

	public static MultiDomainConfig getConfigFromFile(File file) throws IOException {
		return getConfigFromReader(new FileReader(file));
	}


	public String[] getDomainConfigs() {
		return domainConfigs;
	}
}
