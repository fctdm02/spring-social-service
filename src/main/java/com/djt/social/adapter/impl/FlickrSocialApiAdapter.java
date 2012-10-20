package com.djt.social.adapter.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.social.flickr.api.Flickr;
import org.springframework.social.flickr.api.Person;
import org.springframework.social.flickr.api.Photo;
import org.springframework.social.flickr.api.Photos;

import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 */
public final class FlickrSocialApiAdapter extends AbstractSocialApiAdapter {

	/* */
	private Logger logger = Logger.getLogger(FlickrSocialApiAdapter.class);
	
	/* */
	private Flickr flickrApi;

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
	public FlickrSocialApiAdapter(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		ISocialMediaQueryCache socialMediaQueryCache,
		String socialUrlSalt,
		Flickr flickrApi) {
		
		super(
			promotionDeployPath, 
			providerId, 
			accessToken, 
			socialMediaQueryCache,
			socialUrlSalt);

		this.flickrApi = flickrApi;
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

				Person flickrProfile = this.flickrApi.peopleOperations().getPersonProfile();

				String id = flickrProfile.getId();
				String email = "";
				String firstName = "";
				String lastName = "";
				String gender = "";
				
				// TODO: TDM: Investigate.
				String profilePhotoUrl = "http://farm9.staticflickr.com/8146/buddyicons/" + id + ".jpg";
				
				socialProfile = new SocialProfile(
					ISocialService.PROVIDER_NAME_FLICKR,
					flickrProfile.getId(),
					flickrProfile.getUserName(),
					profilePhotoUrl,
					email,
					firstName,
					lastName,
					flickrProfile.getRealName(),
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
	 * @see com.djt.social.service.impl.adapter.AbstractSocialAdapter#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, int, int)
	 */
	@SuppressWarnings("unused")
	public SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		String id,
		int offset, 
		int limit) {
		
		SocialPhotosResponse socialPhotosResponse = null;
		
		try {
			
			SocialProfileResponse socialProfileResponse = this.getSocialProfile();
			SocialProfile socialProfile = socialProfileResponse.getSocialProfile();
			String userId = socialProfile.getId();
									
			List<SocialPhoto> allSocialPhotos = this.getSocialMediaQueryCache().getSocialPhotos(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken());

			int count = 0;
			if (allSocialPhotos == null) {
				
				allSocialPhotos = new ArrayList<SocialPhoto>();
				
				// Iterate through all the photos.
				int page = 1;
				Photos photosObject = this.flickrApi.peopleOperations().getPhotos(userId, MAX_PHOTOS_PER_PAGE, page);
				ArrayList<Photo> photoArray = photosObject.getPhoto();
			    int pageCount = photoArray.size();
			    while (pageCount > 0) {
				    for (int i=0; i < pageCount; i++) {
					    
			    		Photo photo = photoArray.get(i);

			    		String link = photo.getUrlO();
			    		if (link == null || link.trim().isEmpty()) {
			    			link = photo.getUrlL();
			    			if (link == null || link.trim().isEmpty()) {
			    				link = photo.getUrl(); 
			    			}
			    		}
			    		
			    		String thumbnail = photo.getThumb();
			    		if (thumbnail == null || thumbnail.trim().isEmpty()) {
			    			thumbnail = photo.getUrlS();
			    			if (thumbnail == null || thumbnail.trim().isEmpty()) {
			    				thumbnail = photo.getUrl();
			    			}
			    		}
			    		
			    		String name = photo.getDescription();
			    		if (name == null || name.trim().isEmpty()) {
			    			name = photo.getTitle();
			    		}
			    				    		
			    		allSocialPhotos.add(
			    			new SocialPhoto(
			    				photo.getId(), 
			    				name, 
			    				link, 
			    				thumbnail,
			    				buildSocialMediaUrlChecksum(link)));
				    }
				    page = page + 1;
				    photosObject = this.flickrApi.peopleOperations().getPhotos(userId, MAX_PHOTOS_PER_PAGE, page);
				    photoArray = photosObject.getPhoto();
				    pageCount = photoArray.size();
			    }

			    this.getSocialMediaQueryCache().putSocialPhotos(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(),
					allSocialPhotos);
			}
			count = allSocialPhotos.size();
			
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
}