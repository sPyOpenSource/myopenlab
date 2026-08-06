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
