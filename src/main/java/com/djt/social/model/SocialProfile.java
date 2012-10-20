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
public class SocialProfile {

	/* */
	@JsonProperty("providerId") 
	private String providerId;
	
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
	@JsonProperty("email") 
	private String email;
	
	@JsonProperty("firstName") 
	private String firstName;

	@JsonProperty("lastName") 
	private String lastName;

	@JsonProperty("fullName") 
	private String fullName;

	@JsonProperty("gender") 
	private String gender;

	/**
	 * @param providerId
	 * @param id
	 * @param name
	 * @param link
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param fullName
	 * @param gender
	 */
	@JsonCreator
	public SocialProfile(
		@JsonProperty("providerId") String providerId,
		@JsonProperty("id") String id, 
		@JsonProperty("name") String name, 
		@JsonProperty("link") String link,
		@JsonProperty("email") String email,
		@JsonProperty("firstName") String firstName,
		@JsonProperty("lastName") String lastName,
		@JsonProperty("fullName") String fullName,
		@JsonProperty("gender") String gender) {
		this.providerId = providerId;
		this.id = id;
		this.name = name;
		this.link = link;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.fullName = fullName;
		this.gender = gender;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
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
		sb.append(": providerId=");
		sb.append(this.providerId);
		sb.append(", id="); 
		sb.append(this.id);
		sb.append(", name="); 
		sb.append(this.name);
		sb.append(", link="); 
		sb.append(this.link);
		sb.append(", email="); 
		sb.append(this.email);
		sb.append(", firstName=");
		sb.append(this.firstName);
		sb.append(", lastName="); 
		sb.append(this.lastName);
		sb.append(", fullName="); 
		sb.append(this.fullName);
		sb.append(", gender="); 
		sb.append(this.gender);
		sb.append("}");
		return sb.toString();
	}
}