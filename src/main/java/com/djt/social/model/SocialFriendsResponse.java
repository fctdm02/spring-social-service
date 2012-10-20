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
public class SocialFriendsResponse {

	/* */
	@JsonProperty("error") 
	private String error;

	/* */
	@JsonProperty("count") 
	private int count;
	
	/* */
	@JsonProperty("socialFriends") 
	private List<SocialProfile> socialFriends;
	
	/**
	 * @param error
	 * @param count
	 * @param socialFriends
	 */
	@JsonCreator
	public SocialFriendsResponse(
		@JsonProperty("error") String error,
		@JsonProperty("count") int count, 
		@JsonProperty("socialFriends") List<SocialProfile> socialFriends) {
		this.error = error;
		this.count = count;
		if (socialFriends != null) {
			this.socialFriends = socialFriends;
		} else {
			this.socialFriends = new ArrayList<SocialProfile>();
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
	 * @return the socialFriends
	 */
	public List<SocialProfile> getSocialFriends() {
		return socialFriends;
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
		sb.append(", socialFriends=");
		sb.append(this.socialFriends);
		sb.append("}");
		return sb.toString();
	}
}