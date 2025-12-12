//*****************************************************************************
//* Element of MyOpenLab Library                                              *
//*                                                                           *
//* Copyright (C) 2004  Carmelo Salafia (cswi@gmx.de)                         *
//*                                                                           *
//* This library is free software; you can redistribute it and/or modify      *
//* it under the terms of the GNU Lesser General Public License as published  *
//* by the Free Software Foundation; either version 2.1 of the License,       *
//* or (at your option) any later version.                                    *
//* http://www.gnu.org/licenses/lgpl.html                                     *
//*                                                                           *
//* This library is distributed in the hope that it will be useful,           *
//* but WITHOUTANY WARRANTY; without even the implied warranty of             *
//* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                      *
//* See the GNU Lesser General Public License for more details.               *
//*                                                                           *
//* You should have received a copy of the GNU Lesser General Public License  *
//* along with this library; if not, write to the Free Software Foundation,   *
//* Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA                  *
//*****************************************************************************

import VisualLogic.*;
import VisualLogic.variables.*;
import java.util.*;
import java.io.*;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Driver {
    public static SerialPort serss;
    public int timeOut = 50;
    public boolean error;

    private VS1DByte outBytes = new VS1DByte(0);
    private SerialReader serialThread;

    public boolean useOwnInHandler = false;
    public String port;
    public MyOpenLabDriverOwnerIF owner;

    public String[] listSerialPorts() {
        return SerialPortList.getPortNames();
    }

    public Driver(String port, int baud, int bits, int stopBits, int parity) {
        this.port = port;

        error = true;

        System.out.println("DRIVER INIT");

        if (port.trim().equalsIgnoreCase("NULL")) {
            System.out.println("NULL OK");
        } else {
            try {
                System.out.println("PORT :" + port);

                serss = new SerialPort(port);// portID.open("MYOPENLAB", 2000);

                serss.setParams(baud, bits, stopBits, parity);

                error = false;
            } catch (SerialPortException ex) {
                System.out.println("Fehler in RS232 Driver : " + ex);
            }
        }

        System.out.println("DRIVER INIT END");
    }

    public void start() throws SerialPortException {
        try {
            if (useOwnInHandler) {
                System.out.println("useOwnInHandler");
                serialThread = new SerialReader();
                serialThread.start();
            } else {
                System.out.println("not useOwnInHandler");
                serss.addEventListener(new commListener());
                //serss.notifyOnDataAvailable(true);
            }
        } catch (SerialPortException e) {
            System.out.println("Fehler: " + e);
        }
    }

    public void setTimeOut(int millis) {
        timeOut = millis;
    }

    public void close() {
        if (serialThread != null) {
            serialThread.stop = true;
            System.out.println("Closing Driver");
        }
        if (serss != null) {
            //serss.close();
        }
    }

    public void sendCommand(String commando, Object value) {
        System.out.println("RS232 COMMAND : " + commando);

        if (value instanceof VS1DByte values) {
            //int len = values.getLength();
            byte[] dest = values.getValues();

            /*if (commando.equals("RTSON")) serss.setRTS(true);
             if (commando.equals("RTSOFF")) serss.setRTS(false);
             if (commando.equals("DTSON")) serss.setDTR(true);
             if (commando.equals("DTSOFF")) serss.setDTR(false);*/
            if (commando.equals("SENDBYTES")) {
                sendBytes(dest);
            }
        }
    }

    public void registerOwner(MyOpenLabDriverOwnerIF owner) {
        this.owner = owner;
        //firstTime=true;
    }

    public void setRTS(boolean value) throws SerialPortException {
        serss.setRTS(value);
    }

    public void setDTR(boolean value) throws SerialPortException {
        serss.setDTR(value);
    }

    public void sendBytes(byte[] bytes) {
        try {
            if (serss != null) {
                serss.writeBytes(bytes);
                //serss.flush();
            }
        } catch (SerialPortException ex) {
            //element.jShowMessage("Error sending Bytes  : "+ex.toString());
        }
    }

    /**
     * Returns a long that contains the same n bits as the given long,with
     * cleared upper rest.
     *
     * @param value the value which lowest bits should be copied.
     * @param bits the number of lowest bits that should be copied.
     * @return a long value that shares the same low bits as the given value.
     */
    private static long copyBits(final long value, byte bits) {
        final boolean logging = false; //turn off logging here
        long converted = 0;
        long comp = 1L << bits;
        while (--bits != -1) {
            if (((comp >>= 1) & value) != 0) {
                converted |= comp;
            }
            if (logging) {
                System.out.print((comp & value) != 0 ? "1" : "0");
            }
        }
        if (logging) {
            System.out.println();
        }
        return converted;
    }

    /**
     * Converts an unsigned byte to a signed short.
     *
     * @param value an unsigned byte value
     * @return a signed short that represents the unsigned byte's value.
     */
    public static short toSigned(final byte value) {
        return (short) copyBits(value, (byte) 8);
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("RXTX: STARTT");
            while (true) {
                long time2 = System.currentTimeMillis();

                long diff = time2 - time;
                //System.out.println("Time="+diff);

                if (diff > timeOut) {
                    break;
                }
                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                }
            }
            thread = null;

            byte buff[] = new byte[strBuffer.size()];
            for (int i = 0; i < strBuffer.size(); i++) {
                buff[i] = strBuffer.get(i);
            }
            System.out.println("RXTX:" + strBuffer.toString());

            strBuffer.clear();

            outBytes.setValues(buff);
            owner.getCommand("BYTESRECEIVED", (Object) outBytes);
        }
    }

    MyThread thread;
    long time = 0;

    public void make() {
        if (thread == null) {
            thread = new MyThread();
            thread.start();
        }
        time = System.currentTimeMillis();
    }

    ArrayList<Byte> strBuffer = new ArrayList<>();

    public class SerialReader extends Thread {
        private InputStream in;
        public boolean stop = false;

        @Override
        public void run() {
            stop = false;
            try {
                while (true) {
                    if (stop) {
                        return;
                    }
                    while (this.in.available() > 0) {
                        int cc = in.read();
                        owner.getSingleByte(cc);

                        if (stop) {
                            return;
                        }
                    }
                }
            } catch (IOException ex) {

            }
        }
    }

    class commListener implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if (event.getEventType() == SerialPortEvent.TXEMPTY) {
                System.out.println("Ignored event");
            }

            if (event.getEventType() == SerialPortEvent.RXCHAR) {
                try {
                    byte[] buffer = serss.readBytes();

                    for (int i = 0; i < buffer.length; i++) {
                        strBuffer.add(buffer[i]);
                    }

                    make();
                } catch (SerialPortException e) {
                    System.out.println("Fehler: " + e);
                }
            }
        }
    }
}
