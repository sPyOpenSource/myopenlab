package VisualLogic;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class GitHubFileAccessService implements FileAccessService {
    private static final Logger LOG = Logger.getLogger(GitHubFileAccessService.class.getName());
    private static final String USER_AGENT = "MyOpenLab";
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final int READ_TIMEOUT_MS = 30000;

    private final GitHubConfig config;

    public GitHubFileAccessService(GitHubConfig config) {
        this.config = config;
    }

    @Override
    public String[] listFiles(String dir) {
        String apiUrl = config.apiUrlFor(dir);
        try {
            String json = getText(apiUrl);
            if (json == null || json.isEmpty()) {
                return new String[0];
            }
            return parseNamesFromContentsApi(json);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Failed to list " + dir, ex);
            return new String[0];
        }
    }

    @Override
    public String readText(String path) {
        byte[] bytes = readBytes(path);
        return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] readBytes(String path) {
        Path cache = cacheFileFor(path);
        if (Files.exists(cache)) {
            try {
                return Files.readAllBytes(cache);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Corrupt cache " + cache + ", re-fetching", ex);
                try {
                    Files.delete(cache);
                } catch (IOException ignore) {
                }
            }
        }
        String rawUrl = config.rawUrlFor(path);
        byte[] data;
        try {
            data = downloadBytes(rawUrl);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Failed to download " + rawUrl, ex);
            return null;
        }
        if (data != null) {
            try {
                writeCache(cache, data);
            } catch (IOException ex) {
                LOG.log(Level.WARNING, "Failed to cache " + cache, ex);
            }
        }
        return data;
    }

    @Override
    public BufferedImage readImage(String path) {
        byte[] data = readBytes(path);
        if (data == null) {
            return null;
        }
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            if (image == null) {
                LOG.warning("Not a readable image: " + path);
            }
            return image;
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Failed to read image " + path, ex);
            return null;
        }
    }

    @Override
    public boolean exists(String path) {
        Path cache = cacheFileFor(path);
        if (Files.exists(cache)) {
            return true;
        }
        String rawUrl = config.rawUrlFor(path);
        HttpURLConnection con = null;
        try {
            con = open(rawUrl, "HEAD");
            return con.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException ex) {
            return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private String[] parseNamesFromContentsApi(String json) {
        ArrayList<String> names = new ArrayList<>();
        int idx = 0;
        while (true) {
            int keyIdx = json.indexOf("\"name\"", idx);
            if (keyIdx < 0) {
                break;
            }
            int colon = json.indexOf(':', keyIdx);
            int quoteStart = json.indexOf('"', colon + 1);
            int quoteEnd = json.indexOf('"', quoteStart + 1);
            if (quoteStart < 0 || quoteEnd < 0) {
                break;
            }
            names.add(json.substring(quoteStart + 1, quoteEnd));
            idx = quoteEnd + 1;
        }
        return names.toArray(new String[0]);
    }

    private String getText(String urlString) throws IOException {
        byte[] data = downloadBytes(urlString);
        return data == null ? null : new String(data, StandardCharsets.UTF_8);
    }

    private HttpURLConnection open(String urlString, String method) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
        con.setRequestMethod(method);
        con.setConnectTimeout(CONNECT_TIMEOUT_MS);
        con.setReadTimeout(READ_TIMEOUT_MS);
        con.setRequestProperty("User-Agent", USER_AGENT);
        return con;
    }

    private byte[] downloadBytes(String urlString) throws IOException {
        HttpURLConnection con = open(urlString, "GET");
        try {
            int code = con.getResponseCode();
            if (code != HttpURLConnection.HTTP_OK) {
                LOG.warning("HTTP " + code + " for " + urlString);
                return null;
            }
            try (InputStream in = con.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int read;
                while ((read = in.read(buf)) > 0) {
                    out.write(buf, 0, read);
                }
                return out.toByteArray();
            }
        } finally {
            con.disconnect();
        }
    }

    private Path cacheFileFor(String path) {
        String rel = path == null ? "" : path;
        while (rel.startsWith("/")) {
            rel = rel.substring(1);
        }
        Path root = config.cacheDir().toPath().toAbsolutePath().normalize();
        Path out = root.resolve(rel).normalize();
        if (!out.startsWith(root)) {
            out = root.resolve("__invalid__");
        }
        return out;
    }

    private void writeCache(Path cache, byte[] data) throws IOException {
        Files.createDirectories(cache.getParent());
        Files.write(cache, data);
    }
}
