/*
 * %W% %E%
 *
 * Copyright (c) 2007, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.servicetag;

// This class is a copy of the com.sun.scn.servicetags.SystemEnvironment
// class from the Sun Connection source.
//
// The Service Tags team maintains the latest version of the implementation
// for system environment data collection.  JDK will include a copy of
// the most recent released version for a JDK release.  We rename
// the package to com.sun.servicetag so that the Sun Connection
// product always uses the latest version from the com.sun.scn.servicetags
// package. JDK and users of the com.sun.servicetag API
// (e.g. NetBeans and SunStudio) will use the version in JDK.

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * SystemEnvironment class collects the environment data with the
 * best effort from the underlying platform.
 */
public class SystemEnvironment {
    private String hostname;
    private String hostId;
    private String osName;
    private String osVersion;
    private String osArchitecture;
    private String systemModel;
    private String systemManufacturer;
    private String cpuManufacturer;
    private String serialNumber;
    private static SystemEnvironment sysEnv = null;

    public static synchronized SystemEnvironment getSystemEnvironment() {
        if (sysEnv == null) {
            String os = System.getProperty("os.name");
            if (os.equals("SunOS")) {
                sysEnv = new SolarisSystemEnvironment();
            } else if (os.equals("Linux")) {
                sysEnv = new LinuxSystemEnvironment();
            } else if (os.startsWith("Windows")) {
                sysEnv = new WindowsSystemEnvironment();
            } else {
                sysEnv = new SystemEnvironment();
            }
        }
        return sysEnv;
    }

    // package-private
    SystemEnvironment() {
        try {
            this.hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            this.hostname = "Unknown host";
        }
        this.hostId = "";
        this.osName = System.getProperty("os.name");
        this.osVersion = System.getProperty("os.version");
        this.osArchitecture = System.getProperty("os.arch");
        this.systemModel = "";
        this.systemManufacturer = "";
        this.cpuManufacturer = "";
        this.serialNumber = "";
    }


    /**
     * Sets the hostname.
     * @param hostname The hostname to set.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Sets the OS name.
     * @param osName The osName to set.
     */
    public void setOsName(String osName) {
        this.osName = osName;
    }

    /**
     * Sets the OS version.
     * @param osVersion The osVersion to set.
     */
    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    /**
     * Sets the OS architecture.
     * @param osArchitecture The osArchitecture to set.
     */
    public void setOsArchitecture(String osArchitecture) {
        this.osArchitecture = osArchitecture;
    }

    /**
     * Sets the system model.
     * @param systemModel The systemModel to set.
     */
    public void setSystemModel(String systemModel) {
        this.systemModel = systemModel;
    }

    /**
     * Sets the system manufacturer.
     * @param systemManufacturer The systemManufacturer to set.
     */
    public void setSystemManufacturer(String systemManufacturer) {
        this.systemManufacturer = systemManufacturer;
    }

    /**
     * Sets the cpu manufacturer.
     * @param cpuManufacturer The cpuManufacturer to set.
     */
    public void setCpuManufacturer(String cpuManufacturer) {
        this.cpuManufacturer = cpuManufacturer;
    }

    /**
     * Sets the serial number.
     * @param serialNumber The serialNumber to set.
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * Sets the hostid.  Truncates to a max length of 16 chars.
     * @param hostId The hostid to set.
     */
    public void setHostId(String hostId) {
        if (hostId == null || hostId.equals("null")) {
            hostId = "";
        }
        if (hostId.length() > 16) {
            hostId = hostId.substring(0,16);
        }
        this.hostId = hostId;
    }

    /**
     * Returns the hostname.
     * @return The hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Returns the osName.
     * @return The osName.
     */
    public String getOsName() {
        return osName;
    }

    /**
     * Returns the osVersion.
     * @return The osVersion.
     */
    public String getOsVersion() {
        return osVersion;
    }

    /**
     * Returns the osArchitecture.
     * @return The osArchitecture.
     */
    public String getOsArchitecture() {
        return osArchitecture;
    }

    /**
     * Returns the systemModel.
     * @return The systemModel.
     */
    public String getSystemModel() {
        return systemModel;
    }

    /**
     * Returns the systemManufacturer.
     * @return The systemManufacturer.
     */
    public String getSystemManufacturer() {
        return systemManufacturer;
    }

    /**
     * Returns the serialNumber.
     * @return The serialNumber.
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * Returns the hostId.
     * @return The hostId.
     */
    public String getHostId() {
        return hostId;
    }

    /**
     * Returns the cpuManufacturer.
     * @return The cpuManufacturer.
     */
    public String getCpuManufacturer() {
        return cpuManufacturer;
    }

    protected String getCommandOutput(String... command) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        Process p = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            p = pb.start();
            p.waitFor();

            if (p.exitValue() == 0) {
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(line);
                    }
                }
            }
            return sb.toString();
        } catch (InterruptedException ie) {
            // in case the command hangs
            if (p != null) {
                p.destroy();
            }
            return "";
        } catch (Exception e) {
            // ignore exception
            return "";
        } finally {
            if (p != null) {
                try {
                    p.getErrorStream().close();
                } catch (IOException e) {
                    // ignore
                }
                try {
                    p.getInputStream().close();
                } catch (IOException e) {
                    // ignore
                }
                try {
                    p.getOutputStream().close();
                } catch (IOException e) {
                    // ignore
                }
                p = null;
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                } 
            }
        }
    }

    protected String getFileContent(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(f));
            String line = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() > 0) {
                    if (sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(line);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            // ignore exception
            return "";
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                } 
            }
        }
    }
}

