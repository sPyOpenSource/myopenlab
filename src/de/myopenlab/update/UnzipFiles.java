package de.myopenlab.update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipFiles {

    private static final int BUFFER_SIZE = 4096;

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        String destCanonical = destDir.getCanonicalPath();
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        try {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                String entryName = entry.getName();
                File entryFile = new File(destDir, entryName);
                String entryCanonical = entryFile.getCanonicalPath();
                if (!entryCanonical.equals(destCanonical)
                        && !entryCanonical.startsWith(destCanonical + File.separator)) {
                    throw new IOException("Blocked zip entry outside destination directory: " + entryName);
                }
                if (!entry.isDirectory()) {
                    entryFile.getParentFile().mkdirs();
                    extractFile(zipIn, entryFile);
                } else {
                    entryFile.mkdirs();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        } finally {
            zipIn.close();
        }
    }

    private void extractFile(ZipInputStream zipIn, File filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
