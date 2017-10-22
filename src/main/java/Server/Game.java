package Server;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Game {
    public enum TRUMP_COLOUR{
        DIAMONDS, HEARTS, CLUBS, SPADES,
    }
    private Team[] teams = new Team[2];
    private ArrayList<Card> Deck = new ArrayList<Card>();
    private String[] cards_name = {"7", "8", "9", "10","J", "Q", "K", "A"};
    private static int nbTeams = 2;

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
        for (int i = 0; i < nbTeams; i++) {
            if (teams[i].getNbPlayers() <= 1) {
                teams[i].addPlayer(player);
                break;
            }
        }
    }

    public Team getTeam(int idx){
        return (teams[idx]);
    }
}
