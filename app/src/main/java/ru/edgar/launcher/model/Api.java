package ru.edgar.launcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Api {

    @SerializedName("launcher_version")
    @Expose
    private Integer launcher_version;

    @SerializedName("launcher_url") // update at 20.03.24
    @Expose
    private String launcher_url;

    @SerializedName("launcher_path") // update at 20.03.24
    @Expose
    private String launcher_path;

    @SerializedName("launcher_name") // update at 20.03.24
    @Expose
    private String launcher_name;

    @SerializedName("isTest")
    @Expose
    private boolean isTest;

    @SerializedName("test_api")
    @Expose
    private boolean test_api;

    @SerializedName("api")
    @Expose
    private String api;

    @SerializedName("archives") // update at 19.03.24
    private List<Archive> archives;

    @SerializedName("deleted") // update at 26.03.24
    private List<Deleted> deleted;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgardevwork
    // created at 04.01.2024

    public Api(Integer launcher_version, String launcher_url, String launcher_path, String launcher_name, boolean isTest, boolean test_api, String api, List<Archive> archives, List<Deleted> deleted) {
        this.launcher_version = launcher_version;
        this.launcher_url = launcher_url;
        this.launcher_path = launcher_path;
        this.launcher_name = launcher_name;
        this.isTest = isTest;
        this.test_api = test_api;
        this.api = api;
        this.archives = archives;
        this.deleted = deleted;

    }

    public Integer getLauncherVersion() {
        return launcher_version;
    }

    public String getLauncherUrl() {
        return launcher_url;
    }

    public String getLauncherPath() {
        return launcher_path;
    }

    public String getLauncherName() {
        return launcher_name;
    }

    public boolean getIsTest() {
        return isTest;
    }

    public boolean getTestApi() {
        return test_api;
    }

    public String getApi() {
        return api;
    }

    public List<Archive> getArchives() {
        return this.archives;
    }

    public List<Deleted> getDeleted() {
        return deleted;
    }

}
