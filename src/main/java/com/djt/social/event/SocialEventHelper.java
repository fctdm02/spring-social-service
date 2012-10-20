/**
 * 
 */
package com.djt.social.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 
 * @author Tom.Myers
 *
 */
public final class SocialEventHelper implements ISocialEventHelper {
	
	/** */
	private Logger logger = Logger.getLogger(SocialEventHelper.class);
	
	
    /* */
    private Set<ISocialEventListener> socialEventListeners;

    /**
     * 
     */
    public SocialEventHelper() {
        logger.debug("Creating social event listener set.");
        this.socialEventListeners = new HashSet<ISocialEventListener>();        
    }

    /*
     * (non-Javadoc)
     * @see com.djt.social.event.ISocialEventHelper#addSocialEventListener(com.djt.social.event.ISocialEventListener)
     */
    public synchronized void addSocialEventListener(ISocialEventListener socialEventListener) {
    	logger.debug("Adding social event listener: " + socialEventListener);
        this.socialEventListeners.add(socialEventListener);
    }

    /*
     * (non-Javadoc)
     * @see com.djt.social.event.ISocialEventHelper#getSocialEventListeners()
     */
    public synchronized List<ISocialEventListener> getSocialEventListeners() {
        List<ISocialEventListener> list = new ArrayList<ISocialEventListener>();
        list.addAll(this.socialEventListeners);
        return list;
    }

    /*
     * (non-Javadoc)
     * @see com.djt.social.event.ISocialEventHelper#removeSocialEventListener(com.djt.social.event.ISocialEventListener)
     */
    public synchronized void removeSocialEventListener(ISocialEventListener socialEventListener) {
    	logger.debug("Removing social event listener: " + socialEventListener);
        this.socialEventListeners.remove(socialEventListener);
    }

    /*
     * (non-Javadoc)
     * @see com.djt.social.event.ISocialEventHelper#removeAllSocialEventListeners()
     */
    public synchronized void removeAllSocialEventListeners() {
    	logger.debug("Removing all social event listeners.");
        this.socialEventListeners.clear();
    }    
}