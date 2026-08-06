# Focused Unit-Test Suite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add the first unit tests to MyOpenLab — 7 JUnit 4 test classes covering the recently committed security fixes and pure-logic utilities, wired into the existing Ant test infrastructure.

**Architecture:** No production code changes. Wire the shipped `distribution/lib/junit.jar` into the NetBeans/Ant test classpath (`nbproject/project.properties`), then add one test class per target under `test/`, mirroring source packages. The existing NetBeans Ant targets (`test`, `test-single`) activate automatically once `org.junit.Test` is on the test classpath.

**Tech Stack:** Java 11, Ant (Homebrew 1.10.17), NetBeans-generated `nbproject/build-impl.xml`, JUnit 4 (`distribution/lib/junit.jar`, which ships WITHOUT hamcrest).

## Global Constraints

- JUnit 4 only. The bundled `distribution/lib/junit.jar` contains **no hamcrest**, so tests MUST NOT use `assertThat`, `Assume.assumeThat`, or any hamcrest `Matcher`. Use `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, `assertArrayEquals`, `fail`.
- **Zero production code changes.** Only files under `test/` plus `nbproject/project.properties` may be modified. Do not edit anything under `src/`.
- Java 11 source/target, UTF-8 source encoding (`source.encoding=UTF-8` in project.properties).
- Every test file starts with the GPL v3 header (copy the header block verbatim from `src/VisualLogic/Settings.java` lines 1-17).
- Tests must be headless-safe: no `Frame`, no `JFrame`, no AWT windows, no `System.setProperty` affecting the JVM.
- Use JUnit 4 `TemporaryFolder` rule (`org.junit.rules.TemporaryFolder`) for temp files/dirs.
- `Expression` writes noise to stdout/stderr (e.g. `EOL `); ignore it — it is not a failure.
- Run tests from the repo root (`/Users/xuyi/Source/Java/myopenlab`). `java -version` there is Java 11.0.10.
- Commit after every task with a concise message in the repo's existing style (short, prefix `test:`).

---

### Task 1: Wire JUnit into the Ant test classpath

**Files:**
- Modify: `nbproject/project.properties`

**Interfaces:**
- Consumes: nothing.
- Produces: `org.junit.Test` resolvable on `javac.test.classpath` and `run.test.classpath`, activating the existing `test` and `test-single` Ant targets.

- [ ] **Step 1: Add the junit.jar file reference**

Open `nbproject/project.properties`. After the `file.reference.jakarta.xml.bind-api-2.3.3.jar=distribution/lib/jakarta.xml.bind-api-2.3.3.jar` line (line 51), add:

```
file.reference.junit.jar=distribution/lib/junit.jar
```

- [ ] **Step 2: Add junit.jar to the test classpath**

In the same file, change `javac.test.classpath` (lines 62-64) from:

```
javac.test.classpath=\
    ${javac.classpath}:\
    ${build.classes.dir}
```

to:

```
javac.test.classpath=\
    ${javac.classpath}:\
    ${file.reference.junit.jar}:\
    ${build.classes.dir}
```

`javac.test.classpath` feeds `run.test.classpath`, so this single edit covers both compile-time and run-time.

- [ ] **Step 3: Verify the wiring**

Run: `ant test`
Expected: `BUILD SUCCESSFUL`, and the output changes from `No tests executed.` (the junit-unavailable fallback `test-impl` at `nbproject/build-impl.xml:645`) to an actual JUnit run summary (e.g. `Tests run: 0`). With no test files yet the run reports zero tests — that is fine; real execution is proven in Task 2.

- [ ] **Step 4: Commit**

```bash
git add nbproject/project.properties
git commit -m "test: wire junit.jar into Ant test classpath"
```

---

### Task 2: CredentialCryptoTest

**Files:**
- Create: `test/VisualLogic/CredentialCryptoTest.java`
- Test target: `src/VisualLogic/CredentialCrypto.java`

**Interfaces:**
- Consumes: `VisualLogic.CredentialCrypto.encrypt(String) -> String` and `decrypt(String) -> String` (both public static). Empty/null input maps to `""`; non-`enc:v1:` or un-decryptable input passes through unchanged.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/VisualLogic/CredentialCryptoTest.java`:

