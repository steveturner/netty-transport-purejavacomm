package com.beauhinks.purejavacomm;

import io.netty.channel.ChannelOption;
import com.beauhinks.purejavacomm.PureJavaCommChannelConfig.Databits;
import com.beauhinks.purejavacomm.PureJavaCommChannelConfig.Paritybit;
import com.beauhinks.purejavacomm.PureJavaCommChannelConfig.Stopbits;

/**
 * Option for configuring a serial port connection
 */
public final class PureJavaCommChannelOption<T> extends ChannelOption<T> {
    public static final PureJavaCommChannelOption<Integer> BAUD_RATE =
            new PureJavaCommChannelOption<Integer>("BAUD_RATE");

    public static final PureJavaCommChannelOption<Boolean> DTR =
            new PureJavaCommChannelOption<Boolean>("DTR");

    public static final PureJavaCommChannelOption<Boolean> RTS =
            new PureJavaCommChannelOption<Boolean>("RTS");

    public static final PureJavaCommChannelOption<Stopbits> STOP_BITS =
            new PureJavaCommChannelOption<Stopbits>("STOP_BITS");

    public static final PureJavaCommChannelOption<Databits> DATA_BITS =
            new PureJavaCommChannelOption<Databits>("DATA_BITS");

    public static final PureJavaCommChannelOption<Paritybit> PARITY_BIT =
            new PureJavaCommChannelOption<Paritybit>("PARITY_BIT");

    public static final PureJavaCommChannelOption<Integer> WAIT_TIME =
            new PureJavaCommChannelOption<Integer>("WAIT_TIME");

    public static final PureJavaCommChannelOption<Integer> READ_TIMEOUT =
            new PureJavaCommChannelOption<Integer>("READ_TIMEOUT");

    @SuppressWarnings("deprecation")
    private PureJavaCommChannelOption(String name) {
        super(name);
    }
}
