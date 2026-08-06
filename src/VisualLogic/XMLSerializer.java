/*
 * Copyright (C) 2016 carmelosalafia
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

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLSerializer {
    public static void write(Object f, String filename) throws Exception{
        try (XMLEncoder encoder = new XMLEncoder(
                new BufferedOutputStream(
                        new FileOutputStream(filename)))) {
            encoder.writeObject(f);
        }
    }

    public static Object read(String filename) throws Exception {
        validateClassAllowlist(filename);
        Object o;
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
                new FileInputStream(filename)))) {
            o = (Object)decoder.readObject();
        }
        return o;
    }

    private static void validateClassAllowlist(String filename) throws Exception {
        Document doc = SafeXml.newDocumentBuilder().parse(new File(filename));

        checkClassAttributes(doc.getElementsByTagName("object"));
        checkClassAttributes(doc.getElementsByTagName("void"));
        checkClassAttributes(doc.getElementsByTagName("array"));

        NodeList classElements = doc.getElementsByTagName("class");
        for (int i = 0; i < classElements.getLength(); i++) {
            checkClassName(classElements.item(i).getTextContent().trim());
        }
    }

    private static void checkClassAttributes(NodeList nodes) throws Exception {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            Node attr = node.getAttributes().getNamedItem("class");
            if (attr != null) {
                checkClassName(attr.getNodeValue());
            }
        }
    }

    private static void checkClassName(String name) throws Exception {
        if (name == null || name.isEmpty()) {
            return;
        }
        if (name.equals("VisualLogic.Settings")
                || name.equals("java.awt.Point")
                || name.equals("java.awt.Dimension")
                || name.equals("java.awt.Color")
                || name.equals("java.lang.String")
                || name.startsWith("java.util.")) {
            return;
        }
        throw new SecurityException("Blocked class in config XML: " + name);
    }
}