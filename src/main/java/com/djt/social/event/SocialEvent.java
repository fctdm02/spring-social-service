/**
 * 
 */
package com.djt.social.event;

import java.io.Serializable;

/**
 * 
 * @author Tom.Myers
 *
 */
public final class SocialEvent implements Serializable {

	/** */
	public static final String SOCIAL_PROVIDER_API_INSTALLED = "SOCIAL_PROVIDER_API_INSTALLED";

	/** */
	public static final String USER_ACCESS_TOKEN_REMOVED = "USER_ACCESS_TOKEN_REMOVED";

	
	/* */
	private static final long serialVersionUID = 1L;

	
	/* */
	private String promotionDeployPath;
	
	/* */
	private String providerId;
	
	/* */
	private String uid;
	
	/* */
	private String eventType;
	
	/* */
	private String eventDetails;
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param uid
	 * @param eventType
	 * @param eventDetails
	 */
	public SocialEvent(
		String promotionDeployPath,
		String providerId,
		String uid,
		String eventType,
		String eventDetails) {
		
		this.promotionDeployPath = promotionDeployPath;
		this.providerId = providerId;
		this.uid = uid;
		this.eventType = eventType;
		this.eventDetails = eventDetails;
	}

	/**
	 * @return the promotionDeployPath
	 */
	public String getPromotionDeployPath() {
		return promotionDeployPath;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @return the eventDetails
	 */
	public String getEventDetails() {
		return eventDetails;
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
		sb.append(": promotionDeployPath="); 
		sb.append(this.promotionDeployPath);
		sb.append(", providerId="); 
		sb.append(this.providerId);
		sb.append(", uid="); 
		sb.append(this.uid);
		sb.append(", eventType="); 
		sb.append(this.eventType);
		sb.append(", eventDetails="); 
		sb.append(this.eventDetails);
		sb.append("}");
		return sb.toString();
	}
}