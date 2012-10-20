package com.djt.social.persistence;

import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;


/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialConnectionRepositoryFactory extends UsersConnectionRepository {

	/**
	 * 
	 * @param promotionDeployPath
	 * @param userId
	 * @return
	 */
	ConnectionRepository createConnectionRepository(String promotionDeployPath, String userId);
	
}