```java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CredentialCryptoTest {

    @Test
    public void roundTripPreservesPlaintext() {
        String[] samples = {"password123", "p@ss wörd", "x",
            "a long credential string with special chars !#$%^&*()"};
        for (String sample : samples) {
            assertEquals(sample, CredentialCrypto.decrypt(CredentialCrypto.encrypt(sample)));
        }
    }

    @Test
    public void encryptEmptyOrNullReturnsEmpty() {
        assertEquals("", CredentialCrypto.encrypt(""));
        assertEquals("", CredentialCrypto.encrypt(null));
    }

    @Test
    public void decryptEmptyOrNullReturnsUnchanged() {
        assertEquals("", CredentialCrypto.decrypt(""));
        assertEquals("", CredentialCrypto.decrypt(null));
    }

    @Test
    public void decryptNonEncryptedPassthrough() {
        assertEquals("plain", CredentialCrypto.decrypt("plain"));
    }

    @Test
    public void decryptTamperedPayloadPassthrough() {
        String encrypted = CredentialCrypto.encrypt("secret");
        String base64 = encrypted.substring("enc:v1:".length());
        String corrupted = base64.substring(0, base64.length() - 4) + "AAAA";
        String stored = "enc:v1:" + corrupted;
        assertEquals(stored, CredentialCrypto.decrypt(stored));
    }

    @Test
    public void encryptProducesRandomCiphertext() {
        String a = CredentialCrypto.encrypt("same-input");
        String b = CredentialCrypto.encrypt("same-input");
        assertTrue(a.startsWith("enc:v1:"));
        assertFalse(a.equals(b));
    }
}
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=VisualLogic/CredentialCryptoTest.java -Dtest.includes=VisualLogic/CredentialCryptoTest.java`
Expected: 6 tests run, 0 failures, `BUILD SUCCESSFUL`. (If `test-single` misbehaves, fall back to `ant test`, which runs all `**/*Test.java`.)

- [ ] **Step 3: Commit**

```bash
git add test/VisualLogic/CredentialCryptoTest.java
git commit -m "test: add CredentialCrypto unit tests"
```

---

### Task 3: SafeXmlTest

**Files:**
- Create: `test/VisualLogic/SafeXmlTest.java`
- Test target: `src/VisualLogic/SafeXml.java`

**Interfaces:**
- Consumes: `VisualLogic.SafeXml.newDocumentBuilder()` -> `javax.xml.parsers.DocumentBuilder` (public static, throws `ParserConfigurationException`).
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/VisualLogic/SafeXmlTest.java`:

```java
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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.xml.sax.SAXException;

public class SafeXmlTest {

    @Test
    public void parsesWellFormedXml() throws Exception {
        org.w3c.dom.Document doc = SafeXml.newDocumentBuilder().parse(
                new ByteArrayInputStream("<root><child>value</child></root>".getBytes(StandardCharsets.UTF_8)));
        assertNotNull(doc.getDocumentElement());
        assertEquals("root", doc.getDocumentElement().getNodeName());
    }

    @Test(expected = SAXException.class)
    public void rejectsDoctypeDeclaration() throws Exception {
        SafeXml.newDocumentBuilder().parse(new ByteArrayInputStream(
                ("<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/hosts\">]>"
                        + "<root>&xxe;</root>").getBytes(StandardCharsets.UTF_8)));
    }

    @Test(expected = SAXException.class)
    public void rejectsExternalParameterEntity() throws Exception {
        SafeXml.newDocumentBuilder().parse(new ByteArrayInputStream(
                ("<!DOCTYPE foo [<!ENTITY % p \"file:///etc/hosts\">]>"
                        + "<root/>").getBytes(StandardCharsets.UTF_8)));
    }
}
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=VisualLogic/SafeXmlTest.java -Dtest.includes=VisualLogic/SafeXmlTest.java`
Expected: 3 tests run, 0 failures, `BUILD SUCCESSFUL`. (The `expected = SAXException.class` cases receive `SAXParseException`, which is a subclass — passes.)

- [ ] **Step 3: Commit**

```bash
git add test/VisualLogic/SafeXmlTest.java
git commit -m "test: add SafeXml XXE regression tests"
```

---

### Task 4: XMLSerializerTest

**Files:**
- Create: `test/VisualLogic/XMLSerializerTest.java`
- Test target: `src/VisualLogic/XMLSerializer.java`

**Interfaces:**
- Consumes: `VisualLogic.XMLSerializer.write(Object, String)` (throws `Exception`) and `XMLSerializer.read(String)` (throws `Exception`, `SecurityException` on blocked class). `XMLSerializer.read` internally also rejects DOCTYPE via `SafeXml`.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/VisualLogic/XMLSerializerTest.java`:

```java
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
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=VisualLogic/XMLSerializerTest.java -Dtest.includes=VisualLogic/XMLSerializerTest.java`
Expected: 4 tests run, 0 failures, `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add test/VisualLogic/XMLSerializerTest.java
git commit -m "test: add XMLSerializer allowlist regression tests"
```

---

### Task 5: UnzipFilesTest

**Files:**
- Create: `test/de/myopenlab/update/UnzipFilesTest.java`
- Test target: `src/de/myopenlab/update/UnzipFiles.java`

**Interfaces:**
- Consumes: `de.myopenlab.update.UnzipFiles.unzip(String zipFilePath, String destDirectory)` (throws `IOException`; throws `IOException("Blocked zip entry outside destination directory: ...")` on zip-slip entries).
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/de/myopenlab/update/UnzipFilesTest.java`:

```java
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
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=de/myopenlab/update/UnzipFilesTest.java -Dtest.includes=de/myopenlab/update/UnzipFilesTest.java`
Expected: 2 tests run, 0 failures, `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add test/de/myopenlab/update/UnzipFilesTest.java
git commit -m "test: add UnzipFiles zip-slip regression tests"
```

---

### Task 6: IniFileTest

**Files:**
- Create: `test/de/myopenlab/update/IniFileTest.java`
- Test target: `src/de/myopenlab/update/IniFile.java`

**Interfaces:**
- Consumes: `de.myopenlab.update.IniFile(String path)` (throws `IOException`), plus `getString(String section, String key, String defaultvalue)`, `getInt(...)`, `getFloat(...)`, `getDouble(...)` (all with a default-value last argument).
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/de/myopenlab/update/IniFileTest.java`:

