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
public abstract class SocialMedia {

	/* */
	@JsonProperty("id") 
	private String id;
	
	/* */
	@JsonProperty("name") 
	private String name;
	
	/* */
	@JsonProperty("link") 
	private String link;

	/* */
	@JsonProperty("thumbnail") 
	private String thumbnail;
	
	/* */
	@JsonProperty("checksum")
	private String checksum;
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param link
	 * @param thumbnail
	 * @param checksum
	 */
	@JsonCreator
	public SocialMedia(
		@JsonProperty("id") String id, 
		@JsonProperty("name") String name, 
		@JsonProperty("link") String link,
		@JsonProperty("thumbnail") String thumbnail,
		@JsonProperty("checksum") String checksum) {
		this.id = id;
		this.name = name;
		this.link = link;
		this.thumbnail = thumbnail;
		this.checksum = checksum;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * @return the thumbnail
	 */
	public String getThumbnail() {
		return thumbnail;
	}

	/**
	 * @return the checksum
	 */
	public String getChecksum() {
		return checksum;
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
		sb.append(": id="); 
		sb.append(this.id);
		sb.append(", name="); 
		sb.append(this.name);
		sb.append(", link="); 
		sb.append(this.link);
		sb.append(", thumbnail="); 
		sb.append(this.thumbnail);
		sb.append(", checksum="); 
		sb.append(this.checksum);
		sb.append("}");
		return sb.toString();
	}
}