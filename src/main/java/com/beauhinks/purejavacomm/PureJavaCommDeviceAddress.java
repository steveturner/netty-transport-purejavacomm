package com.beauhinks.purejavacomm;

import java.net.SocketAddress;

/**
 * A {@link SocketAddress} subclass to wrap the serial port address of a PureJavaComm
 * device (e.g. COM1, /dev/ttyUSB0).
 */
public class PureJavaCommDeviceAddress extends SocketAddress {

    private static final long serialVersionUID = -2907820090993709523L;

    private final String value;

    /**
     * Creates a PureJavaCommDeviceAddress representing the address of the serial port.
     *
     * @param value the address of the device (e.g. COM1, /dev/ttyUSB0, ...)
     */
    public PureJavaCommDeviceAddress(String value) {
        this.value = value;
    }

    /**
     * @return The serial port address of the device (e.g. COM1, /dev/ttyUSB0, ...)
     */
    public String value() {
        return value;
    }
}
