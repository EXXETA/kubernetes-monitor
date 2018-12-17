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

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import com.exxeta.we.kubernetes.monitor.config.ApplicationConfig;
import com.exxeta.we.kubernetes.monitor.config.ClusterConfig;
import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.config.ObjectClassSelector;
import com.exxeta.we.kubernetes.monitor.saver.ReportSaver;
import com.google.gson.stream.MalformedJsonException;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.extensions.Deployment;
import io.fabric8.kubernetes.api.model.extensions.StatefulSet;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

public class Collector {

	public static void main(String[] args) throws Exception {
		new Collector().collect("config.json");

	}

	public StatusReport collect(String fileName) throws Exception {

		MonitorConfig config = MonitorConfig.getConfigFromFile(new File(fileName));

		ReportSaver saver = ReportSaver.getSaver(config);

		StatusReport result = new StatusReport();
		result.setDomainName(config.getName());

		for (ClusterConfig cluster : config.getClusters()) {
			try {
				checkCluster(cluster, result);
			} catch (Exception e) {
				if (e instanceof MalformedJsonException){
					result.setMalformedJson(true);
				} else {
					e.printStackTrace();
				}
				// TODO Save info in result
			}
		}

		StatusReport oldStatus = null;
		try {
			oldStatus = saver.load();
		} catch (Exception e) {
			System.err.println("Error while downloading old status");
			e.printStackTrace();
		}

		collectClusterAvailability(oldStatus, result, config);

		saver.save(result);

		System.out.println("Uploaded status");

		new AlertManager(config).checkForNewIssues(oldStatus, result, config.getAlertEmailAddress(),
				config.getSmptServer());
		
		return result;

	}

	private void collectClusterAvailability(StatusReport oldStatus, StatusReport result, MonitorConfig config) {
		for (ClusterConfig cluster : config.getClusters()) {
			ClusterAvailablility oldAvailability = oldStatus==null?null:oldStatus.getClusterAvailability(cluster.getName());
			ClusterAvailablility newAvailability = new ClusterAvailablility(oldAvailability);
			newAvailability.setClusterName(cluster.getName());
			boolean available = true;
			try {
				DefaultKubernetesClient client = createKubernetesClient(cluster);
				for (Node node : client.nodes().list().getItems()) {
					for (NodeCondition condition : node.getStatus().getConditions()) {
						if ("Ready".equals(condition.getType()) && !"True".equals(condition.getStatus())) {
							available = false;
						}
					}
				}
				newAvailability.setNumberOfNodes(client.nodes().list().getItems().size());
				newAvailability.setAvailable(available);

			} catch (Exception e) {
				e.printStackTrace();
				available = false;
			}
			if (available) {
				if (oldAvailability != null && oldAvailability.getAvailableMillis() != null
						&& oldAvailability.getLastUpdate() != null) {
					newAvailability.setAvailableMillis(oldAvailability.getAvailableMillis()
							+ (newAvailability.getLastUpdate() - oldAvailability.getLastUpdate()));
				} else {
					newAvailability.setAvailableMillis(0l);
				}
			}
			result.addClusterAvailability(newAvailability);
		}

	}

	private void checkCluster(ClusterConfig cluster, StatusReport result) throws IOException {

		DefaultKubernetesClient client = createKubernetesClient(cluster);

		for (Namespace ns : client.namespaces().list().getItems()) {
			String namespaceName = ns.getMetadata().getName();
			if (shouldSkip(cluster.getSkipNamespaces(), namespaceName)) {
				continue;
			}
			boolean unknonwnNamespace = true;
			for (ApplicationConfig application : cluster.getApplications()) {
				if (application.getNamespace().equals(namespaceName)) {
					unknonwnNamespace = false;
				}
			}
			if (unknonwnNamespace) {
				result.addUnknownNamespace(cluster.getName(), namespaceName);
			}
		}

		for (ApplicationConfig application : cluster.getApplications()) {
			checkApplication(client, application, result, cluster.getName());
		}
		client.close();
		result.getApplications().sort(new Comparator<ApplicationState>() {

			@Override
			public int compare(ApplicationState arg0, ApplicationState arg1) {
				if (arg0 == arg1) {
					return 0;
				}
				if (arg0 == null || arg0.getName() == null) {
					return -1;
				}
				if (arg1 == null || arg1.getName() == null) {
					return 1;
				}
				return arg0.getName().compareTo(arg1.getName());
			}

		});

	}

	private boolean shouldSkip(List<String> skipNamespaces, String namespaceName) {
		for (String skipNamespace : skipNamespaces) {
			if (Pattern.matches(skipNamespace, namespaceName)) {
				return true;
			}
		}
		return false;
	}

	private DefaultKubernetesClient createKubernetesClient(ClusterConfig cluster) throws IOException {
		String kubeconfigContents = new String(Files.readAllBytes(new File(cluster.getKubeconfig()).toPath()),
				StandardCharsets.UTF_8);

		Config kubeConfig = Config.fromKubeconfig(kubeconfigContents);
		return new DefaultKubernetesClient(kubeConfig);
	}

