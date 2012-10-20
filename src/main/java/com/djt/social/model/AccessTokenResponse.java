/**
 *
 * 
 */
package com.djt.social.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * TODO: TDM: Need to add "providerUserId"
 * 
 * @author Tom.Myers
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AccessTokenResponse {

	/* */
	@JsonProperty("error") 
	private String error;
	
	/* */
	@JsonProperty("accessToken") 
	private String accessToken;

	/* */
	@JsonProperty("accessTokenSecret") 
	private String accessTokenSecret;
	
	/**
	 * @param error
	 * @param accessToken
	 * @param accessTokenSecret
	 */
	@JsonCreator
	public AccessTokenResponse(
		@JsonProperty("error") String error,
		@JsonProperty("accessToken") String accessToken,
		@JsonProperty("accessTokenSecret") String accessTokenSecret) {
		this.error = error;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return the accessTokenSecret
	 */
	public String getAccessTokenSecret() {
		return accessTokenSecret;
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
		sb.append(", accessToken=");
		sb.append(this.accessToken);
		sb.append(", accessTokenSecret=");
		sb.append(this.accessTokenSecret);
		sb.append("}");
		return sb.toString();
	}
}