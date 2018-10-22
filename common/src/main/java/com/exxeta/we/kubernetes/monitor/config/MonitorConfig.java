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
package com.exxeta.we.kubernetes.monitor.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.google.gson.Gson;

public class MonitorConfig {

	private String privateKey;
	private String publicKey;
	private String webDavUser;
	private String webDavPassword;
	private String reportUrl;
	private String alertEmailAddress;
	private List<ClusterConfig> clusters;
	private Boolean encrypt;
	private String smptServer;
	

	public List<ClusterConfig> getClusters() {
		return clusters;
	}

	public void setClusters(List<ClusterConfig> clusters) {
		this.clusters = clusters;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getWebDavUser() {
		return webDavUser;
	}

	public void setWebDavUser(String webDavUser) {
		this.webDavUser = webDavUser;
	}

	public String getWebDavPassword() {
		return webDavPassword;
	}

	public void setWebDavPassword(String webDavPassword) {
		this.webDavPassword = webDavPassword;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	public static MonitorConfig getConfigFromResource(String resource) throws IOException {
		return getConfigFromReader(new InputStreamReader(MonitorConfig.class.getResourceAsStream(resource)));
	}

	public static MonitorConfig getConfigFromReader(Reader reader) throws IOException {
		return new Gson().fromJson(reader, MonitorConfig.class);

	}

	public static MonitorConfig getConfigFromString(String json) throws IOException {
		return getConfigFromReader(new StringReader(json));
	}

	public static MonitorConfig getConfig() throws IOException {
		return getConfigFromResource("/config.json");
	}

	public static MonitorConfig getConfigFromFile(File file) throws IOException {
		return getConfigFromReader(new FileReader(file));
	}

	public String getAlertEmailAddress() {
		return alertEmailAddress;
	}

	public void setAlertEmailAddress(String alertEmailAddress) {
		this.alertEmailAddress = alertEmailAddress;
	}
	
	
	public Boolean getEncrypt() {
		return encrypt;
	}
	
	public void setEncrypt(Boolean encrypt) {
		this.encrypt = encrypt;
	}

	public String getSmptServer() {
		return smptServer;
	}

	public void setSmptServer(String smptServer) {
		this.smptServer = smptServer;
	}
	
	
}