	private void checkApplication(DefaultKubernetesClient client, ApplicationConfig application, StatusReport result, String clusterName) {

		ApplicationInstanceState state = new ApplicationInstanceState(application.getStage(), application.getRegion(), clusterName, application.getNamespace());
		if (application.getSelectors().isEmpty()) {
			System.err.println("no selectors for " + application.getName() + " " + application.getRegion() + " "
					+ application.getStage());
		}
		for (ObjectClassSelector selector : application.getSelectors()) {
			state.addObjectState(checkObjectClass(client, application, selector));
		}
		result.setApplicationInstanceState(application.getName(), state);

		client.pods().inNamespace(application.getNamespace()).list().getItems().stream()
				.filter(pod -> pod.getStatus().getContainerStatuses().stream()
						.filter(container -> !container.getReady()).count() > 0)
				.map(pod -> pod.getMetadata().getName()).forEach(podName -> state.addPodNotReady(podName));

		getContainers(client, application, state);
	}

	private void getContainers(DefaultKubernetesClient client, ApplicationConfig application,
			ApplicationInstanceState state) {
		try {
			for (Deployment depl : client.extensions().deployments().inNamespace(application.getNamespace()).list()
					.getItems()) {
				String deploymentName = depl.getMetadata().getName();
				for (Container container : depl.getSpec().getTemplate().getSpec().getContainers()) {
					String image = container.getImage();
					String version = image.substring(image.lastIndexOf(':') + 1);
					String containerName = container.getName();
					state.addContainer(deploymentName + " - " + containerName, version,
							getContainerStatus(client, application.getNamespace(), deploymentName, containerName));
				}
			}
		} catch (KubernetesClientException e) {
			e.printStackTrace();
		}
		try {
			for (StatefulSet statefulSet : client.apps().statefulSets().inNamespace(application.getNamespace()).list()
					.getItems()) {
				String setName = statefulSet.getMetadata().getName();
				for (Container container : statefulSet.getSpec().getTemplate().getSpec().getContainers()) {
					String image = container.getImage();
					String version = image.substring(image.lastIndexOf(':') + 1);
					String containerName = container.getName();
					state.addContainer(setName + " - " + containerName, version,
							getContainerStatus(client, application.getNamespace(), setName, containerName));
				}
			}
		} catch (KubernetesClientException e) {
			e.printStackTrace();
		}

	}

	private Boolean getContainerStatus(DefaultKubernetesClient client, String namespace, String prefix,
			String containerName) {
		for (Pod pod : client.pods().inNamespace(namespace).list().getItems()) {
			if (pod.getMetadata().getName().startsWith(prefix)) {
				for (ContainerStatus c : pod.getStatus().getContainerStatuses()) {
					if (c.getName().equals(containerName)) {
						return c.getReady();
					}
				}
			}
		}
		return null;
	}

	private ObjectClassState checkObjectClass(DefaultKubernetesClient client, ApplicationConfig application,
			ObjectClassSelector selector) {
		ObjectClassState state = new ObjectClassState();
		state.setObjectClass(selector.getObjectClass());
		state.setExpectedNumber(selector.getExpectedNumber());
		try {
			List<? extends HasMetadata> items = null;
			if ("service".equals(selector.getObjectClass())) {
				items = client.services().inNamespace(application.getNamespace()).list().getItems();
			} else if ("deployment".equals(selector.getObjectClass())) {
				items = client.extensions().deployments().inNamespace(application.getNamespace()).list().getItems();
			} else if ("replicaSet".equals(selector.getObjectClass())) {
				items = client.extensions().replicaSets().inNamespace(application.getNamespace()).list().getItems()
						.stream().filter(replicaset -> replicaset.getSpec().getReplicas() > 0).collect(toList());
			} else if ("pod".equals(selector.getObjectClass())) {
				items = client.pods().inNamespace(application.getNamespace()).list().getItems().stream()
						.filter(pod -> !pod.getStatus().getPhase().equals("Failed")
								&& !pod.getStatus().getPhase().equals("Succeeded"))
						.collect(toList());
			} else if ("statefulSet".equals(selector.getObjectClass())) {
				items = client.apps().statefulSets().inNamespace(application.getNamespace()).list().getItems();
			} else {
				return state;
			}
			for (HasMetadata item : items) {
				if (!isValid(selector.getPatterns(), item.getMetadata().getName())) {
					state.addUnexpectedObject(item.getMetadata().getName());
				} else {
					state.incActualNumber();
					state.addExpectedObject(item.getMetadata().getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		state.setErrorDuringSelection(false);
		return state;

	}

	private boolean isValid(List<String> pods, String name) {
		if (pods == null)
			return false;
		for (String podRegEx : pods) {
			if (Pattern.matches(podRegEx, name)) {
				return true;
			}
		}
		return false;
	}

}
