package cipm.consistency.base.core.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cipm.consistency.base.shared.util.IGenericListener;

/**
 * Configuration of the validation feedback loop (VFL).
 * 
 * @author David Monschein
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationFeedbackLoopConfiguration {
	/**
	 * URL where the Headless PCM {@link https://github.com/dmonsch/PCM-Headless} is
	 * running.
	 */
	private String url;

	/**
	 * Port of the Headless PCM.
	 */
	private int port;

	/**
	 * Simulation time units.
	 */
	private long simulationTime = 150000;

	/**
	 * Measurements count.
	 */
	private long measurements = 10000;

	/**
	 * Service target id.
	 */
	private String targetServiceId = "";

	/**
	 * Minimum interarrival time for users to keep the simulations fast and stable.
	 */
	private double minInterarrivalTime = 0;

	/**
	 * Split of the monitoring data into training/ validation data, e.g. a value of
	 * 0.1 means that 10% of the monitoring are used for the validation and 90% for
	 * the training.
	 */
	private float validationShare;

	/**
	 * Listeners that are informed when this configuration changes.
	 */
	@JsonIgnore
	private List<IGenericListener<Void>> changeListener = new ArrayList<>();

	/**
	 * Sets the URL where the Headless PCM is running and informs all listeners.
	 * 
	 * @param url URL where the Headless PCM is running
	 */
	public void setUrl(String url) {
		this.url = url;
		this.changeListener.forEach(c -> c.inform(null));
	}

	/**
	 * Sets the port where the Headless PCM is running and informs all listeners.
	 * 
	 * @param port port where the Headless PCM is running
	 */
	public void setPort(int port) {
		this.port = port;
		this.changeListener.forEach(c -> c.inform(null));
	}

	/**
	 * Registers a new listener for changes within this configuration.
	 * 
	 * @param list the listener to add
	 */
	public void registerChangeListener(IGenericListener<Void> list) {
		this.changeListener.add(list);
	}

	/**
	 * Determines whether this configuration is valid.
	 * 
	 * @return true if the configuration is valid, false otherwise
	 */
	@JsonIgnore
	public boolean isValid() {
		return url != null && port > 0 && port < 65535 && simulationTime > 0 && validationShare >= 0
				&& validationShare <= 1;
	}

	public String getUrl() {
		return url;
	}

	public int getPort() {
		return port;
	}

	public long getSimulationTime() {
		return simulationTime;
	}

	public long getMeasurements() {
		return measurements;
	}

	public String getTargetServiceId() {
		return targetServiceId;
	}

	public double getMinInterarrivalTime() {
		return minInterarrivalTime;
	}

	public float getValidationShare() {
		return validationShare;
	}

	public void setSimulationTime(long simulationTime) {
		this.simulationTime = simulationTime;
	}

	public void setMeasurements(long measurements) {
		this.measurements = measurements;
	}

	public void setTargetServiceId(String targetServiceId) {
		this.targetServiceId = targetServiceId;
	}

	public void setMinInterarrivalTime(double minInterarrivalTime) {
		this.minInterarrivalTime = minInterarrivalTime;
	}

	public void setValidationShare(float validationShare) {
		this.validationShare = validationShare;
	}
}
