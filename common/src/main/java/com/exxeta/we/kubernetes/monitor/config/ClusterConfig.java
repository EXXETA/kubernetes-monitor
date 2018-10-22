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

import java.util.List;

public class ClusterConfig {

	private String name;
	private String kubeconfig;
	private List<String> skipNamespaces;
	private List<ApplicationConfig> applications;
	private Boolean sendAlerts;
	
	public List<String> getSkipNamespaces() {
		return skipNamespaces;
	}
	public void setSkipNamespaces(List<String> skipNamespaces) {
		this.skipNamespaces = skipNamespaces;
	}
	public List<ApplicationConfig> getApplications() {
		return applications;
	}
	public void setApplications(List<ApplicationConfig> applications) {
		this.applications = applications;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKubeconfig() {
		return kubeconfig;
	}
	public void setKubeconfig(String kubeconfig) {
		this.kubeconfig = kubeconfig;
	}
	public Boolean getSendAlerts() {
		return sendAlerts;
	}
	public void setSendAlerts(Boolean sendAlerts) {
		this.sendAlerts = sendAlerts;
	}

	
	
}
