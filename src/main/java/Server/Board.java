package Server;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private List<Card> cards = new ArrayList<Card>();
    private List<Player> players = new ArrayList<Player>();
    private int masterCardIdx;
    private Card.COLOUR current_trump;
    private Card.COLOUR colour_set;
    private int nb_cards = 0;

    public boolean putCard(String display_string, Player player){
        Card card_tmp;
         if ((card_tmp = player.getCard(display_string)) == null) {
             player.sendMsg("pas la carte");
             return false;
         }
         if (nb_cards == 0){
             player.playCard(display_string);
             cards.add(card_tmp);
             players.add(player);
             masterCardIdx = nb_cards;
             colour_set = card_tmp.getColour();
             nb_cards += 1;
         }
        else {
             List<Card> playable_cards;
             playable_cards = getPlayableCards(player);
             System.out.println(playable_cards.get(0).getDisplay_string());
             if (!playable_cards.contains(card_tmp)) {
                 player.sendMsg("le joueur n'a pas joué la carte de bonne couleur");
                 return false;
             }
             player.playCard(display_string);
             cards.add(card_tmp);
             players.add(player);
             nb_cards += 1;
         }
        masterCardIdx = getMasterPlayerIdx();
        return true;
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

    private int getMasterPlayerIdx(){
        int i = 0;
        int value_max = 0;
        int trump_value_max = 0;
        int value_max_idx = 0;
        int trump_value_max_idx = 0;
        for (Card card_tmp : cards){
            if (card_tmp.getColour().equals(current_trump)){
                if (card_tmp.getTrump_value() > trump_value_max) {
                    trump_value_max = card_tmp.getTrump_value();
                    trump_value_max_idx = i;
                }
            }
            else {
                if (card_tmp.getValue() > value_max && card_tmp.getColour().equals(colour_set)){
                    value_max = card_tmp.getValue();
                    value_max_idx = i;
                }
            }
            i++;
        }
        if (trump_value_max == 0)
            return value_max_idx;
        return trump_value_max_idx;
    }

    public List<Card> getPlayableCards(Player player){
        List<Card> playable_cards = new ArrayList<Card>();
        for (Card card : player.getCards()){
            if (card.getColour().equals(colour_set)){
                playable_cards.add(card);
            }
        }
        if (playable_cards.isEmpty()) {
            for (Card card : player.getCards()){
                if (card.getColour().equals(current_trump))
                    playable_cards.add(card);
            }
        }
        if (playable_cards.isEmpty())
            return player.getCards();
        return playable_cards;
    }

    public Card getMasterCard(){
        return cards.get(masterCardIdx);
    }

    public void countPoints(Team[] teams, int nbTurns){
        int points_earned = 0;
        if (nbTurns == 8)
            points_earned = 10;
        for (Card card : cards){
            if (card.getColour().equals(current_trump))
                points_earned += card.getTrump_value();
            else
                points_earned += card.getValue();
        }
        teams[players.get(masterCardIdx).getTeam_id()].addRoundScore(points_earned);
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

    public void cleanBoard(List<Card> Deck){
        nb_cards = 0;
        masterCardIdx = 0;
        players.clear();
        for (int i = 0; i < 4; i++){
            Deck.add(cards.get(0));
            cards.remove(0);
        }
    }
}
