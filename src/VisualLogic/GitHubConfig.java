package VisualLogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GitHubConfig {
    public static final String DEFAULT_OWNER = "sPyOpenSource";
    public static final String DEFAULT_REPO = "myopenlab";
    public static final String DEFAULT_BRANCH = "master";
    public static final String DEFAULT_BASE_PATH = "distribution/Elements";
    public static final String DEFAULT_CACHE_DIR = ".cache/remote-elements";

    public String owner;
    public String repo;
    public String branch;
    public String basePath;
    public String cacheDir;

    public GitHubConfig() {
        this("myopenlab.properties");
    }

    public GitHubConfig(String propertiesFileName) {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(propertiesFileName)) {
            props.load(in);
        } catch (IOException | RuntimeException ex) {
            Logger.getLogger(GitHubConfig.class.getName()).log(Level.WARNING,
                "Cannot load " + propertiesFileName + ", using defaults: " + ex.getMessage());
        }
        this.owner = orDefault(props.getProperty("elements.remote.owner"), DEFAULT_OWNER);
        this.repo = orDefault(props.getProperty("elements.remote.repo"), DEFAULT_REPO);
        this.branch = orDefault(props.getProperty("elements.remote.branch"), DEFAULT_BRANCH);
        this.basePath = orDefault(props.getProperty("elements.remote.basePath"), DEFAULT_BASE_PATH);
        this.cacheDir = orDefault(props.getProperty("elements.remote.cacheDir"), DEFAULT_CACHE_DIR);
        this.basePath = stripSlashes(this.basePath);
    }

    private static String orDefault(String value, String fallback) {
        return (value == null || value.trim().isEmpty()) ? fallback : value.trim();
    }

    private static String stripSlashes(String s) {
        if (s == null) {
            return "";
        }
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public String rawBaseUrl() {
        return "https://raw.githubusercontent.com/" + owner + "/" + repo + "/" + branch + "/" + basePath;
    }

    /**
     * Ref-less Contents API base for the configured repository and base path.
     * Use {@link #apiUrlFor(String)} to pin a listing to the configured branch.
     */
    public String apiBaseUrl() {
        return "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + basePath;
    }

    /** Full raw download URL for a logical element path such as "/CircuitElements/Analoge". */
    public String rawUrlFor(String logicalPath) {
        String p = (logicalPath == null) ? "" : logicalPath;
        if (!p.isEmpty() && !p.startsWith("/")) {
            p = "/" + p;
        }
        return rawBaseUrl() + p;
    }

    /** Contents API URL for a logical element directory, pinned to the configured branch via ?ref=. */
    public String apiUrlFor(String logicalPath) {
        String p = (logicalPath == null) ? "" : logicalPath;
        if (!p.isEmpty() && !p.startsWith("/")) {
            p = "/" + p;
        }
        String ref;
        try {
            ref = java.net.URLEncoder.encode(branch, "UTF-8");
        } catch (java.io.UnsupportedEncodingException ex) {
            ref = branch;
        }
        return apiBaseUrl() + p + "?ref=" + ref;
    }

    public File cacheDir() {
        return new File(cacheDir);
    }
}