```java
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
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=de/myopenlab/update/IniFileTest.java -Dtest.includes=de/myopenlab/update/IniFileTest.java`
Expected: 2 tests run, 0 failures, `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add test/de/myopenlab/update/IniFileTest.java
git commit -m "test: add IniFile unit tests"
```

---

### Task 7: Tools2Test

**Files:**
- Create: `test/de/myopenlab/update/Tools2Test.java`
- Test target: `src/de/myopenlab/update/Tools2.java`

**Interfaces:**
- Consumes: `de.myopenlab.update.Tools2.copy(InputStream, OutputStream, int bufferSize)` (throws `IOException`) and `Tools2.deleteFolder(File)` (void). Only these two pure methods are tested; `getPackageZip` requires network + `javax.xml.bind` and is out of scope.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/de/myopenlab/update/Tools2Test.java`:

```java
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
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=de/myopenlab/update/Tools2Test.java -Dtest.includes=de/myopenlab/update/Tools2Test.java`
Expected: 2 tests run, 0 failures, `BUILD SUCCESSFUL`.

- [ ] **Step 3: Commit**

```bash
git add test/de/myopenlab/update/Tools2Test.java
git commit -m "test: add Tools2 copy and deleteFolder unit tests"
```

---

### Task 8: ExpressionTest

**Files:**
- Create: `test/MyParser/ExpressionTest.java`
- Test target: `src/MyParser/Expression.java`

**Interfaces:**
- Consumes: `MyParser.Expression` (public static field `liste`, `ArrayList`), `MyParser.Expression.Scanner(Reader)`, `Expression.yyparse(yyInput, Object)` -> `Object` (throws `IOException`, `Expression.yyException`). `Expression.liste` accumulates instruction tokens across parses, so it must be cleared before each parse.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Write the test**

Create `test/MyParser/ExpressionTest.java`:

```java
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
package MyParser;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class ExpressionTest {

    @Before
    public void clearTokenList() {
        Expression.liste.clear();
    }

    private List<?> parse(String input) throws Exception {
        Expression.liste.clear();
        new Expression().yyparse(new Expression.Scanner(new StringReader(input)), null);
        return Expression.liste;
    }

    @Test
    public void parsesAddition() throws Exception {
        assertEquals(Arrays.asList("PUSHB 1", "PUSHB 2", "ADD"), parse("1+2"));
    }

    @Test
    public void parsesAssignment() throws Exception {
        assertEquals(Arrays.asList("PUSHB 5", "POPI a"), parse("a=5"));
    }

    @Test
    public void parsesTrigFunction() throws Exception {
        assertEquals(Arrays.asList("PUSHB 1", "SIN "), parse("SIN(1)"));
    }

    @Test
    public void parsesRelationalExpression() throws Exception {
        assertEquals(Arrays.asList("PUSHB 1", "PUSHB 2", "IF_A<B "), parse("1<2"));
    }

    @Test
    public void rejectsMalformedInput() throws Exception {
        Expression.liste.clear();
        try {
            new Expression().yyparse(new Expression.Scanner(new StringReader("1+")), null);
            fail("expected Expression.yyException");
        } catch (Expression.yyException expected) {
            // expected
        }
    }
}
```

- [ ] **Step 2: Run the test**

Run: `ant test-single -Djavac.includes=MyParser/ExpressionTest.java -Dtest.includes=MyParser/ExpressionTest.java`
Expected: 5 tests run, 0 failures, `BUILD SUCCESSFUL`. (Parser noise on stdout/stderr, e.g. `EOL `, is expected and harmless.)

- [ ] **Step 3: Commit**

```bash
git add test/MyParser/ExpressionTest.java
git commit -m "test: add MyParser Expression unit tests"
```

---

### Task 9: Full-suite verification

**Files:**
- None created or modified.

**Interfaces:**
- Consumes: all seven test classes from Tasks 2-8.

- [ ] **Step 1: Run the entire suite**

Run: `ant clean test`
Expected: `BUILD SUCCESSFUL`, with a junit summary reporting 24 tests run, 0 failures, 0 errors.

- [ ] **Step 2: Verify no production code changed**

Run: `git status --short`
Expected: only changes under `test/` (plus `nbproject/project.properties` already committed in Task 1 and the plan/spec docs). No entries under `src/`.

- [ ] **Step 3: Spot-check the HTML report**

Run: `ls build/test/results/html/`
Expected: an `index.html` (and per-test HTML files) exist from the junitreport step of `ant test`.
