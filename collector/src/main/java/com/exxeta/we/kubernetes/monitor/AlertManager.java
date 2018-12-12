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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.exxeta.we.kubernetes.monitor.config.ApplicationConfig;
import com.exxeta.we.kubernetes.monitor.config.ClusterConfig;
import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.issue.CannotReadObjectClassIssue;
import com.exxeta.we.kubernetes.monitor.issue.Issue;
import com.exxeta.we.kubernetes.monitor.issue.MalformedJsonIssue;
import com.exxeta.we.kubernetes.monitor.issue.UnexpectedObjectIssue;
import com.exxeta.we.kubernetes.monitor.issue.UnknownNamespaceIssue;
import com.exxeta.we.kubernetes.monitor.issue.WrongObjectCountIssue;

public class AlertManager {

	private final MonitorConfig config;

	public AlertManager(MonitorConfig config) {
		this.config = config;
	}

	public void checkForNewIssues(StatusReport oldStatus, StatusReport newStatus, String alertEmailAddress, String smtpServer) {
		List<Issue> oldIssues = getIssues(oldStatus);
		List<Issue> newIssues = getIssues(newStatus);
		newIssues.removeAll(oldIssues);
		for (Issue issue : newIssues){
			sendEmail(issue, alertEmailAddress, smtpServer);
		}
	}
	
	private List<Issue> getIssues(StatusReport report){
		ArrayList<Issue> result = new ArrayList<>();
		result.addAll(getUnknownNamespaceIssues(report));
		result.addAll(getApplicationIssues(report));
		if (report.isMalformedJson()){
			result.add(new MalformedJsonIssue());
		}
		return result;
	}

	private Collection<? extends Issue> getApplicationIssues(StatusReport report) {
		ArrayList<Issue> result = new ArrayList<>();
		for (ApplicationState appState : report.getApplications()) {
			result.addAll(getApplicationStateIssues(appState));
		}
		return result;
	}

	private Collection<? extends Issue> getApplicationStateIssues(ApplicationState appState) {
		ArrayList<Issue> result = new ArrayList<>();
		for (ApplicationInstanceState appInst : appState.getInstances()) {
			if (shouldSendAlerts(appState.getName(), appInst.getStage(), appInst.getRegion())){
				result.addAll(getApplicationInstanceIssues(appState.getName(), appInst));
			}

		}
		return result;
	}

	private boolean shouldSendAlerts(String appName, String stage, String region) {
		for (ClusterConfig cluster : config.getClusters()) {
			for (ApplicationConfig app : cluster.getApplications()){
				if (app.getName().equals(appName) && app.getRegion().equals(region) && app.getStage().equals(stage)){
					if (cluster.getSendAlerts()!= null && !cluster.getSendAlerts()){
						return false;
					}
					if (app.getSendAlerts()==null ){
						return true;
					} else {
						return app.getSendAlerts();
					}
				}
			}
		}
		return true;
	}

	private boolean shouldSendAlerts(String clusterName) {
		for (ClusterConfig cluster : config.getClusters()) {
			if (cluster.getName().equals(clusterName)){
				if (cluster.getSendAlerts()!=null){
					return cluster.getSendAlerts();
				} else {
					return true;
				}
			}
		}
		return true;
	}

	private Collection<? extends Issue> getApplicationInstanceIssues(String appName, ApplicationInstanceState newAppInst) {
		ArrayList<Issue> result = new ArrayList<>();
		for (ObjectClassState newObjectState: newAppInst.getObjectStates()){
			result.addAll(getObjectClassStateIssues (newObjectState, appName, newAppInst.getCluster(), newAppInst.getRegion(), newAppInst.getStage()));
		}
		return result;
	}

	private Collection<? extends Issue> getObjectClassStateIssues(ObjectClassState newObject, String appName, String cluster, String region, String stage) {
		ArrayList<Issue> result = new ArrayList<>();
		if (newObject.isErrorDuringSelection()){
			result.add(new CannotReadObjectClassIssue(region, stage, appName, newObject.getObjectClass()));
		}
		if (newObject.getExpectedNumber() != newObject.getActualNumber()){
			result.add(new WrongObjectCountIssue(region, stage, cluster, appName, newObject.getObjectClass(), newObject.getActualNumber(), newObject.getExpectedNumber()));
		}
		
//		if (!newObject.getUnexpectedObjects().isEmpty()){
//				result.add(new UnexpectedObjectIssue(region, stage, cluster, appName, newObject.getObjectClass(),newObject.getUnexpectedObjects()));
//		}
		return result;
	}

	private Collection<UnknownNamespaceIssue> getUnknownNamespaceIssues(StatusReport report) {
		ArrayList<UnknownNamespaceIssue> result = new ArrayList<>();
		for (UnknownNamespace unknownNS : report.getUnknownNamespaces()) {
			if (shouldSendAlerts(unknownNS.getClusterName())){
				result.add(new UnknownNamespaceIssue(unknownNS));
			}
		}
		return result;
	}

	private void sendEmail(Issue issue, String emailAddress, String smtpServer) {
	      String to = emailAddress;
	      String from = emailAddress;
	      String host = smtpServer;
	      Properties properties = System.getProperties();
	      properties.setProperty("mail.smtp.host", host);
	      Session session = Session.getDefaultInstance(properties);

	      try {
	         MimeMessage message = new MimeMessage(session);
	         message.setFrom(new InternetAddress(from));
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
	         message.setSubject(issue.getSubject());
	         message.setText(issue.getText());

	         // Send message
	         Transport.send(message);
	         System.out.println("Sent message successfully....");
	      } catch (MessagingException mex) {
	         mex.printStackTrace();
	      }
		
	}
	
}
