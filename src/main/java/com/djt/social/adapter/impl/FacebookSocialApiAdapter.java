package com.djt.social.adapter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.social.facebook.api.Album;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Photo;
import org.springframework.social.facebook.api.Photo.Image;
import org.springframework.social.facebook.api.Video;

import com.djt.social.cache.ISocialMediaQueryCache;
import com.djt.social.model.SocialAlbum;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideo;
import com.djt.social.model.SocialVideosResponse;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 */
public final class FacebookSocialApiAdapter extends AbstractSocialApiAdapter {

	/* */
	private Logger logger = Logger.getLogger(FacebookSocialApiAdapter.class);
	
	/* */
	private Facebook facebookApi;
	
	/**
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param ISocialMediaQueryCache
	 * @param socialUrlSalt
	 * @param facebookApi
	 */
	public FacebookSocialApiAdapter(
		String promotionDeployPath,
		String providerId,
		String accessToken,
		ISocialMediaQueryCache socialMediaQueryCache,
		String socialUrlSalt,
		Facebook facebookApi) {
		
		super(
			promotionDeployPath, 
			providerId, 
			accessToken, 
			socialMediaQueryCache,
			socialUrlSalt);
		
		this.facebookApi = facebookApi;
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

				FacebookProfile facebookProfile = this.facebookApi.userOperations().getUserProfile();

				socialProfile = new SocialProfile(
					ISocialService.PROVIDER_NAME_FACEBOOK,	
					facebookProfile.getId(),
					facebookProfile.getUsername(),
					facebookProfile.getLink(),
					facebookProfile.getEmail(),
					facebookProfile.getFirstName(),
					facebookProfile.getLastName(),
					facebookProfile.getName(),
					facebookProfile.getGender());
				
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
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotoAlbums()
	 */
	public SocialAlbumsResponse getSocialPhotoAlbums() {

		SocialAlbumsResponse socialAlbumsResponse = null;
		
		try {
			
			List<SocialAlbum> socialPhotoAlbums = this.getSocialMediaQueryCache().getSocialPhotoAlbums(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken());
			
			if (socialPhotoAlbums != null) {
				socialAlbumsResponse = new SocialAlbumsResponse("", socialPhotoAlbums.size(), socialPhotoAlbums);
			} else {
				List<Album> albums = this.facebookApi.mediaOperations().getAlbums();
				socialAlbumsResponse = buildSocialPhotoAlbums(albums.size(), albums);
				this.getSocialMediaQueryCache().putSocialPhotoAlbums(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(),
					socialAlbumsResponse.getSocialAlbums());
			}
						
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			socialAlbumsResponse = new SocialAlbumsResponse(e.getMessage(), 0, null);
		}
		
		return socialAlbumsResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotoAlbums(int, int)
	 */
	public SocialAlbumsResponse getSocialPhotoAlbums(
		int offset, 
		int limit) {

		SocialAlbumsResponse socialAlbumsResponse = null;
		
		try {
			
			List<SocialAlbum> socialPhotoAlbums = this.getSocialMediaQueryCache().getSocialPhotoAlbums(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken());
			
			if (socialPhotoAlbums == null) {
				List<Album> albums = this.facebookApi.mediaOperations().getAlbums();
				socialAlbumsResponse = buildSocialPhotoAlbums(albums.size(), albums);
				socialPhotoAlbums = socialAlbumsResponse.getSocialAlbums();
				
				this.getSocialMediaQueryCache().putSocialPhotoAlbums(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(),
					socialPhotoAlbums);
			}
			
			int count = socialPhotoAlbums.size();
			List<SocialAlbum> albumPage = new ArrayList<SocialAlbum>();
			
		    if (offset >=0 && offset < count) {
		    	
		    	for (int i=offset; i < offset+limit && i < count; i++) {
				
		    		SocialAlbum album = socialPhotoAlbums.get(i);
					albumPage.add(album);
			    }
		    } else {
		    	albumPage = socialPhotoAlbums;
		    }
		    
		    socialAlbumsResponse = new SocialAlbumsResponse("", count, albumPage);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			socialAlbumsResponse = new SocialAlbumsResponse(e.getMessage(), 0, null);
		}
		
		return socialAlbumsResponse;
	}

	/*
	 * @param count
	 * @param facebookAlbums
	 * @return
	 */
	private SocialAlbumsResponse buildSocialPhotoAlbums(
		int count,
		List<Album> facebookAlbums) {

		List<SocialAlbum> socialAlbums = new ArrayList<SocialAlbum>();
		
		Iterator<Album> facebookAlbumIterator = facebookAlbums.iterator();
		while (facebookAlbumIterator.hasNext()) {
		
			Album facebookAlbum = facebookAlbumIterator.next();
			
			SocialAlbum socialAlbum = new SocialAlbum(
				facebookAlbum.getId(),
				facebookAlbum.getName(),
				facebookAlbum.getLink());
			
			socialAlbums.add(socialAlbum);
		}
				
		return new SocialAlbumsResponse("", count, socialAlbums);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialPhotosFromSocialPhotoAlbum(java.lang.String, int, int)
	 */
	public SocialPhotosResponse getSocialPhotosFromSocialPhotoAlbum(
		String id, 
		int offset, 
		int limit) {
		
		SocialPhotosResponse socialPhotosResponse = null;
		
		try {
			
			List<SocialPhoto> socialPhotos = this.getSocialMediaQueryCache().getSocialPhotosFromSocialPhotoAlbum(
				this.getPromotionDeployPath(), 
				this.getProviderId(), 
				this.getAccessToken(), 
				id, 
				offset, 
				limit);
			
			if (socialPhotos != null) {
				socialPhotosResponse = new SocialPhotosResponse("", -1, socialPhotos);
			} else {
				socialPhotos = new ArrayList<SocialPhoto>();
				
				Album album = this.facebookApi.mediaOperations().getAlbum(id);
				int photoCount = album.getCount();
				
				List<Photo> facebookPhotos = this.facebookApi.mediaOperations().getPhotos(id, offset, limit);
				Iterator<Photo> facebookPhotoIterator = facebookPhotos.iterator();
				while (facebookPhotoIterator.hasNext()) {
				
					Photo facebookPhoto = facebookPhotoIterator.next();
					
					Image image = facebookPhoto.getOversizedImage();
					if (image == null) {
						image = facebookPhoto.getSourceImage();
						if (image == null) {
							image = facebookPhoto.getAlbumImage();
						}
					}
					String sourceUrl = image.getSource(); 
						
					Image thumbnailImage = facebookPhoto.getSmallImage();
					if (thumbnailImage == null) {
						thumbnailImage = facebookPhoto.getSourceImage();
						if (thumbnailImage == null) {
							thumbnailImage = facebookPhoto.getAlbumImage();
						}
					}
					String thumbnailUrl = thumbnailImage.getSource();
					
					SocialPhoto socialPhoto = new SocialPhoto(
						facebookPhoto.getId(),
						facebookPhoto.getName(),
						sourceUrl,
						thumbnailUrl,
						buildSocialMediaUrlChecksum(sourceUrl));
					
					socialPhotos.add(socialPhoto);
				}
				
				this.getSocialMediaQueryCache().putSocialPhotosFromSocialPhotoAlbum(
					this.getPromotionDeployPath(), 
					this.getProviderId(), 
					this.getAccessToken(), 
					id, 
					offset, 
					limit,
					socialPhotos);				
				socialPhotosResponse = new SocialPhotosResponse("", photoCount, socialPhotos);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			socialPhotosResponse = new SocialPhotosResponse(e.getMessage(), 0, null);
		}
		
		return socialPhotosResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.djt.social.service.ISocialApiAdapter#getSocialVideosFromSocialVideoAlbum(java.lang.String, int, int)
	 */
	@SuppressWarnings("unused")
	public SocialVideosResponse getSocialVideosFromSocialVideoAlbum(
		String id, 
		int offset, 
		int limit) {
		
		try {

			List<SocialVideo> socialVideos = new ArrayList<SocialVideo>();
			
			List<Video> facebookVideos = this.facebookApi.mediaOperations().getVideos(offset, limit);
			Iterator<Video> facebookVideoIterator = facebookVideos.iterator();
			while (facebookVideoIterator.hasNext()) {
			
				Video facebookVideo = facebookVideoIterator.next();
				
				SocialVideo socialVideo = new SocialVideo(
					facebookVideo.getId(),
					facebookVideo.getName(),
					facebookVideo.getSource(),
					facebookVideo.getPicture(),
					buildSocialMediaUrlChecksum(facebookVideo.getSource()));
				
				socialVideos.add(socialVideo);
			}
			
			return new SocialVideosResponse("", -1, socialVideos);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SocialVideosResponse(e.getMessage(), 0, null);
		}
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
			
			List<String> friendIdsList = this.facebookApi.friendOperations().getFriendIds();
			int friendCount = friendIdsList.size();
			
			List<FacebookProfile> facebookFriends = this.facebookApi.friendOperations().getFriendProfiles(offset, limit);
			Iterator<FacebookProfile> facebookFriendIterator = facebookFriends.iterator();
			while (facebookFriendIterator.hasNext()) {
				
				FacebookProfile facebookProfile = facebookFriendIterator.next();

				socialFriends.add(new SocialProfile(
					ISocialService.PROVIDER_NAME_FACEBOOK,	
					facebookProfile.getId(),
					facebookProfile.getUsername(),
					facebookProfile.getLink(),
					facebookProfile.getEmail(),
					facebookProfile.getFirstName(),
					facebookProfile.getLastName(),
					facebookProfile.getName(),
					facebookProfile.getGender()));
			}
			
			return new SocialFriendsResponse("", friendCount, socialFriends);
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new SocialFriendsResponse(e.getMessage(), 0, null);
		}
	}	
}