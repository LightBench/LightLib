package com.frahhs.lightlib;

import java.io.File;

public class LightOptions {
    private String permissionPrefix = "light";
    private String spigotMarketID;
    private boolean updateCheck = false;

    private String githubContentsUrl;
    private String githubUrlTemplate;
    private Integer bstatsID = null;

    public void setPermissionPrefix(String permissionPrefix) {
        this.permissionPrefix = permissionPrefix;
    }

    public void setSpigotMarketID(String spigotMarketID) {
        this.spigotMarketID = spigotMarketID;
    }

    public void setUpdateCheck(boolean updateCheck) {
        this.updateCheck = updateCheck;
    }

    public void setGithubContentsUrl(String githubContentsUrl) {
        this.githubContentsUrl = githubContentsUrl;
    }

    public void setGithubUrlTemplate(String githubUrlTemplate) {
        this.githubUrlTemplate = githubUrlTemplate;
    }

    public void setBstatsID(int bstatsID) {
        this.bstatsID = bstatsID;
    }

    public String getPermissionPrefix() {
        return permissionPrefix;
    }

    public String getSpigotMarketID() {
        return spigotMarketID;
    }

    public boolean getUpdateCheck() {
        return updateCheck;
    }

    public String getGithubContentsUrl() {
        return githubContentsUrl;
    }

    public String getGithubUrlTemplate() {
        return githubUrlTemplate;
    }

    public Integer getBstatsID() {
        return bstatsID;
    }
}
