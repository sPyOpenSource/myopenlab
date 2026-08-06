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
package VisualLogic;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXParseException;

public class XMLSerializerTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void settingsRoundTrip() throws Exception {
        File file = temp.newFile("settings.xml");
        Settings settings = new Settings();
        settings.setRepository_login_username("alice");
        settings.setRepository_login_password("pw123");
        settings.setCurrentDirectory("/tmp/x");

        XMLSerializer.write(settings, file.getAbsolutePath());

        Settings read = (Settings) XMLSerializer.read(file.getAbsolutePath());
        assertEquals("alice", read.getRepository_login_username());
        assertEquals("pw123", read.getRepository_login_password());
        assertEquals("/tmp/x", read.getCurrentDirectory());
    }

    @Test
    public void allowsAllowlistedSimpleTypes() throws Exception {
        File file = temp.newFile("simple.xml");
        ArrayList<Object> payload = new ArrayList<>();
        payload.add("text");
        payload.add(new Point(3, 4));
        payload.add(new Dimension(10, 20));

        XMLSerializer.write(payload, file.getAbsolutePath());

        ArrayList<?> read = (ArrayList<?>) XMLSerializer.read(file.getAbsolutePath());
        assertEquals(3, read.size());
        assertEquals("text", read.get(0));
        assertEquals(new Point(3, 4), read.get(1));
        assertEquals(new Dimension(10, 20), read.get(2));
    }

    @Test(expected = SecurityException.class)
    public void rejectsBlockedClass() throws Exception {
        File file = temp.newFile("evil.xml");
        Files.write(file.toPath(),
                "<java><object class=\"java.lang.Runtime\"/></java>".getBytes(StandardCharsets.UTF_8));
        XMLSerializer.read(file.getAbsolutePath());
    }

    @Test(expected = SAXParseException.class)
    public void rejectsDoctype() throws Exception {
        File file = temp.newFile("doctype.xml");
        Files.write(file.toPath(),
                ("<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/hosts\">]><java>&xxe;</java>")
                        .getBytes(StandardCharsets.UTF_8));
        XMLSerializer.read(file.getAbsolutePath());
    }
}
