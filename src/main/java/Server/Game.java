package Server;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Game {
    public enum TRUMP_COLOUR{
        DIAMONDS, HEARTS, CLUBS, SPADES,
    }
    private String[] cards_name = {"7", "8", "9", "10","J", "Q", "K", "A"};
    private List<Card> Deck = new ArrayList<Card>();

    private Team[] teams = new Team[2];
    //private Player players = new players[4];

    private int currentPlayerId = 0;
    private int firstPlayer = 1;
    private int nbPlayers = 0;

    public Game(){
        for (int i = 0; i < nbTeams; i++){
            teams[i] = new Team();
        }
        this.setDeck();
        /*for (int i = 0; i < 2; i++){
            System.out.println(teams[i].getPlayer(0).getId());
            System.out.println(teams[i].getPlayer(0).getTeam_id());
            System.out.println(teams[i].getPlayer(1).getId());
            System.out.println(teams[i].getPlayer(1).getTeam_id());
        }*/
    }

    public void start(){
        System.out.println("La on start la game");
        List<Player> players = new ArrayList<Player>();
        //bout de code pour avoir des players propres dans game, sans les stocker 2 fois dans team/game
        players.add(getTeam(0).getPlayer(0));
        players.add(getTeam(1).getPlayer(0));
        players.add(getTeam(0).getPlayer(1));
        players.add(getTeam(1).getPlayer(1));

        while (teams[0].getScore() < 2000 && teams[1].getScore() < 2000){

        }
    }

    public void setDeck() {
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 8; j++){
                //tmp_card = new Card(cards_name[j], Card.COLOUR.values()[i], 7 + j);
                Deck.add(new Card(cards_name[j], Card.COLOUR.values()[i], 7 + j));
            }
        }
        Collections.shuffle(Deck);
        for (int k = 0; k < 32; k++){
            System.out.print(Deck.get(k).getValue());
            System.out.print(Deck.get(k).getColour());
            System.out.println(Deck.get(k).getDisplay_string());
        }
    }

    public void addPlayer(Player player) {

        for (int i = 0; i < 2; i++) {
            if (teams[i].getNbPlayers() <= 1) {
                teams[i].addPlayer(player);
                break;
            }
        }
    }

    public void scanMsg(String msg){
        System.out.println("C'est à " + currentPlayerId + "de jouer\n");
    }

    public Team getTeam(int idx){
        return (teams[idx]);
    }

    public int getCurrentPlayerId(){
        return (currentPlayerId);
    }
    public Player getPlayerById(int id){
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 2; j++){
                if (teams[i].getPlayer(j).getId() == id)
                    return (teams[i].getPlayer(j));
            }
        }
        return (null);
    }
}
