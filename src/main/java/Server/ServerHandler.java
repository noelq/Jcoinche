package Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static Game game;

    public enum SERVER_STATE{
        WAIT, GAME,
    }
    private SERVER_STATE server_state;
    private static int player_cpt = 0;

    public ServerHandler() {
        server_state = SERVER_STATE.WAIT;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        if (player_cpt == 0){
            game = new Game();
        }
        if (server_state == SERVER_STATE.GAME){
            System.out.println("New client waiting");
            incoming.writeAndFlush(" [SERVER] - Game in progress, please wait\n");
        }
        if (player_cpt != 4){
            for (Channel channel : channels) {
                channel.writeAndFlush(" [SERVER] - " + incoming.remoteAddress() + "has joined!\n");
            }
            Player player_tmp = new Player();
            player_tmp.setChannel(incoming);
            player_tmp.setId(player_cpt + 1);
            player_tmp.setTeam_id(player_cpt % 2);
            game.addPlayer(player_tmp);
            channels.add(ctx.channel());
            player_cpt += 1;
            if (player_cpt == 4) {
                game.start();
                server_state = SERVER_STATE.GAME;
            }
        }
    }



    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception{
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            channel.writeAndFlush(" [SERVER] - " + incoming.remoteAddress() + "has left!\n");
        }
        channels.remove(ctx.channel());
    }


    public void channelRead0(ChannelHandlerContext arg0, String message) throws Exception{
        Channel incoming = arg0.channel();

        if (message.toLowerCase().equals("show"))
            game.getPlayerbyChannel(incoming).showCards();
        else if (game.getWait().ordinal() >= 0 && game.getCurrentPlayer().getChannel() == incoming){
            game.scanMsg(message);
        }
        else {
            incoming.writeAndFlush("It is player " + game.getCurrentPlayer().getId() + "'s turn\n");
        }
    }
}
