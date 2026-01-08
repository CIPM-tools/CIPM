package org.pcm.headless.api.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import org.pcm.headless.api.util.PCMUtil;

public class PCMHeadlessClient {
	private static final long DEFAULT_TIMEOUT = 30000;

	private static boolean PCM_INITED = false;

	private static final String PING_URL = "rest/ping";
	private static final String CLEAR_URL = "rest/clear";
	private static final String PREPARE_URL = "rest/prepare";

	private String baseUrl;

	private HttpClient client;

	public PCMHeadlessClient(String baseUrl) {
		this.client = produceClient(DEFAULT_TIMEOUT);
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
		if (!this.baseUrl.startsWith("http://")) {
			this.baseUrl = "http://" + this.baseUrl;
		}

		if (!PCM_INITED) {
			PCMUtil.loadPCMModels();
			PCM_INITED = true;
		}
	}

	/**
	 * Makes a full clean, this means all analysis results and states are removed.
	 * Be aware: this can break currently running simulations.
	 */
	public boolean clear() {
		HttpRequest request = HttpRequest
				.newBuilder(URI.create(this.baseUrl + CLEAR_URL))
				.timeout(client.connectTimeout().get())
				.build();
		try {
			client.send(request, BodyHandlers.discarding());
			return true;
		} catch (IOException | InterruptedException e) {
			return false;
		}
	}

	/**
	 * Determines whether the backend is reachable.
	 * 
	 * @param timeout the time to wait for a response
	 * @return true if the backend is reachable, false if not
	 */
	public boolean isReachable(long timeout) {
		HttpClient client = produceShallow(timeout);
		HttpRequest request = HttpRequest
				.newBuilder(URI.create(this.baseUrl + PING_URL))
				.timeout(client.connectTimeout().get())
				.build();

		boolean reach;
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			reach = response.body().equals("{}");
		} catch (IOException | InterruptedException e) {
			reach = false;
		}
		return reach;
	}

	/**
	 * Prepares a simulation and returns a client to configure and start it.
	 * 
	 * @return instance of {@link SimulationClient} which can be used to execute a
	 *         simulation.
	 */
	public SimulationClient prepareSimulation() {
		HttpRequest request = HttpRequest
				.newBuilder(URI.create(this.baseUrl + PREPARE_URL))
				.timeout(client.connectTimeout().get())
				.build();
		try {
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
			return new SimulationClient(this.baseUrl, response.body(), this.client);
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}

	private HttpClient produceClient(long timeout) {
		return HttpClient.newBuilder().connectTimeout(Duration.of(timeout, ChronoUnit.MILLIS)).build();
	}

	private HttpClient produceShallow(long timeout) {
		return HttpClient.newBuilder().connectTimeout(Duration.of(timeout, ChronoUnit.MILLIS)).build();
	}
}
