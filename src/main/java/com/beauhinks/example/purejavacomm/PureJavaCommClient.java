package com.beauhinks.example.purejavacomm;

import com.beauhinks.purejavacomm.PureJavaCommChannel;
import com.beauhinks.purejavacomm.PureJavaCommDeviceAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import purejavacomm.CommPortIdentifier;

import java.util.Enumeration;


/**
 * Sends one message to a serial device
 */
public final class PureJavaCommClient {

    public static void main(String[] args) throws Exception {
        CommPortIdentifier portid = null;
        Enumeration e = CommPortIdentifier.getPortIdentifiers();
        while (e.hasMoreElements()) {
            portid = (CommPortIdentifier) e.nextElement();
            System.out.println("found " + portid.getName());
        }
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(PureJavaCommChannel.class)
                    .handler(new ChannelInitializer<PureJavaCommChannel>() {
                        @Override
                        public void initChannel(PureJavaCommChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LineBasedFrameDecoder(32768),
                                    new StringEncoder(),
                                    new StringDecoder(),
                                    new PureJavaCommClientHandler()
                            );
                        }
                    });

            ChannelFuture f = b.connect(new PureJavaCommDeviceAddress("/dev/ttyS0")).sync();
            f.channel().write()
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private PureJavaCommClient() {
    }
}