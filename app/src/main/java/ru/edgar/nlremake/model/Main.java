package ru.edgar.nlremake.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgardevwork
 * created at 04.01.2024
 */
public class Main {
    @SerializedName("servers")
    @Expose
    private String servers; // update at 06.01.24

    @SerializedName("stories")
    @Expose
    private String stories; // update at 06.01.24

    @SerializedName("verifyAuth")
    @Expose
    private String verifyAuth; // update at 06.01.24

    @SerializedName("resetPassword")
    @Expose
    private String resetPassword; // update at 24.08.26

    @SerializedName("character")
    @Expose
    private String character; // update at 24.08.26

    @SerializedName("accountDetails")
    @Expose
    private String accountDetails; // update at 06.01.24

    @SerializedName("isAcc")
    @Expose
    private String isAcc; // update at 06.01.24

    @SerializedName("skinsCDN")
    @Expose
    private String skinsCDN; // update at 09.01.24

    @SerializedName("crashReport")
    @Expose
    private String crashReport; // update at 25.08.26

    @SerializedName("deleteAcc")
    @Expose
    private String deleteAcc; // update at 24.08.26

    public Main(
            String servers,
            String stories,
            String verifyAuth,
            String resetPassword,
            String character,
            String accountDetails,
            String isAcc,
            String skinsCDN,
            String crashReport,
            String deleteAcc
    ) {
        this.servers = servers;
        this.stories = stories;
        this.verifyAuth = verifyAuth;
        this.resetPassword = resetPassword;
        this.character = character;
        this.accountDetails = accountDetails;
        this.isAcc = isAcc;
        this.skinsCDN = skinsCDN;
        this.crashReport = crashReport;
        this.deleteAcc = deleteAcc;
    }

    public String getServers() {
        return servers;
    }

    public String getStories() {
        return stories;
    }

    public String getVerifyAuth() {
        return verifyAuth;
    }

    public String getResetPassword() {
        return resetPassword;
    }

    public String getCharacter() {
        return character;
    }

    public String getAccountDetails() {
        return accountDetails;
    }

    public String getIsAcc() {
        return isAcc;
    }

    public String getSkinsCDN() {
        return skinsCDN;
    }

    public String getCrashReport() {
        return crashReport;
    }

    public String getDeleteAcc() {
        return deleteAcc;
    }
}