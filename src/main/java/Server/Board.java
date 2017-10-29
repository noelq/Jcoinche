package Server;

import java.util.ArrayList;
import java.util.List;

public class Board {
    List<Card> cards = new ArrayList<Card>();
    List<Player> players = new ArrayList<Player>();
    int masterCardIdx;
    Card.COLOUR current_trump;
    int nb_cards = 0;

    public boolean putCard(String display_string, Player player){
        Card card_tmp;
         if ((card_tmp = player.getCard(display_string)) == null) {
             player.sendMsg("pas la carte");
             return false;
         }
       /* else if (nb_cards == 0){
            player.playCard(display_string);
             cards.add(card_tmp);
             players.add(player);
             return true;
        }*/
        else {
             List<Card> playable_cards;
             playable_cards = player.getPlayableCards(current_trump, cards);
             player.sendMsg("size " + playable_cards.size());
             if (!(playable_cards = player.getPlayableCards(current_trump, cards)).isEmpty()) {
                 player.sendMsg("le jouer a des cartes de bonne couleur");
                 if (!playable_cards.contains(card_tmp)) {
                     player.sendMsg("le joueur n'a pas joué la carte de bonne couleur");
                     return false;
                 }
             }
             player.playCard(display_string);
             cards.add(card_tmp);
             players.add(player);
             if (nb_cards == 0)
                 masterCardIdx = player.getId();
             else if (isBestCard(card_tmp))
                 masterCardIdx = player.getId();
             player.sendMsg("masterCardIdx : " + Integer.toString(masterCardIdx));
             return true;
         }
    }

    public boolean isBestCard(Card my_card){
        for (Card card : cards){
            if (card.getColour().equals(current_trump) && (!my_card.getColour().equals(current_trump)))
                return false;
            else {
                if (card.getValue() > my_card.getValue())
                    return false;
            }
        }
        return true;
    }

    public Card getMasterCard(){
        return cards.get(masterCardIdx);
    }

    public void setMasterCardIdx(int idx){
        masterCardIdx = idx;
    }

    public void setCurrent_trump(Card.COLOUR current_trump) {
        this.current_trump = current_trump;
    }

    public Card.COLOUR getCurrent_trump() {
        return current_trump;
    }
}
