package com.djt.social.adapter.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.twitter.api.CursoredList;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.TwitterProfile;

import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 */
public final class TwitterSocialApiAdapter extends AbstractSocialApiAdapter {

	/* */
	private Twitter twitterApi;

	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param ISocialMediaQueryCache
	 * @param socialUrlSalt
	 * @param twitterApi
	 */
	public TwitterSocialApiAdapter(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		ISocialMediaQueryCache socialMediaQueryCache,
		String socialUrlSalt,
		Twitter twitterApi) {

		super(
			promotionDeployPath, 
			providerId, 
			accessToken, 
			socialMediaQueryCache,
			socialUrlSalt);

		this.twitterApi = twitterApi;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.adapter.ISocialApiAdapter#getSocialProfile()
	 */
	public SocialProfileResponse getSocialProfile()  {
		
		SocialProfileResponse socialProfileResponse = null;
		
		try {
			
			SocialProfile socialProfile = this.getSocialMediaQueryCache().getSocialProfile(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken());
			
			if (socialProfile == null) {

				TwitterProfile twitterProfile = this.twitterApi.userOperations().getUserProfile();
				
				String email = "";
				String firstName = "";
				String lastName = "";
				String gender = "";
				
				socialProfile = new SocialProfile(
					ISocialService.PROVIDER_NAME_TWITTER,
					Long.toString(twitterProfile.getId()),
					twitterProfile.getScreenName(),
					twitterProfile.getProfileImageUrl(),
					email,
					firstName,
					lastName,
					twitterProfile.getName(),
					gender);
				
				this.getSocialMediaQueryCache().putSocialProfile(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(),
					socialProfile);
			}
			
			socialProfileResponse = new SocialProfileResponse("", socialProfile);
			
		} catch (Exception e) {
			socialProfileResponse = new SocialProfileResponse(e.getMessage(), null);
		}
		
		return socialProfileResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.adapter.ISocialApiAdapter#getSocialFriends(java.lang.String, int, int)
	 */
	@SuppressWarnings("unused")
	public SocialFriendsResponse getSocialFriends(
		String id,
		int offset, 
		int limit) {
		
		try {

			List<SocialProfile> socialFriends = new ArrayList<SocialProfile>();

			CursoredList<TwitterProfile> friends = this.twitterApi.friendOperations().getFriends();

			String email = "";
			String firstName = "";
			String lastName = "";
			String gender = "";
			
		    int count = friends.size();
		    if (offset >=0 && offset < count) {

		    	for (int i=offset; i < offset+limit && i < count; i++) {
		    
		    		TwitterProfile twitterProfile = friends.get(i);

		    		socialFriends.add(new SocialProfile(
		    			ISocialService.PROVIDER_NAME_TWITTER,
		    			Long.toString(twitterProfile.getId()),
		    			twitterProfile.getScreenName(),
		    			twitterProfile.getProfileImageUrl(),
		    			email,
		    			firstName,
		    			lastName,
		    			twitterProfile.getName(),
		    			gender));
		    	}
		    }

		    return new SocialFriendsResponse("", count, socialFriends);
			
		} catch (Exception e) {
			return new SocialFriendsResponse(e.getMessage(), 0, null);
		}
	}	
}