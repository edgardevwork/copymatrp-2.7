package ru.edgar.nlremake.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Server {

	@SerializedName("id") // Update ->  26.09/2026 by EDGAR 3.0 / https://github.com/edgardevwork
	@Expose
	private int id;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("color")
	@Expose
	private String color;

	@SerializedName("ip")
	@Expose
	private String ip;

	@SerializedName("port")
	@Expose
	private int port;

	@SerializedName("isRecommended")
	@Expose
	private boolean isRecommended;

	@SerializedName("load")
	@Expose
	private int load;

	@SerializedName("isTest")
	@Expose
	private boolean isTest;

	@SerializedName("enterLock")
	@Expose
	private boolean enterLock;

	@SerializedName("personId")
	@Expose
	private int personId;

	@SerializedName("personName")
	@Expose
	private String personName;

	// Update ->  26.09/2026 by EDGAR 3.0 / https://github.com/edgardevwork
	public Server(int id, String name, String color, String ip, int port,
	              boolean isRecommended, int load, boolean isTest, boolean enterLock, int personId, String personName) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.ip = ip;
		this.port = port;
		this.isRecommended = isRecommended;
		this.load = load;
		this.isTest = isTest;
		this.enterLock = enterLock;
		this.personId = personId;
		this.personName = personName;
	}

	public int getId(){
		return id;
	}

	public String getName() {
		return name;
	}

	public String getColor() {
		return color;
	}

	public String getIp() {
		return ip;
	}

	public int getPort(){
		return port;
	}

	public boolean isRecommended(){
		return isRecommended;
	}

	public int getLoad() {
		return load;
	}

	public boolean isTest() {
		return isTest;
	}

	public boolean isEnterLock() {
		return enterLock;
	}

	public int getPersonId() {
		return personId;
	}

	public String getPersonName() {
		return personName;
	}
}