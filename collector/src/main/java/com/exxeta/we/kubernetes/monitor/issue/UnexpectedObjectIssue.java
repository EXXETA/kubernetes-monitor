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

import java.util.List;

public class UnexpectedObjectIssue implements Issue {
	private String region;
	private String stage;
	private String appName;
	private String objectClass;
	private List<String> unexpectedObjects;

	public UnexpectedObjectIssue(String region, String stage, String appName, String objectClass, List<String> unexpectedObjects) {
		super();
		this.region = region;
		this.stage = stage;
		this.appName = appName;
		this.objectClass = objectClass;
		this.unexpectedObjects=unexpectedObjects;
	}
	
	@Override
	public String getSubject() {
		return ""+region+" - "+stage+" - "+appName + " Unexpected "+objectClass;
	}

	@Override
	public String getText() {
		String result = "Region: "+region +"\n Stage: "+stage+"\n Application: "+appName+"\n\nUnexpected "+objectClass;
		for (String unexpectedObject : unexpectedObjects){
			result+="\n* "+unexpectedObject;
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appName == null) ? 0 : appName.hashCode());
		result = prime * result + ((objectClass == null) ? 0 : objectClass.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		result = prime * result + ((unexpectedObjects == null) ? 0 : unexpectedObjects.hashCode());
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
		UnexpectedObjectIssue other = (UnexpectedObjectIssue) obj;
		if (appName == null) {
			if (other.appName != null)
				return false;
		} else if (!appName.equals(other.appName))
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
		if (unexpectedObjects == null) {
			if (other.unexpectedObjects != null)
				return false;
		} else if (!unexpectedObjects.equals(other.unexpectedObjects))
			return false;
		return true;
	}
}
