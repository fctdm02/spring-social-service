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
public class SocialAlbum {

	/* */
	@JsonProperty("id") 
	private String id;
	
	/* */
	@JsonProperty("name") 
	private String name;
	
	/* */
	@JsonProperty("link") 
	private String link;

	/**
	 * 
	 * @param id
	 * @param name
	 * @param link
	 */
	@JsonCreator
	public SocialAlbum(
		@JsonProperty("id") String id, 
		@JsonProperty("name") String name, 
		@JsonProperty("link") String link) {
		this.id = id;
		this.name = name;
		this.link = link;
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
		sb.append("}");
		return sb.toString();
	}
}