/**
 *
 * 
 */
package com.djt.social.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author Tom.Myers
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class VersionResponse {

	/* */
	@JsonProperty("error") 
	private String error;

	/* */
	@JsonProperty("version") 
	private String version;

	/* */
	@JsonProperty("timestamp") 
	private String timestamp;
	
	/**
	 * @param error
	 * @param version
	 * @param timestamp
	 */
	@JsonCreator
	public VersionResponse(
		@JsonProperty("error") String error,
		@JsonProperty("version") String version,
		@JsonProperty("timestamp") String timestamp) {
		this.error = error;
		this.version = version;
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(this.getClass().getSimpleName());
		sb.append(": error=");
		sb.append(this.error);
		sb.append(", version=");
		sb.append(this.version);
		sb.append(", timestamp=");
		sb.append(this.timestamp);
		sb.append("}");
		return sb.toString();
	}
}