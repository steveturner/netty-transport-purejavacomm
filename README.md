netty-transport-purejavacomm
============================

A netty serial pipeline using JNA and PureJavaComm

#Intro
Typically when interacting with serial ports from Java you have three options:

* [RxTx](http://rxtx.qbang.org/)
* [SerialIO](https://serialio.com/products/applications/custom-prior-software-versions/java-serial-port)
* [JSS](https://github.com/scream3r/java-simple-serial-connector/)

All are actually mature solutions but SerialIO is a commercial product and JSS and RxTx either require the native libraries to be intalled/compiled for the host platform. Personally, I've had issues with RxTx and the C++ source code can be a bit difficult to understand when a problem arrises.

#Solution
[PureJavaComm](https://github.com/nyholku/purejavacomm) is a solution which I've had the privilege to work with extensively. What's nice about PureJavaComm is that it is entirely written in Java except for specific calls to platform functions which leverage JNA. In their own words:
>PJC is written 100% in Java so it is easy for Java programmers to develop and debug and it requires no native libraries. Native access to the underlaying operating system's serial port programming interface is provided by the wonderful JNA library which takes away all the pain of compiling and deploying native code.

In my own use, I've found PureJavaComm to be extremely easy to use and extend to serial devices with their own platform libraries by leveraging [JNAerator](https://code.google.com/p/jnaerator/) to generate JNA wrappers to library code.

##Netty And Serial
Netty already provides an excellent wrapper to interacting with a serial port at the application level via the RxTx transport. However, as mentioned above you still need the native libraries installed. To alleviate this, I've created [Netty Transport PureJavaComm](https://github.com/steveturner/netty-transport-purejavacomm/tree/develop)
Here's a quick sample based on the existing Netty RxTx code:

``` java
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
```
To get up and running and test the code there are two VM's in the develop branch, master and slave. Start the master with `vagrant up` and then `cd` into the slave directory and start the slave with `vagrant up`. `cd` to /Vagrant and run `mvn exec:java` to run the samples. The master and slave VM's will then exchange messages over a simulated serialport via a host named pipe. The great thing about using Netty to interact with a serial port is that you can easily integrate [Kryo](https://code.google.com/p/kryo/), [MsgPack](http://msgpack.org/), or JSON into your pipeline to handle serialization and pass arbitrary POJOS or commands over a serial port without writing a bunch of one-off parsing logic typically seen when dealing with serial ports.

Fork and enjoy.
