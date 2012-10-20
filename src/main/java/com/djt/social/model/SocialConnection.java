/**
 *
 * 
 */
package com.djt.social.model;

import com.djt.social.service.ISocialService;


/**
 * TODO: TDM: Reconcile "SocialConnection" with "SocialConnectionData"
 * 
 * @author Tom.Myers
 */
public class SocialConnection {

	/* */
	private String promotionDeployPath = "";
	
	/* */
	private String providerId = "";

	/* */
	private String id = "";

	/* */
	private String uid = "";
	
	/* */
	private String cid = "";
	
	/* */
	private String data = "";
	
	/* */
	private String sourceUrl = "";
	
	/* */
	private String targetFilepath = "";
	
	/* */
	private String accessToken = "";
	
	/* */
	private String accessTokenSecret = "";

	/* */
	private String apiKey = "";
	
	/* */
	private String apiKeySecret = "";
	
	/* */
	private int offset = 0;
	
	/* */
	private int limit = ISocialService.DEFAULT_PAGE_SIZE;
	
	/* */
	private String status = "connected";
	
	/**
	 * 
	 */
	public SocialConnection() {
		
		String strLimit = System.getProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME);
		if (strLimit != null && strLimit.length() > 0) {
			limit = Integer.parseInt(strLimit);
		}
	}

	/**
	 * @param providerId
	 */
	public SocialConnection(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the promotionDeployPath
	 */
	public String getPromotionDeployPath() {
		return promotionDeployPath;
	}

	/**
	 * @param promotionDeployPath the promotionDeployPath to set
	 */
	public void setPromotionDeployPath(String promotionDeployPath) {
		this.promotionDeployPath = promotionDeployPath;
	}
	
	/**
	 * @return the providerId
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the sourceUrl
	 */
	public String getSourceUrl() {
		return sourceUrl;
	}

	/**
	 * @param sourceUrl the sourceUrl to set
	 */
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	/**
	 * @return the targetFilepath
	 */
	public String getTargetFilepath() {
		return targetFilepath;
	}

	/**
	 * @param targetFilepath the targetFilepath to set
	 */
	public void setTargetFilepath(String targetFilepath) {
		this.targetFilepath = targetFilepath;
	}
	
	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the accessTokenSecret
	 */
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	/**
	 * @param accessTokenSecret the accessTokenSecret to set
	 */
	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}
	
	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the apiKeySecret
	 */
	public String getApiKeySecret() {
		return apiKeySecret;
	}

	/**
	 * @param apiKeySecret the apiKeySecret to set
	 */
	public void setApiKeySecret(String apiKeySecret) {
		this.apiKeySecret = apiKeySecret;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the cid
	 */
	public String getCid() {
		return cid;
	}

	/**
	 * @param cid the cid to set
	 */
	public void setCid(String cid) {
		this.cid = cid;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		if (status != null && !status.isEmpty()) {
			this.status = status;
		}
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
		sb.append(", id=");
		sb.append(this.id);
		sb.append(", uid=");
		sb.append(this.uid);
		sb.append(", cid=");
		sb.append(this.cid);
		sb.append(", data=");
		sb.append(this.data);
		sb.append(", sourceUrl=");
		sb.append(this.sourceUrl);
		sb.append(", targetFilepath=");
		sb.append(this.targetFilepath);
		sb.append(", accessToken=");
		sb.append(this.accessToken);
		sb.append(", accessTokenSecret=");
		sb.append(this.accessTokenSecret);
		sb.append(", apiKey=");
		sb.append(this.apiKey);
		sb.append(", apiKeySecret=");
		sb.append(this.apiKeySecret);
		sb.append(", status=");
		sb.append(this.status);		
		sb.append("}");
		return sb.toString();
	}
}