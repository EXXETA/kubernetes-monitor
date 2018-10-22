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
package com.exxeta.we.kubernetes.monitor;

import java.util.Date;

public class ClusterAvailablility {

	private String clusterName;
	private Long begin;
	private Long lastUpdate;
	private Long availableMillis;
	private int numberOfNodes;
	private boolean available;

	public ClusterAvailablility() {
		this.lastUpdate = new Date().getTime();
		this.begin = new Date().getTime();
	}

	public ClusterAvailablility(ClusterAvailablility availability) {
		this();
		if (availability != null) {
			this.clusterName = availability.clusterName;
			this.begin = availability.begin;
			this.availableMillis = availability.availableMillis;
			this.numberOfNodes = availability.numberOfNodes;
			this.available = availability.available;
		}
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public Long getBegin() {
		return begin;
	}

	public void setBegin(Long begin) {
		this.begin = begin;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getAvailableMillis() {
		return availableMillis;
	}

	public void setAvailableMillis(Long availableSeconds) {
		this.availableMillis = availableSeconds;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public boolean isAvailable() {
		return available;
	}

}
