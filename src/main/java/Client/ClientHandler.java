package Client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {


    public void channelRead0(ChannelHandlerContext arg0, String arg1) throws  Exception{
        System.out.println(arg1);
    }
}
