package Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.ArrayList;

import Server.Server;
import static Server.ServerHandler.SERVER_STATE.START;
import static Server.ServerHandler.SERVER_STATE.WAIT;


public class ServerHandler extends ChannelInboundMessageHandlerAdapter<String> {
    private static final ChannelGroup channels = new DefaultChannelGroup();


    public enum SERVER_STATE{
        WAIT, START, GAME,
    }
    private SERVER_STATE server_state;
    private static int player_cpt = 0;

    public ServerHandler() {
        //player_cpt = 0;
        server_state = WAIT;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.write(" [SERVER] - " + incoming.remoteAddress() + "has joined!\n");
        }
        channels.add(ctx.channel());
        System.out.println(player_cpt);
        //setPlayer_cpt(getPlayer_cpt() + 1);
        player_cpt += 1;
        if (player_cpt == 4) {
            System.out.println("4 CLIENTS");
            server_state = START;
            new Game(channels);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.write(" [SERVER] - " + incoming.remoteAddress() + "has left!\n");
        }
        channels.remove(ctx.channel());
        //player_cpt -= 1;

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
