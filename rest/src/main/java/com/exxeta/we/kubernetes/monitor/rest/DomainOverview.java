package com.exxeta.we.kubernetes.monitor.rest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exxeta.com.we.kubernetes.monitor.Domain;
import com.exxeta.we.kubernetes.monitor.ApplicationInstanceState;
import com.exxeta.we.kubernetes.monitor.ApplicationState;
import com.exxeta.we.kubernetes.monitor.ObjectClassState;
import com.exxeta.we.kubernetes.monitor.StatusReport;
import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.ReportSaver;
import com.google.gson.Gson;

public class DomainOverview extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final RestConfig config;

	public DomainOverview() {
		RestConfig loadedConfig = null;
		try {
			loadedConfig = RestConfig.getConfigFromFile(new File("kubeRest.json"));
		} catch (IOException e) {
			System.out.println(new File("kubeRest.json").getAbsolutePath());

			e.printStackTrace();
		}
		config = loadedConfig;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json");

		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");

		PrintWriter out = resp.getWriter();
		Gson gson = new Gson();
		Domain domain = new Domain();
		boolean envFound = false;
		
		for (MonitorConfig env : config.getEnvironments()) {

			if (req.getPathInfo().equals("/" + env.getName())) {
				envFound = true;

				domain.setName(env.getName());

				StatusReport statusReport = ReportSaver.getSaver(env).load();

				statusReport.getClusterAvailability();
				domain.setClusterConfig(statusReport.getClusterAvailability());
				domain.setLastUpdate(statusReport.getTimestamp());

				for (ApplicationState application : statusReport.getApplications()) {

					for (ApplicationInstanceState applicationInstanceState : application.getInstances()) {

						for (ObjectClassState objectClassState : applicationInstanceState.getObjectStates()) {

							if (objectClassState.getExpectedNumber() > 0) {

								if (objectClassState.getExpectedNumber() == objectClassState.getActualNumber()) {
									// Alles gut
								} else if (objectClassState.getActualNumber() == 0) {
									// Alle down
									domain.setStatus("danger");
								} else if (objectClassState.getActualNumber() > 0) {
									// Mindestens eins ist noch aktiv
									domain.setStatus("warning");
								}
							}
							
							if(!objectClassState.getUnexpectedObjects().isEmpty()) {
								domain.setStatus("warning");
							}
						}
					}
				}
			}
		}
		
		if (!envFound) {
			System.out.println("Cannot find env:" + req.getPathInfo());
			resp.setStatus(404);
		} else {
			out.write(new String(gson.toJson(domain)));
		}

	}

}
