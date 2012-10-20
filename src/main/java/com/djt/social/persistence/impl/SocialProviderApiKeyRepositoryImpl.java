/**
 * 
 */
package com.djt.social.persistence.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import com.djt.social.persistence.ISocialProviderApiKeyRepository;


/**
 * 
 * @author Tom.Myers
 * <pre>
	CREATE TABLE SOCIAL_PROVIDER_API_KEY (
	    PROMO_DEPLOY_PATH varchar(128) not null,
	    PROVIDER_ID varchar(128) not null,
	    API_KEY varchar(255) not null,
	    API_KEY_SECRET varchar(255),
	    PRIMARY KEY (PROMO_DEPLOY_PATH, PROVIDER_ID));
 * </pre>
 */
public final class SocialProviderApiKeyRepositoryImpl implements ISocialProviderApiKeyRepository {
	
	/* */
	private static final String INSERT_PROVIDER_API_KEY_SQL = "INSERT INTO SOCIAL_PROVIDER_API_KEY (PROMO_DEPLOY_PATH, PROVIDER_ID, API_KEY, API_KEY_SECRET) VALUES (?, ?, ?, ?)";

	/* */
	private static final String UPDATE_PROVIDER_API_KEY_SQL = "UPDATE SOCIAL_PROVIDER_API_KEY SET API_KEY = ?, API_KEY_SECRET = ? WHERE PROMO_DEPLOY_PATH = ? AND PROVIDER_ID = ?";
		
	/* */
	private static final String SELECT_ALL_PROVIDER_API_KEYS_SQL = "SELECT PROMO_DEPLOY_PATH, PROVIDER_ID, API_KEY, API_KEY_SECRET FROM SOCIAL_PROVIDER_API_KEY ORDER BY PROMO_DEPLOY_PATH";
	
	/* */
	private static final String SELECT_PROVIDER_API_KEY_SQL = "SELECT PROMO_DEPLOY_PATH, PROVIDER_ID, API_KEY, API_KEY_SECRET FROM SOCIAL_PROVIDER_API_KEY WHERE PROMO_DEPLOY_PATH = ? AND PROVIDER_ID = ?";
	
	/* */
	private static final String DELETE_PROVIDER_API_KEY_SQL = "DELETE FROM SOCIAL_PROVIDER_API_KEY WHERE PROMO_DEPLOY_PATH = ? AND PROVIDER_ID = ?";
	
	/* */
	private final JdbcTemplate jdbcTemplate;
	
	/* */
	private final TextEncryptor textEncryptor;
		
	/* */
	private final SocialProviderApiKeyConnectionMapper connectionMapper = new SocialProviderApiKeyConnectionMapper();
	
	/**
	 * 
	 * @param dataSource
	 * @param textEncryptor
	 */
	public SocialProviderApiKeyRepositoryImpl(
		DataSource dataSource, 
		TextEncryptor textEncryptor) {
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.textEncryptor = textEncryptor;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.persistence.ISocialProviderApiKeyRepository#insertOrUpdateProviderApiKey(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void insertOrUpdateProviderApiKey(
		String promotionDeployPath,
		String providerId,
		String apiKey,
		String apiKeySecret) {
		
		// See if the given API Key already exists.  If so, we do an update, otherwise, we do an insert.
		Map<String, String> map = this.getProviderApiKeyPair(promotionDeployPath, providerId);
		if (map != null) {
			updateProviderApiKey(
				promotionDeployPath, 
				providerId, 
				apiKey, 
				apiKeySecret);
		} else {
			this.jdbcTemplate.update(INSERT_PROVIDER_API_KEY_SQL,
				promotionDeployPath,				 
				providerId,
				encrypt(apiKey), 
				encrypt(apiKeySecret));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.persistence.ISocialProviderApiKeyRepository#updateProviderApiKey(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void updateProviderApiKey(
		String promotionDeployPath,
		String providerId,
		String apiKey,
		String apiKeySecret) {

		this.jdbcTemplate.update(UPDATE_PROVIDER_API_KEY_SQL,
			encrypt(apiKey), 
			encrypt(apiKeySecret),
			promotionDeployPath,
			providerId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.persistence.ISocialProviderApiKeyRepository#deleteProviderApiKey(java.lang.String, java.lang.String)
	 */
	public void deleteProviderApiKey(
		String promotionDeployPath,
		String providerId) {
		
		this.jdbcTemplate.update(
			DELETE_PROVIDER_API_KEY_SQL, 
			promotionDeployPath, 
			providerId);		
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.persistence.ISocialProviderApiKeyRepository#getAllProviderApiKeyPairs()
	 */
	public List<Map<String, String>> getAllProviderApiKeyPairs() {
		
		return this.jdbcTemplate.query(
			SELECT_ALL_PROVIDER_API_KEYS_SQL, 
			connectionMapper);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.persistence.ISocialProviderApiKeyRepository#getProviderApiKeyPair(java.lang.String, java.lang.String)
	 */
	public Map<String, String> getProviderApiKeyPair(
		String promotionDeployPath, 
		String providerId) {
		
		List<Map<String, String>> maps = this.jdbcTemplate.query(
			SELECT_PROVIDER_API_KEY_SQL, 
			connectionMapper, 
			promotionDeployPath, 
			providerId);
		
		if (maps.size() > 0) {
			return maps.get(0);
		} else {
			return null;
		}		
	}
	
	/*
	 * 
	 * @author Tom.Myers
	 *
	 */
	class SocialProviderApiKeyConnectionMapper implements RowMapper<Map<String, String>> {
		
		/*
		 * (non-Javadoc)
		 * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
		 */
		@SuppressWarnings("unused")
		public Map<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {

			Map<String, String> map = new HashMap<String, String>();
			
			map.put(PROMO_DEPLOY_PATH, rs.getString(PROMO_DEPLOY_PATH));
			map.put(PROVIDER_ID, rs.getString(PROVIDER_ID));
			map.put(API_KEY, decrypt(rs.getString(API_KEY)));
			map.put(API_KEY_SECRET, decrypt(rs.getString(API_KEY_SECRET)));

			return map;
		}

		/*
		 * 
		 * @param encryptedText
		 * @return
		 */
		private String decrypt(String encryptedText) {
			
			return encryptedText != null ? textEncryptor.decrypt(encryptedText) : encryptedText;
		}
	}
	
	/*
	 * 
	 * @param text
	 * @return
	 */
	private String encrypt(String text) {
		
		return text != null ? textEncryptor.encrypt(text) : text;
	}
}