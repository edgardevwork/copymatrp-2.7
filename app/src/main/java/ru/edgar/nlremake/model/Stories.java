package ru.edgar.nlremake.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stories {

	@SerializedName("imageurl")
	@Expose
	private String imageurl;

	@SerializedName("miniDate")
	@Expose
	private String miniDate;

	public Stories(String imageurl, String miniDate) {
		this.imageurl = imageurl;
		this.miniDate = miniDate;
	}

	public String getImageUrl() {
		return imageurl;
	}

	public String getMiniDate() {
		return miniDate;
	}

}