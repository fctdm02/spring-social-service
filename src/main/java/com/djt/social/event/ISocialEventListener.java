/**
 * 
 */
package com.djt.social.event;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialEventListener {

	/**
	 * Invoked when a social event occurs, such as when a 
	 * social provider API key is added or when an
	 * end-user access token is deleted. 
	 * 
	 * @param socialEvent The Social Event that occurred.
	 */
	void socialEventOccurred(SocialEvent socialEvent);
}