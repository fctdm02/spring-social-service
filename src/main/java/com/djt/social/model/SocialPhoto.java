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
public class SocialPhoto extends SocialMedia {

	/**
	 * 
	 * @param id
	 * @param name
	 * @param link
	 * @param thumbnail
	 * @param checksum
	 */
	@JsonCreator
	public SocialPhoto(
		@JsonProperty("id") String id, 
		@JsonProperty("name") String name, 
		@JsonProperty("link") String link,
		@JsonProperty("thumbnail") String thumbnail,
		@JsonProperty("checksum") String checksum) {
		super(id, name, link, thumbnail, checksum);
	}
}