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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class IniFileTest {

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void parsesSectionsAndValues() throws Exception {
        File ini = temp.newFile("test.ini");
        Files.write(ini.toPath(),
                ("[sensor]\nname=temp1\nvalue=3.5\ncount=7\n\n[alarm]\nenabled=true\n")
                        .getBytes(StandardCharsets.UTF_8));

        IniFile config = new IniFile(ini.getAbsolutePath());

        assertEquals("temp1", config.getString("sensor", "name", "default"));
        assertEquals(3.5f, config.getFloat("sensor", "value", 0f), 0.0001f);
        assertEquals(7, config.getInt("sensor", "count", 0));
        assertEquals("true", config.getString("alarm", "enabled", "false"));
    }

    @Test
    public void returnsDefaultsForMissingKeysAndSections() throws Exception {
        File ini = temp.newFile("empty.ini");
        Files.write(ini.toPath(), "[empty]\n".getBytes(StandardCharsets.UTF_8));

        IniFile config = new IniFile(ini.getAbsolutePath());

        assertEquals("dflt", config.getString("missing-section", "key", "dflt"));
        assertEquals("dflt2", config.getString("empty", "missing-key", "dflt2"));
        assertEquals(42, config.getInt("empty", "missing-key", 42));
        assertEquals(1.5d, config.getDouble("missing-section", "key", 1.5d), 0.0001d);
    }
}
