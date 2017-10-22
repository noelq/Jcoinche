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
    public ArrayList<Card> Deck = new ArrayList<Card>();
    public String[] cards_name = {"7", "8", "9", "10","J", "Q", "K", "A"};

    public Game(Team[] teams){
        this.setDeck();
        for (int i = 0; i < 2; i++){
            System.out.println(teams[i].getPlayer(0).getId());
            System.out.println(teams[i].getPlayer(0).getTeam_id());
            System.out.println(teams[i].getPlayer(1).getId());
            System.out.println(teams[i].getPlayer(1).getTeam_id());
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

}
