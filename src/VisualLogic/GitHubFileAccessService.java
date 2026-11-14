package VisualLogic;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class GitHubFileAccessService implements FileAccessService {
    private static final Logger LOG = Logger.getLogger(GitHubFileAccessService.class.getName());
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
                try { Files.delete(cache); } catch (IOException ignore) {}
            }
        }
        String rawUrl = config.rawUrlFor(path);
        try {
            byte[] data = downloadBytes(rawUrl);
            if (data != null) {
                writeCache(cache, data);
            }
            return data;
        } catch (IOException ex) {
            LOG.log(Level.WARNING, "Failed to download " + rawUrl, ex);
            return null;
        }
    }

    @Override
    public BufferedImage readImage(String path) {
        byte[] data = readBytes(path);
        if (data == null) {
            return null;
        }
        try {
            return ImageIO.read(new ByteArrayInputStream(data));
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
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(rawUrl).openConnection();
            con.setRequestMethod("HEAD");
            con.setRequestProperty("User-Agent", "MyOpenLab");
            int code = con.getResponseCode();
            return code == HttpURLConnection.HTTP_OK;
        } catch (IOException ex) {
            return false;
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

    private byte[] downloadBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(30000);
        con.setRequestProperty("User-Agent", "MyOpenLab");
        int code = con.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
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
    }

    private Path cacheFileFor(String path) {
        String rel = path.startsWith("/") ? path.substring(1) : path;
        return config.cacheDir().toPath().resolve(rel);
    }

    private void writeCache(Path cache, byte[] data) throws IOException {
        Files.createDirectories(cache.getParent());
        Files.write(cache, data);
    }
}
