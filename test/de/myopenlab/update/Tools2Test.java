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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class Tools2Test {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void copyTransfersBytesAcrossMultipleBuffers() throws Exception {
        byte[] data = new byte[5000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i % 251);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Tools2.copy(new ByteArrayInputStream(data), out, 1024);

        assertArrayEquals(data, out.toByteArray());
    }

    @Test
    public void deleteFolderRemovesNestedTree() throws Exception {
        File tree = temp.newFolder("tree");
        File nested = new File(tree, "a/b");
        assertTrue(nested.mkdirs());
        assertTrue(new File(nested, "f1.txt").createNewFile());
        assertTrue(new File(tree, "f2.txt").createNewFile());

        Tools2.deleteFolder(tree);

        assertFalse(tree.exists());
    }
}