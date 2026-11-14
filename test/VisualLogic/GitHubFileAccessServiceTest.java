package VisualLogic;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GitHubFileAccessServiceTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private HttpServer server;
    private int port;
    private GitHubFileAccessService service;
    private Path cacheDir;
    private Level previousLogLevel;

    @Before
    public void setUp() throws Exception {
        Logger serviceLog = Logger.getLogger(GitHubFileAccessService.class.getName());
        previousLogLevel = serviceLog.getLevel();
        serviceLog.setLevel(Level.OFF);
        cacheDir = folder.getRoot().toPath().resolve("cache").toAbsolutePath();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        port = server.getAddress().getPort();
        final byte[] png = pngBytes();
        server.createContext("/contents", exchange -> {
            String p = exchange.getRequestURI().getPath();
            if (p.contains("/Missing")) {
                respond(exchange, 404, new byte[0]);
            } else {
                String json = "[{\"name\":\"Generator\",\"type\":\"dir\"},{\"name\":\"icon32.png\",\"type\":\"file\"}]";
                respond(exchange, 200, json.getBytes(StandardCharsets.UTF_8));
            }
        });
        server.createContext("/raw", exchange -> {
            String p = exchange.getRequestURI().getPath();
            if (p.endsWith("/definition.def")) {
                respond(exchange, 200, "CAPTION = Generator\n".getBytes(StandardCharsets.UTF_8));
            } else if (p.endsWith("/icon32.png")) {
                respond(exchange, 200, png);
            } else if (p.endsWith("/cached.txt")) {
                respond(exchange, 200, "remote-value".getBytes(StandardCharsets.UTF_8));
            } else {
                respond(exchange, 404, new byte[0]);
            }
        });
        server.start();

        Path props = folder.newFile("test.properties").toPath();
        GitHubConfig config = new GitHubConfig(props.toString()) {
            @Override
            public String rawUrlFor(String logicalPath) {
                String p = logicalPath.startsWith("/") ? logicalPath : "/" + logicalPath;
                return "http://127.0.0.1:" + port + "/raw" + p;
            }

            @Override
            public String apiUrlFor(String logicalPath) {
                String p = logicalPath.startsWith("/") ? logicalPath : "/" + logicalPath;
                return "http://127.0.0.1:" + port + "/contents" + p + "?ref=test";
            }

            @Override
            public File cacheDir() {
                return GitHubFileAccessServiceTest.this.cacheDir.toFile();
            }
        };
        service = new GitHubFileAccessService(config);
    }

    @After
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
        Logger.getLogger(GitHubFileAccessService.class.getName()).setLevel(previousLogLevel);
    }

    private static byte[] pngBytes() throws IOException {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        return out.toByteArray();
    }

    private static void respond(HttpExchange exchange, int code, byte[] body) throws IOException {
        exchange.sendResponseHeaders(code, body.length == 0 ? -1 : body.length);
        if (body.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        }
        exchange.close();
    }

    @Test
    public void listFilesParsesNamesFromContentsApi() {
        String[] names = service.listFiles("/CircuitElements/Analoge");
        assertArrayEquals(new String[]{"Generator", "icon32.png"}, names);
    }

    @Test
    public void listFilesReturnsEmptyOnHttpError() {
        assertEquals(0, service.listFiles("/Missing").length);
    }

    @Test
    public void readTextFetchesAndCaches() {
        String first = service.readText("/CircuitElements/Analoge/Generator/definition.def");
        assertEquals("CAPTION = Generator\n", first);
        server.stop(0);
        server = null;
        String second = service.readText("/CircuitElements/Analoge/Generator/definition.def");
        assertEquals("CAPTION = Generator\n", second);
    }

    @Test
    public void readBytesReturnsNullForMissing() {
        assertNull(service.readBytes("/CircuitElements/Analoge/Generator/missing.bin"));
    }

    @Test
    public void existsFalseForMissing() {
        assertFalse(service.exists("/CircuitElements/Analoge/Generator/missing.bin"));
    }

    @Test
    public void existsTrueForCachedFile() {
        service.readText("/CircuitElements/Analoge/Generator/definition.def");
        assertTrue(service.exists("/CircuitElements/Analoge/Generator/definition.def"));
    }

    @Test
    public void readImageReturnsBufferedImage() {
        BufferedImage image = service.readImage("/CircuitElements/Analoge/Generator/icon32.png");
        assertNotNull(image);
        assertEquals(2, image.getWidth());
    }

    @Test
    public void corruptCacheIsRefetched() throws Exception {
        Path cached = cacheDir.resolve("CircuitElements/Analoge/Generator/cached.txt");
        Files.createDirectories(cached);
        String value = service.readText("/CircuitElements/Analoge/Generator/cached.txt");
        assertEquals("remote-value", value);
        assertTrue(Files.isRegularFile(cached));
    }
}
