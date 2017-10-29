package Server;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player {
    private int id;
    private Channel channel;
    private int team_id;
    private List<Card> cards = new ArrayList<Card>();


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

    public void sendMsg(String msg){
        channel.writeAndFlush(msg + "\n");
    }

    public void showCards() {
        Card tmpCard;
        String str = "";
        for (Card card : cards) {
            str += card.getDisplay_string();
            str += " ";
        }
        sendMsg(str);
    }

    public Card getCard(String cardName){
        Card tmpCard;

        for (Card card : cards){
            if (card.getDisplay_string().equals(cardName)){
                tmpCard = card;
                return (tmpCard);
            }
        }
        return null;
    }

    public Card playCard(String cardName){
        Card tmpCard;

        for (Card card : cards){
            if (card.getDisplay_string().equals(cardName)){
                tmpCard = card;
                cards.remove(card);
                return (tmpCard);
            }
        }
        return (null);
    }

    public List<Card> getPlayableCards(Card.COLOUR CurrentColour, List<Card> cardsonboard){
        List<Card> playable_cards = new ArrayList<Card>();

        for (Card card : cards){
            if (card.getColour().equals(CurrentColour)){
                playable_cards.add(card);
            }
        }
        return playable_cards;
    }
}
