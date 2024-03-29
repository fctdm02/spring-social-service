package org.springframework.social.flickr.api;

import java.util.LinkedHashMap;

public class Person {
	
	String id;
	String nsid;
	String ispro;
	String iconServer;
	String iconFarm;
	String pathAlias;
	String userName;
	String realName;
	String location;
	//timezone pending
	String description;
	String photosurl;
	String profileurl;
	String mobileurl;
	PhotosInfo photos;
	

	public String getUserName() {
		return userName;
	}
	public void setUserName(LinkedHashMap<String, String> userName) {
		this.userName = userName.get(FlickrConstant.JSONCONTENT);
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(LinkedHashMap<String, String> realName) {
		this.realName = realName.get(FlickrConstant.JSONCONTENT);
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(LinkedHashMap<String, String> location) {
		this.location = location.get(FlickrConstant.JSONCONTENT);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(LinkedHashMap<String, String> description) {
		this.description = description.get(FlickrConstant.JSONCONTENT);
	}
	public String getPhotosurl() {
		return photosurl;
	}
	public void setPhotosurl(LinkedHashMap<String, String> photosurl) {
		this.photosurl = photosurl.get(FlickrConstant.JSONCONTENT);
	}
	public String getProfileurl() {
		return profileurl;
	}
	public void setProfileurl(LinkedHashMap<String, String> profileurl) {
		this.profileurl = profileurl.get(FlickrConstant.JSONCONTENT);
	}
	public String getMobileurl() {
		return mobileurl;
	}
	public void setMobileurl(LinkedHashMap<String, String> mobileurl) {
		this.mobileurl = mobileurl.get(FlickrConstant.JSONCONTENT);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNsid() {
		return nsid;
	}
	public void setNsid(String nsid) {
		this.nsid = nsid;
	}
	public String getIspro() {
		return ispro;
	}
	public void setIspro(String ispro) {
		this.ispro = ispro;
	}
	public String getIconServer() {
		return iconServer;
	}
	public void setIconServer(String iconServer) {
		this.iconServer = iconServer;
	}
	public String getIconFarm() {
		return iconFarm;
	}
	public void setIconFarm(String iconFarm) {
		this.iconFarm = iconFarm;
	}
	public String getPathAlias() {
		return pathAlias;
	}
	public void setPathAlias(String pathAlias) {
		this.pathAlias = pathAlias;
	}
	public PhotosInfo getPhotos() {
		return photos;
	}
	public void setPhotos(PhotosInfo photos) {
		this.photos = photos;
	}
	
	
}
