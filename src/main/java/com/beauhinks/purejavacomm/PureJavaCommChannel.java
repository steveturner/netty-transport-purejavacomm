package com.beauhinks.purejavacomm;

import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.OioByteStreamChannel;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import static com.beauhinks.purejavacomm.PureJavaCommChannelOption.*;

/**
 * A channel to a serial device using the PureJavaComm library.
 */
public class PureJavaCommChannel extends OioByteStreamChannel {

    private static final PureJavaCommDeviceAddress LOCAL_ADDRESS = new PureJavaCommDeviceAddress("localhost");

    private final PureJavaCommChannelConfig config;

    private boolean open = true;
    private PureJavaCommDeviceAddress deviceAddress;
    private SerialPort serialPort;

    public PureJavaCommChannel() {
        super(null);

        config = new DefaultPureJavaCommChannelConfig(this);
    }

    @Override
    public PureJavaCommChannelConfig config() {
        return config;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    protected AbstractUnsafe newUnsafe() {
        return new PureJavaCommUnsafe();
    }

    @Override
    protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        PureJavaCommDeviceAddress remote = (PureJavaCommDeviceAddress) remoteAddress;
        final CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(remote.value());
        final CommPort commPort = cpi.open(getClass().getName(), 1000);

        deviceAddress = remote;

        serialPort = (SerialPort) commPort;
    }

    protected void doInit() throws Exception {
        serialPort.setSerialPortParams(
                config().getOption(BAUD_RATE),
                config().getOption(DATA_BITS).value(),
                config().getOption(STOP_BITS).value(),
                config().getOption(PARITY_BIT).value()
        );
        serialPort.setDTR(config().getOption(DTR));
        serialPort.setRTS(config().getOption(RTS));

        activate(serialPort.getInputStream(), serialPort.getOutputStream());
    }

    @Override
    public PureJavaCommDeviceAddress localAddress() {
        return (PureJavaCommDeviceAddress) super.localAddress();
    }

    @Override
    public PureJavaCommDeviceAddress remoteAddress() {
        return (PureJavaCommDeviceAddress) super.remoteAddress();
    }

    @Override
    protected PureJavaCommDeviceAddress localAddress0() {
        return LOCAL_ADDRESS;
    }

    @Override
    protected PureJavaCommDeviceAddress remoteAddress0() {
        return deviceAddress;
    }

    @Override
    protected void doBind(SocketAddress localAddress) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDisconnect() throws Exception {
        doClose();
    }

    @Override
    protected void doClose() throws Exception {
        open = false;
        try {
            super.doClose();
        } finally {
            if (serialPort != null) {
                serialPort.removeEventListener();
                serialPort.close();
                serialPort = null;
            }
        }
    }

    private final class PureJavaCommUnsafe extends AbstractUnsafe {
        @Override
        public void connect(
                final SocketAddress remoteAddress,
                final SocketAddress localAddress, final ChannelPromise promise) {
            if (!ensureOpen(promise)) {
                return;
            }

            try {
                final boolean wasActive = isActive();
                doConnect(remoteAddress, localAddress);

                int waitTime = config().getOption(WAIT_TIME);
                if (waitTime > 0) {
                    eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doInit();
                                promise.setSuccess();
                                if (!wasActive && isActive()) {
                                    pipeline().fireChannelActive();
                                }
                            } catch (Throwable t) {
                                promise.setFailure(t);
                                closeIfClosed();
                            }
                        }
                    }, waitTime, TimeUnit.MILLISECONDS);
                } else {
                    doInit();
                    promise.setSuccess();
                    if (!wasActive && isActive()) {
                        pipeline().fireChannelActive();
                    }
                }
            } catch (Throwable t) {
                promise.setFailure(t);
                closeIfClosed();
            }
        }
    }
}