package VisualLogic;

import java.awt.image.BufferedImage;

public interface FileAccessService {
    String[] listFiles(String dir);
    String readText(String path);
    byte[] readBytes(String path);
    BufferedImage readImage(String path);
    boolean exists(String path);
}
