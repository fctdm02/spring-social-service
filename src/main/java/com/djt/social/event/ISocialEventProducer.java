/**
 * 
 */
package com.djt.social.event;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialEventProducer {

	/**
	 * Fires a social event.
	 * 
	 * @param socialEvent
	 */
    void fireSocialEvent(SocialEvent socialEvent);   
}