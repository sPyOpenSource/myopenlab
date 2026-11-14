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
        } catch (IOException ex) {
            Logger.getLogger(GitHubConfig.class.getName()).log(Level.INFO,
                "No " + propertiesFileName + ", using defaults: " + ex.getMessage());
        }
        this.owner = props.getProperty("elements.remote.owner", DEFAULT_OWNER);
        this.repo = props.getProperty("elements.remote.repo", DEFAULT_REPO);
        this.branch = props.getProperty("elements.remote.branch", DEFAULT_BRANCH);
        this.basePath = props.getProperty("elements.remote.basePath", DEFAULT_BASE_PATH);
        this.cacheDir = props.getProperty("elements.remote.cacheDir", DEFAULT_CACHE_DIR);
    }

    public String rawBaseUrl() {
        return "https://raw.githubusercontent.com/" + owner + "/" + repo + "/" + branch + "/" + basePath;
    }

    public String apiBaseUrl() {
        return "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + basePath;
    }

    public File cacheDir() {
        return new File(cacheDir);
    }
}
