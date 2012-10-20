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
public class SocialProfileResponse {

	/* */
	@JsonProperty("error") 
	private String error = "";
	
	/* */
	@JsonProperty("socialProfile") 
	private SocialProfile socialProfile;
	
	/**
	 * @param error
	 * @param socialProfile
	 */
	@JsonCreator
	public SocialProfileResponse(
		@JsonProperty("error") String error,
		@JsonProperty("socialProfile") SocialProfile socialProfile) {
		this.error = error;
		this.socialProfile = socialProfile;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @return the socialProfile
	 */
	public SocialProfile getSocialProfile() {
		return socialProfile;
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
		sb.append(", socialProfile=");
		sb.append(this.socialProfile);
		sb.append("}");
		return sb.toString();
	}
}