package com.exxeta.we.kubernetes.monitor.rest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exxeta.we.kubernetes.monitor.config.MonitorConfig;
import com.exxeta.we.kubernetes.monitor.saver.DomainOverviewSaver;

public class Overview extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final RestConfig config;

	public Overview() {
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

		boolean envFound = false;

		for (MonitorConfig env : config.getEnvironments()) {

			if (req.getPathInfo().equals("/" + env.getName())) {
				PrintWriter out = resp.getWriter();

				byte[] json = DomainOverviewSaver.getSaver(env).loadJson();
				out.write(new String(json));
				envFound = true;
				break;
			}
		}

		if (!envFound) {
			System.out.println("Cannot find env:" + req.getPathInfo());
			resp.setStatus(404);
		}
	}
}
