package com.terraboxstudios.instantreplay.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.net.URL;
import java.net.URLConnection;

@RequiredArgsConstructor
public class PluginVersionChecker {

    private final String currentVersion;
    private final String githubUrl;
    @Getter
    private String latestReleaseUrl;

    public boolean shouldUpdate() {
        try {
            URL url = new URL(githubUrl + (githubUrl.endsWith("/") ? "" : "/") + "releases/latest");
            URLConnection conn = url.openConnection();
            String redirectUrl = conn.getHeaderField("location");
            this.latestReleaseUrl = redirectUrl;
            String[] redirectUrlElements = redirectUrl.split("/");
            String latestVersion = redirectUrlElements[redirectUrlElements.length - 1];
            return !currentVersion.equals("v" + latestVersion);
        } catch (Exception e) {
            return false;
        }
    }

}
