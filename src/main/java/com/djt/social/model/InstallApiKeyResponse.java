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
public class InstallApiKeyResponse {

	/* */
	@JsonProperty("error") 
	private String error;
	
	/* */
	@JsonProperty("result") 
	private String result;
	
	/**
	 * @param error
	 * @param result
	 */
	@JsonCreator
	public InstallApiKeyResponse(
		@JsonProperty("error") String error,
		@JsonProperty("result") String result) {
		this.error = error;
		this.result = result;
	}
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
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
		sb.append(", result=");
		sb.append(this.result);
		sb.append("}");
		return sb.toString();
	}
}