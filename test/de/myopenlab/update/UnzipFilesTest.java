/*
 * MyOpenLab by Carmelo Salafia www.myopenlab.de
 * Copyright (C) 2004  Carmelo Salafia cswi@gmx.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.myopenlab.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class UnzipFilesTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private File makeZip(File dir, String[] entries) throws IOException {
        File zip = new File(dir, "pkg.zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip))) {
            for (String entry : entries) {
                zos.putNextEntry(new ZipEntry(entry));
                zos.write(("content-of-" + entry).getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
            }
        }
        return zip;
    }

    @Test
    public void extractsNestedFilesAndDirectories() throws Exception {
        File src = temp.newFolder("src");
        File zip = makeZip(src, new String[]{"a.txt", "sub/b.txt", "sub/deep/c.txt"});
        File dest = temp.newFolder("out");

        new UnzipFiles().unzip(zip.getAbsolutePath(), dest.getAbsolutePath());

        assertEquals("content-of-a.txt",
                new String(Files.readAllBytes(new File(dest, "a.txt").toPath()), StandardCharsets.UTF_8));
        assertEquals("content-of-sub/b.txt",
                new String(Files.readAllBytes(new File(dest, "sub/b.txt").toPath()), StandardCharsets.UTF_8));
        assertEquals("content-of-sub/deep/c.txt",
                new String(Files.readAllBytes(new File(dest, "sub/deep/c.txt").toPath()), StandardCharsets.UTF_8));
    }

    @Test
    public void rejectsZipSlipEntry() throws Exception {
        File src = temp.newFolder("src");
        File zip = makeZip(src, new String[]{"../evil.txt"});
        File dest = temp.newFolder("out");

        try {
            new UnzipFiles().unzip(zip.getAbsolutePath(), dest.getAbsolutePath());
            org.junit.Assert.fail("expected zip-slip entry to be rejected");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("Blocked zip entry"));
        }
        assertFalse(new File(dest.getParentFile(), "evil.txt").exists());
    }
}
