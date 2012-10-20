package com.djt.social.adapter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.social.instagram.api.Caption;
import org.springframework.social.instagram.api.Image;
import org.springframework.social.instagram.api.Instagram;
import org.springframework.social.instagram.api.InstagramProfile;
import org.springframework.social.instagram.api.Media;
import org.springframework.social.instagram.api.PagedMediaList;
import org.springframework.social.instagram.api.Pagination;

import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 */
public final class InstagramSocialApiAdapter extends AbstractSocialApiAdapter {

	/* */
	private Logger logger = Logger.getLogger(InstagramSocialApiAdapter.class);
	
	/* */
	private Instagram instagramApi;

	/* */
	private final static int MAX_PHOTOS_PER_PAGE = 100;
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param ISocialMediaQueryCache
	 * @param socialUrlSalt
	 * @param instagramApi
	 */
	public InstagramSocialApiAdapter(
		String promotionDeployPath,
		String providerId,		
		String accessToken,
		ISocialMediaQueryCache socialMediaQueryCache,
		String socialUrlSalt,
		Instagram instagramApi) {
		
		super(
			promotionDeployPath, 
			providerId, 
			accessToken, 
			socialMediaQueryCache,
			socialUrlSalt);

		this.instagramApi = instagramApi;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.adapter.ISocialApiAdapter#getSocialProfile()
	 */
	public SocialProfileResponse getSocialProfile() {
		
		SocialProfileResponse socialProfileResponse = null;
		
		try {
			
			SocialProfile socialProfile = this.getSocialMediaQueryCache().getSocialProfile(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken());
			
			if (socialProfile == null) {

			InstagramProfile instagramProfile = this.instagramApi.userOperations().getUser();
			
			String email = "";
			String gender = "";
			
			socialProfile = new SocialProfile(
				ISocialService.PROVIDER_NAME_INSTAGRAM,
				instagramProfile.getId(),
				instagramProfile.getUsername(),
				instagramProfile.getProfilePictureUrl(),
				email,
				instagramProfile.getFirstName(),
				instagramProfile.getLastName(),
				instagramProfile.getFullName(),
				gender);
				
				this.getSocialMediaQueryCache().putSocialProfile(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(),
					socialProfile);
			}
			
			socialProfileResponse = new SocialProfileResponse("", socialProfile);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			socialProfileResponse = new SocialProfileResponse(e.getMessage(), null);
		}
		
		return socialProfileResponse;
	}
		
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, int, int)
	 */
	@SuppressWarnings("unused")
	public SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		String id, 
		int offset, 
		int limit) {
		
		SocialPhotosResponse socialPhotosResponse = null;
		
		try {
			
			List<SocialPhoto> allSocialPhotos = this.getSocialMediaQueryCache().getSocialPhotos(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken());

			int count = 0;
			if (allSocialPhotos == null) {
				
				allSocialPhotos = new ArrayList<SocialPhoto>();

				SocialProfileResponse socialProfileResponse = this.getSocialProfile();
				SocialProfile socialProfile = socialProfileResponse.getSocialProfile();
				String userId = socialProfile.getId();
																
				// Iterate through all photos, building up the entire list in the and then return the appropriate page to the client.				
				PagedMediaList pagedMediaList = this.instagramApi.userOperations().getRecentMedia(userId);
				Pagination pagination = pagedMediaList.getPagination();
				String nextMinId = pagination.getNextMinId();
				String nextMaxId = pagination.getNextMaxId();
				int pageCount = pagedMediaList.getList().size();
				
				List<SocialPhoto> socialPhotosPage = buildSocialPhotosPage(pagedMediaList);
				allSocialPhotos.addAll(socialPhotosPage);

				// Now, iterate through the remaining pages, now that we have (and can get) minId and maxId.
				while (nextMaxId != null) {
					
					pagedMediaList = this.instagramApi.userOperations().getRecentMedia(userId, MAX_PHOTOS_PER_PAGE, nextMaxId, nextMinId);
					pagination = pagedMediaList.getPagination();
					nextMinId = pagination.getNextMinId();
					nextMaxId = pagination.getNextMaxId();
					pageCount = pagedMediaList.getList().size();
					
					socialPhotosPage = buildSocialPhotosPage(pagedMediaList);
					allSocialPhotos.addAll(socialPhotosPage);
				}
															    
			    this.getSocialMediaQueryCache().putSocialPhotos(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(),
					allSocialPhotos);
			    			    
			    count = allSocialPhotos.size();
			} else {
				count = allSocialPhotos.size();
			}
						
			List<SocialPhoto> socialPhotos = new ArrayList<SocialPhoto>();
	    	for (int i=offset; i < offset+limit && i < count; i++) {
	    
	    		socialPhotos.add(allSocialPhotos.get(i));
		    }
		    socialPhotosResponse = new SocialPhotosResponse("", count, socialPhotos);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			socialPhotosResponse = new SocialPhotosResponse(e.getMessage(), 0, null);
		}
		
		return socialPhotosResponse;
	}
	
	/*
	 * 
	 * @param pagedMediaList
	 * @return
	 */
	private List<SocialPhoto> buildSocialPhotosPage(PagedMediaList pagedMediaList) {
		
		List<SocialPhoto> socialPhotosPage = new ArrayList<SocialPhoto>();
		
	    ArrayList<Media> instagramPhotos = new ArrayList<Media>();
	    instagramPhotos.addAll(pagedMediaList.getList());
	    
	    int pageSize = instagramPhotos.size();
	    for (int i=0; i < pageSize; i++) {
	    	
			Media instagramPhoto = instagramPhotos.get(i);
			
			Map<String,Image> images = instagramPhoto.getImages();
			
			Image image = images.get("standard_resolution");
			if (image == null) {
				image = images.get("low_resolution");
				if (image == null) {
					image = images.get("thumbnail");
				}
			}
			String sourceUrl = image.getUrl(); 
				
			Image thumbnailImage = images.get("thumbnail");
			if (thumbnailImage == null) {
				thumbnailImage = images.get("low_resolution");
				if (thumbnailImage == null) {
					thumbnailImage = images.get("standard_resolution");
				}
			}
			String thumbnailUrl = thumbnailImage.getUrl();
			
			String name = "";
			Caption caption = instagramPhoto.getCaption();
			if (caption != null) {
				name = caption.getText();
 			}
			
			SocialPhoto socialPhoto = new SocialPhoto(
				instagramPhoto.getId(),
				name,
				sourceUrl,
				thumbnailUrl,
				buildSocialMediaUrlChecksum(sourceUrl));
			
			socialPhotosPage.add(socialPhoto);
	    }
		
		return socialPhotosPage;
	}

	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.api.adapter.ISocialApiAdapter#getSocialFriends(java.lang.String, int, int)
	 */
	public SocialFriendsResponse getSocialFriends(
		String id,
		int offset, 
		int limit) {

		try {

			List<SocialProfile> socialFriends = new ArrayList<SocialProfile>();

			List<InstagramProfile> follows = this.instagramApi.userOperations().getFollows(id);
			List<InstagramProfile> followedBy = this.instagramApi.userOperations().getFollowedBy(id);
			
			List<InstagramProfile> friends = new ArrayList<InstagramProfile>();
			friends.addAll(follows);
			friends.addAll(followedBy);

			String email = "";
			String gender = "";
			
		    int count = friends.size();
		    if (offset >=0 && offset < count) {
		    	
		    	for (int i=offset; i < offset+limit && i < count; i++) {
		    
		    		InstagramProfile instagramProfile = friends.get(i);

		    		socialFriends.add(new SocialProfile(
	    				ISocialService.PROVIDER_NAME_INSTAGRAM,
	    				instagramProfile.getId(),
	    				instagramProfile.getUsername(),
	    				instagramProfile.getProfilePictureUrl(),
	    				email,
	    				instagramProfile.getFirstName(),
	    				instagramProfile.getLastName(),
	    				instagramProfile.getFullName(),
	    				gender));
		    	}
		    }
			
			return new SocialFriendsResponse("", count, socialFriends);			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SocialFriendsResponse(e.getMessage(), 0, null);
		}
	}	
}