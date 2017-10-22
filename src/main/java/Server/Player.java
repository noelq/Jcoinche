package Server;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private int id;
    private Channel channel;
    private int team_id;
    public List<Card> cards = new ArrayList<Card>();


    public void setId(int id) {
        this.id = id;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public Channel getChannel() {
        return channel;
    }

    public int getId() {
        return id;
    }

    public int getTeam_id() {
        return team_id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(int value, Card.COLOUR colour){
        for (Iterator<Card> iter = cards.listIterator();  iter.hasNext();){
            Card card = iter.next();
            if (card.getColour() == colour && card.getValue() == value){
                iter.remove();
            }
        }
    }
}
