package com.djt.social.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Tom.Myers
 *
 */
public interface ISocialProviderApiKeyRepository {

	/** */
	String PROMO_DEPLOY_PATH = "PROMO_DEPLOY_PATH";
	
	/** */
	String PROVIDER_ID = "PROVIDER_ID";
	
	/** */
	String API_KEY = "API_KEY";

	/** */
	String API_KEY_SECRET = "API_KEY_SECRET";
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param apiKey
	 * @param apiKeySecret
	 */
	@Transactional
	void insertOrUpdateProviderApiKey(
		String promotionDeployPath,
		String providerId,
		String apiKey,
		String apiKeySecret);
	
	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 * @param apiKey
	 * @param apiKeySecret
	 */
	@Transactional
	void updateProviderApiKey(
		String promotionDeployPath,
		String providerId,
		String apiKey,
		String apiKeySecret);

	/**
	 * 
	 * @param promotionDeployPath
	 * @param providerId
	 */
	@Transactional
	void deleteProviderApiKey(
		String promotionDeployPath,
		String providerId);

	/**
	 * @return
	 */
	List<Map<String, String>> getAllProviderApiKeyPairs();
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @return
	 */
	Map<String, String> getProviderApiKeyPair(
		String promotionDeployPath, 
		String providerId);	
}



