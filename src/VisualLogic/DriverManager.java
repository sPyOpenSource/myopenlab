/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package VisualLogic;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import VisualLogic.gui.FrameMain;

class MyOpenLabDriver {

    public String driverName = "";
    public String driverPath = "";
    public Object owner = null;
    public MyOpenLabDriverIF driver;

    private final FileAccessService fileAccessService;
    private final GitHubConfig remoteConfig;

    public MyOpenLabDriver(String driverName, String driverPath, FileAccessService fas, GitHubConfig config) {
        this.driverName = driverName;
        this.driverPath = driverPath;
        this.fileAccessService = fas;
        this.remoteConfig = config;

        // Remote drivers are not present on the local filesystem; load their
        // driver.info and class bytes through the FileAccessService seam. Local
        // driver directories keep the existing File-based loading unchanged.
        boolean remote = fileAccessService != null && remoteConfig != null
                && !new File(driverPath).exists();

        DriverInfo info;
        if (remote) {
            String text = fileAccessService.readText(driverPath + "/driver.info");
            info = text == null ? null : parseDriverInfo(text);
        } else {
            info = Tools.openDriverInfo(new File(driverPath));
        }

        if (info != null) {
            Loader loader = new Loader();
            try {
                URL url1;
                URL url2;
                if (remote) {
                    String classLogical = driverPath;
                    if (info.classpath != null && !info.classpath.isEmpty()) {
                        classLogical = classLogical + "/" + info.classpath;
                    }
                    url1 = dirUrl(remoteConfig.rawUrlFor(classLogical));

                    String jarLogical = driverPath;
                    if (info.Jar != null && !info.Jar.isEmpty()) {
                        jarLogical = jarLogical + "/" + info.Jar;
                    }
                    url2 = new URL(remoteConfig.rawUrlFor(jarLogical));
                } else {
                    url1 = new File(driverPath + info.classpath).toURI().toURL();
                    url2 = new File(driverPath + info.Jar).toURI().toURL();
                }

                driver = (MyOpenLabDriverIF) loader.ladeClasseDriver(new URL[]{url1, url2}, info.Classe);
                System.out.println("Driver loaded : " + driverName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Ensures a URL points at a directory base (trailing "/") so that
     * URLClassLoader can resolve {@code Foo.class} against it over HTTP.
     */
    private static URL dirUrl(String spec) throws MalformedURLException {
        if (!spec.endsWith("/")) {
            spec = spec + "/";
        }
        return new URL(spec);
    }

    /**
     * Parses a {@code driver.info} file's text into a DriverInfo. Mirrors the
     * parsing performed by {@link Tools#openDriverInfo(File)} for local drivers.
     */
    private static DriverInfo parseDriverInfo(String text) {
        DriverInfo tmp = new DriverInfo();
        try (java.io.StringReader reader = new java.io.StringReader(text);
             java.io.BufferedReader input = new java.io.BufferedReader(reader)) {
            String inputString;
            while ((inputString = input.readLine()) != null) {
                if (!inputString.equalsIgnoreCase("")) {
                    String elementClass = Tools.extractClassName(inputString);
                    String elementName = inputString.substring(elementClass.length());
                    elementName = elementName.trim();
                    elementName = elementName.substring(1);
                    elementClass = elementClass.trim();
                    elementName = elementName.trim();

                    if (elementClass.equalsIgnoreCase("JAR")) {
                        tmp.Jar = elementName;
                    }
                    if (elementClass.equalsIgnoreCase("CLASS")) {
                        tmp.Classe = elementName;
                    }
                    if (elementClass.equalsIgnoreCase("CLASSPATH")) {
                        tmp.classpath = elementName;
                    }
                    if (elementClass.equalsIgnoreCase("CLASSPATH2")) {
                        tmp.classpath2 = elementName;
                    }
                }
            }
        } catch (IOException ex) {
        }
        return tmp;
    }

}

public class DriverManager {

    // Haelt alle Treiber in der Liste driver
    private final ArrayList<MyOpenLabDriver> drivers = new ArrayList<>();
    private final FrameMain frameCircuit;

    // Remote element/driver loading seam. When fileAccessService == null the
    // existing local File-based driver loading is used unchanged.
    private final FileAccessService fileAccessService;
    private GitHubConfig remoteConfig;

    public DriverManager(FrameMain frameCircuit) {
        this(frameCircuit, null, null);
    }

    public DriverManager(FrameMain frameCircuit, FileAccessService fas) {
        this(frameCircuit, fas, null);
    }

    public DriverManager(FrameMain frameCircuit, FileAccessService fas, GitHubConfig config) {
        this.frameCircuit = frameCircuit;
        this.fileAccessService = fas;
        this.remoteConfig = config;
    }

    private MyOpenLabDriver findDriver(String driverName) {
        MyOpenLabDriver d;
        for (int i = 0; i < drivers.size(); i++) {
            d = drivers.get(i);
            if (d.driverName.equalsIgnoreCase(driverName)) {
                return d;
            }
        }
        return null;
    }

    // --------------    Public    ---------------------------------------------
    /*
     * If result = null : Driver not found or already Opened
     * from another Component
     */
    public MyOpenLabDriverIF openDriver(Element element, String driverName, ArrayList args) {
        try {
            MyOpenLabDriver driver = findDriver(driverName);
            {
                if (driver.owner != element) {
                    driver.owner = element;
                    driver.driver.driverStart(args);
                    return driver.driver;
                }
            }
        } catch (Exception ex) {
            System.out.println("Errorin openDriver: " + ex);
            return null;
        }

        return null;
    }

    public void closeDriver(Element element, String driverName) {
        MyOpenLabDriver driver = findDriver(driverName);
        if (driver != null && driver.owner != null) {
            if (driver.owner == element) {
                driver.driver.driverStop();
                driver.owner = null;
            }
        }
    }

    /*
     *  if Result = true anithing OK and Driver registred!
     *  if Result = false : driver already registred
     */

    public boolean registerDriver(String driverPath) {
        File file = new File(driverPath);

        String driverName = file.getName();

        MyOpenLabDriver d = findDriver(driverName);
        if (d == null) {
            drivers.add(new MyOpenLabDriver(driverName, driverPath, fileAccessService, remoteConfig));
            return true;
        } else {
            return false;
        }
    }

}
