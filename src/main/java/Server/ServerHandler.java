package Server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;

import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends ChannelInboundMessageHandlerAdapter<String> {
    private static final ChannelGroup channels = new DefaultChannelGroup();
    private static Game game;

    public enum SERVER_STATE{
        WAIT, GAME,
    }
    private SERVER_STATE server_state;
    private static int player_cpt = 0;
    public Team[] teams = new Team[2];

    public ServerHandler() {
        //player_cpt = 0;
        server_state = SERVER_STATE.WAIT;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        if (player_cpt == 0){
            game = new Game();
        }
        if (player_cpt != 4){
            for (Channel channel : channels) {
                channel.write(" [SERVER] - " + incoming.remoteAddress() + "has joined!\n");
            }
            Player player_tmp = new Player();
            player_tmp.setChannel(incoming);
            player_tmp.setId(player_cpt + 1);
            player_tmp.setTeam_id(player_cpt % 2);
            System.out.println("player cpt: " + player_cpt);
            game.addPlayer(player_tmp);
            channels.add(ctx.channel());
            player_cpt += 1;
            if (player_cpt == 4) {
                game.start();
                server_state = SERVER_STATE.GAME;
            }
        }
        if (server_state == SERVER_STATE.GAME){
            System.out.println("New client waiting");
            incoming.write(" [SERVER] - Game in progress, please wait\n");
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

        if (game.getWait().ordinal() >= 0 && game.getCurrentPlayerId() != 0 && game.getPlayerById(game.getCurrentPlayerId()).getChannel() == incoming){
            game.scanMsg(message);
        }
        else {
            incoming.write("It is player " + game.getCurrentPlayerId() + "'s turn\n");
        }
        /*for (Channel channel: channels) {
            if (channel != incoming){
                channel.write("[" + incoming.remoteAddress() + "]" + message + "\n");
            }
        }*/

    }

}
