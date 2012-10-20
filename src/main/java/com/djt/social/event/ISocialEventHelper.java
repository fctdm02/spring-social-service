/**
 * 
 */
package com.djt.social.event;

import java.util.List;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialEventHelper {
	
    /**
     * 
     * @param socialEventListener
     */
    void addSocialEventListener(ISocialEventListener socialEventListener);

    /**
     * 
     * @return
     */
    List<ISocialEventListener> getSocialEventListeners();

    /**
     * 
     * @param socialEventListener
     */
    void removeSocialEventListener(ISocialEventListener socialEventListener);

    /**
     * 
     */
    void removeAllSocialEventListeners();    
}