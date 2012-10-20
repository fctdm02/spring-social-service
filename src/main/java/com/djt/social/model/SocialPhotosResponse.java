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
public class SocialPhotosResponse {

	/* */
	@JsonProperty("error") 
	private String error;
	
	/* */
	@JsonProperty("count") 
	private int count;
	
	/* */
	@JsonProperty("socialPhotos") 
	private List<SocialPhoto> socialPhotos;
	
	/**
	 * @param error
	 * @param count
	 * @param socialPhotos
	 */
	@JsonCreator
	public SocialPhotosResponse(
		@JsonProperty("error") String error,
		@JsonProperty("count") int count, 
		@JsonProperty("socialPhotos") List<SocialPhoto> socialPhotos) {
		this.error = error;
		this.count = count;
		if (socialPhotos != null) {
			this.socialPhotos = socialPhotos;	
		} else {
			this.socialPhotos = new ArrayList<SocialPhoto>();
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
	 * @return the socialPhotos
	 */
	public List<SocialPhoto> getSocialPhotos() {
		return socialPhotos;
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
		sb.append(", socialPhotos=");
		sb.append(this.socialPhotos);
		sb.append("}");
		return sb.toString();
	}
}