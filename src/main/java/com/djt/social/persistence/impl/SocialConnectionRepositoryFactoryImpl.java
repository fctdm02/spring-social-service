/**
 * 
 */
package com.djt.social.persistence.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;

import com.djt.social.persistence.ISocialConnectionRepositoryFactory;
import com.djt.social.persistence.ISocialConnectionRepositoryFactoryLocator;

/**
 * 
 * @author Tom.Myers
 *
 */
public class SocialConnectionRepositoryFactoryImpl implements ISocialConnectionRepositoryFactory {

	/* */
	private Logger logger = Logger.getLogger(SocialConnectionRepositoryFactoryImpl.class);
	
	/* */
	private final JdbcTemplate jdbcTemplate;
	
	/* */
	private final ISocialConnectionRepositoryFactoryLocator socialConnectionRepositoryFactoryLocator;

	/* */
	private final TextEncryptor textEncryptor;

	/* */
	private ConnectionSignUp connectionSignUp;
	
	/**
	 * 
	 * @param dataSource
	 * @param socialConnectionRepositoryFactoryLocator
	 * @param textEncryptor
	 */
	public SocialConnectionRepositoryFactoryImpl(
		DataSource dataSource, 
		ISocialConnectionRepositoryFactoryLocator socialConnectionRepositoryFactoryLocator, 
		TextEncryptor textEncryptor) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.socialConnectionRepositoryFactoryLocator = socialConnectionRepositoryFactoryLocator;
		this.textEncryptor = textEncryptor;
	}

	/**
	 * The command to execute to create a new local user profile in the event no user id could be mapped to a connection.
	 * Allows for implicitly creating a user profile from connection data during a provider sign-in attempt.
	 * Defaults to null, indicating explicit sign-up will be required to complete the provider sign-in attempt.
	 * @see #findUserIdsWithConnection(Connection)
	 */
	public void setConnectionSignUp(ConnectionSignUp connectionSignUp) {
		this.connectionSignUp = connectionSignUp;
		// TODO: TDM: Remove
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.UsersConnectionRepository#findUserIdsWithConnection(org.springframework.social.connect.Connection)
	 */
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		
		ConnectionKey key = connection.getKey();
		List<String> localUserIds = jdbcTemplate.queryForList("SELECT UID FROM SOCIAL_USER_CONNECTION WHERE PROVIDER_ID = ? AND PROVIDER_USER_ID = ?", String.class, key.getProviderId(), key.getProviderUserId());		
		if (localUserIds.size() == 0 && connectionSignUp != null) {
			String newUserId = connectionSignUp.execute(connection);
			if (newUserId != null)
			{
				createConnectionRepository(newUserId).addConnection(connection);
				return Arrays.asList(newUserId);
			}
		}
		return localUserIds;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.UsersConnectionRepository#findUserIdsConnectedTo(java.lang.String, java.util.Set)
	 */
	@SuppressWarnings("unused")
	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		throw new RuntimeException("Not implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.social.connect.UsersConnectionRepository#createConnectionRepository(java.lang.String)
	 */
	@SuppressWarnings("unused")
	public ConnectionRepository createConnectionRepository(String userId) {
		throw new IllegalStateException("Unsupported operation.  Use createConnectionRepository(String promotionDeployPath, String userId) instead.");
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.persistence.ISocialJdbcUsersConnectionRepository#createConnectionRepository(java.lang.String, java.lang.String)
	 */
	public ConnectionRepository createConnectionRepository(String promotionDeployPath, String userId) {

		logger.debug("createConnectionRepository(): promotionDeployPath: " + promotionDeployPath);
				
		return new SocialConnectionRepositoryImpl(
			promotionDeployPath, 
			userId, 
			jdbcTemplate, 
			socialConnectionRepositoryFactoryLocator, 
			textEncryptor);
	}
}