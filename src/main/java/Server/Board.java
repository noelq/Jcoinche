package Server;

import java.util.ArrayList;
import java.util.List;

public class Board {
    List<Card> cards = new ArrayList<Card>();
    List<Player> players = new ArrayList<Player>();
    int masterCardIdx;

    public boolean putCard(Card card, Player player){
        cards.add(card);
        players.add(player);

        return true;
    }

    public Card getMasterCard(){
        return cards.get(masterCardIdx);
    }
}
