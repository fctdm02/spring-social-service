/**
 * 
 */
package com.djt.social;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * 
 * @author Tom.Myers
 *
 */
public abstract class AbstractSocialServiceIntegrationTest {
		
    @Deployment
    public static WebArchive deploy() {
        return WebArchiveUtil.getWar();
    }	
}