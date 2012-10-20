/**
 *
 * 
 */
package com.djt.social.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * 
 * @author Tom.Myers
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class SocialVideosResponse {

	/* */
	@JsonProperty("error") 
	private String error;
	
	/* */
	@JsonProperty("count") 
	private int count;
	
	/* */
	@JsonProperty("socialVideos") 
	private List<SocialVideo> socialVideos;
	
	/**
	 * @param error
	 * @param count
	 * @param socialVideos
	 */
	@JsonCreator
	public SocialVideosResponse(
		@JsonProperty("error") String error,
		@JsonProperty("count") int count, 
		@JsonProperty("socialVideos") List<SocialVideo> socialVideos) {
		this.error = error;
		this.count = count;
		if (socialVideos != null) {
			this.socialVideos = socialVideos;	
		} else {
			this.socialVideos = new ArrayList<SocialVideo>();
		}		
	}
	
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return the socialVideos
	 */
	public List<SocialVideo> getSocialVideos() {
		return socialVideos;
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
		sb.append(", count=");
		sb.append(this.count);		
		sb.append(", socialVideos=");
		sb.append(this.socialVideos);
		sb.append("}");
		return sb.toString();
	}
	
}