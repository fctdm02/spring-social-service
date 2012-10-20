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
public class SocialAlbumsResponse {

	/* */
	@JsonProperty("error") 
	private String error;
	
	/* */
	@JsonProperty("count") 
	private int count;
	
	/* */
	@JsonProperty("socialAlbums") 
	private List<SocialAlbum> socialAlbums;
	
	/**
	 * @param error
	 * @param count
	 * @param socialAlbums
	 */
	@JsonCreator
	public SocialAlbumsResponse(
		@JsonProperty("error") String error,	
		@JsonProperty("count") int count, 
		@JsonProperty("socialAlbums") List<SocialAlbum> socialAlbums) {
		this.error = error;
		this.count = count;
		if (socialAlbums != null) {
			this.socialAlbums = socialAlbums;
		} else {
			this.socialAlbums = new ArrayList<SocialAlbum>();
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
	 * @return the socialAlbums
	 */
	public List<SocialAlbum> getSocialAlbums() {
		return socialAlbums;
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
		sb.append(", socialAlbums=");
		sb.append(this.socialAlbums);
		sb.append("}");
		return sb.toString();
	}
}