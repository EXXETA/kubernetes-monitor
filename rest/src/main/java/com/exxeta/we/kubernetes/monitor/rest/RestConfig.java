package com.exxeta.we.kubernetes.monitor.rest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.google.gson.Gson;

public class RestConfig {

	private List<MonitorConfig> environments = new ArrayList<>();

	public List<MonitorConfig> getEnvironments() {
		return environments;
	}

	public static RestConfig getConfigFromResource(String resource) throws IOException {
		return getConfigFromReader(new InputStreamReader(MonitorConfig.class.getResourceAsStream(resource)));
	}

	public static RestConfig getConfigFromReader(Reader reader) throws IOException {
		return new Gson().fromJson(reader, RestConfig.class);

	}

	public static RestConfig getConfigFromString(String json) throws IOException {
		return getConfigFromReader(new StringReader(json));
	}

	public static RestConfig getConfig() throws IOException {
		return getConfigFromResource("/config.json");
	}

	public static RestConfig getConfigFromFile(File file) throws IOException {
		return getConfigFromReader(new FileReader(file));
	}

}
