package Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import static Server.ServerHandler.SERVER_STATE.START;
import static Server.ServerHandler.SERVER_STATE.WAIT;
import static Server.ServerHandler.SERVER_STATE.GAME;

public class ServerHandler extends ChannelInboundMessageHandlerAdapter<String> {
    private static final ChannelGroup channels = new DefaultChannelGroup();
    private int player_cpt = 0;
    public enum SERVER_STATE{
      WAIT, START, GAME,
    }
    SERVER_STATE server_state;

    public ServerHandler(){
        server_state = WAIT;

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.write(" [SERVER] - " + incoming.remoteAddress() + "has joined!\n");
        }
        channels.add(ctx.channel());
        player_cpt += 1;
        if (player_cpt == 4) {
            server_state = START;
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.write(" [SERVER] - " + incoming.remoteAddress() + "has left!\n");
        }
        channels.remove(ctx.channel());
        player_cpt -= 1;

    }


    public void messageReceived(ChannelHandlerContext arg0, String message) throws Exception{
        Channel incoming = arg0.channel();
        for (Channel channel: channels) {
            if (channel != incoming){
                channel.write("[" + incoming.remoteAddress() + "]" + message + "\n");
            }
        }

    }
}
