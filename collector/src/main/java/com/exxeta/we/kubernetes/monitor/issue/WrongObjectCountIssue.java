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
package com.exxeta.we.kubernetes.monitor.issue;

public class WrongObjectCountIssue implements Issue {
	private String region;
	private String stage;
	private String cluster;
	private String appName;
	private String objectClass;
	private int actualNumber;
	private int expectedNumber;
	

	public WrongObjectCountIssue(String region, String stage, String cluser, String appName, String objectClass, int actualNumber,
			int expectedNumber) {
		super();
		this.region = region;
		this.stage = stage;
		this.cluster = cluser;
		this.appName = appName;
		this.objectClass = objectClass;
		this.actualNumber = actualNumber;
		this.expectedNumber = expectedNumber;
	}

	@Override
	public String getSubject() {
		return ""+region+" - "+stage+" - "+appName + " wrong number of "+objectClass;
	}

	@Override
	public String getText() {
		return "Region: "+region +"\n Stage: "+stage+"\n Cluster:"+cluster+"\n Application: "+appName+"\n\nWrong number of  "+objectClass
				+"\nExpected: "+expectedNumber+"\nActual: "+actualNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + actualNumber;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result + expectedNumber;
		result = prime * result + ((objectClass == null) ? 0 : objectClass.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WrongObjectCountIssue other = (WrongObjectCountIssue) obj;
		if (actualNumber != other.actualNumber)
			return false;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
			return false;
		if (expectedNumber != other.expectedNumber)
			return false;
		if (objectClass == null) {
			if (other.objectClass != null)
				return false;
		} else if (!objectClass.equals(other.objectClass))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		if (stage == null) {
			if (other.stage != null)
				return false;
		} else if (!stage.equals(other.stage))
			return false;
		return true;
	}
}
