package Server;

//import com.sun.deploy.util.JarUtil;
//import com.sun.deploy.util.StringUtils;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Game {

    public enum WAIT_FOR{
        NOTHING, CALL, CARD
    }

    public enum TRUMP_COLOUR{
        DIAMONDS, HEARTS, CLUBS, SPADES,
    }

    private Board board;
    private String[] cards_name = {"7", "8", "9", "10","J", "Q", "K", "A"};
    private List<Card> Deck = new ArrayList<Card>();

    private int callValue = -1;
    private TRUMP_COLOUR callColor;
    private int passStack = 0;

    private Team[] teams = new Team[2];
    private List<Player> players = new ArrayList<Player>();

    private int currentPlayerIdx = 0;
    private int turnState = 1;
    private int nbCycle = 0;
    private int firstPlayerIdx = 0;

    private WAIT_FOR wait = WAIT_FOR.NOTHING;

    public Game(){
        for (int i = 0; i < 2; i++){
            teams[i] = new Team();
        }
        System.out.println("je suis passé");
        this.setDeck();
        players = getPlayers();

        /*for (int i = 0; i < 2; i++){
            System.out.println(teams[i].getPlayer(0).getId());
            System.out.println(teams[i].getPlayer(0).getTeam_id());
            System.out.println(teams[i].getPlayer(1).getId());
            System.out.println(teams[i].getPlayer(1).getTeam_id());
        }*/
    }

    public void start() {
        System.out.println("La on start la game");

        board = new Board();
        this.distribution();
        this.askPlayerToCall(getCurrentPlayer());
    }

    public void askPlayerToCall(Player player){
        player.sendMsg("It is your turn to make a call");
        System.out.println("je lui ai écrit");
        wait = WAIT_FOR.CALL;
        System.out.println("j'attend son call");
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

    public void validateTurn(){
        firstPlayerIdx++;
        if (firstPlayerIdx == 4)
            firstPlayerIdx = 0;
        currentPlayerIdx = firstPlayerIdx;

    }

    public void scanMsg(String msg) {
        System.out.println("Le bon joueur me parle");
        if (wait == WAIT_FOR.CALL) {
            scanCall(msg);
        }
        else if (wait == WAIT_FOR.CARD){
            scanCard(msg);
        }
    }

    public void scanCard(String msg){
        Player player = getCurrentPlayer();
        Card cardToplay;

        if ((cardToplay = player.playCard(msg)) == null) {
            player.sendMsg("You don't have this card or you used the wrong format");
            return;
        }
        if (!isCardPlayable(cardToplay, player.getCards())){
            player.sendMsg("You can't play this card");
            return ;
        }
        if (!board.putCard(cardToplay, player)){
            player.sendMsg("You can't play this card");
            return ;
        }
        groupMsg("Player " + player.getId() + " played " + cardToplay.getDisplay_string());
        if (turnState < 4)
            nextPlayer();
        else
            validateTurn();
    }

    private boolean isCardPlayable(Card cardToPlay, List<Card> cards){
        if (cardToPlay.getValue() > board.getMasterCard().getValue())
        return true;
    }

    public void scanCall(String msg) {
        String tokens[] = msg.split(" ");

        if (tokens[0].equals("pass")){
            passStack++;
            groupMsg("Player " + getCurrentPlayer().getId();
            if (passStack == 3){
                nextPlayer();
                getCurrentPlayer();
            }
        }
        if (checkCall(tokens[0]) && checkColor(tokens[1])) {
            getCurrentPlayer().sendMsg("Call accepted");
            groupMsg("Player " + getCurrentPlayer().getId() + " called " + callValue + " " + callColor);
        }
    }

    public boolean checkCall(String str){
        int tmpCall;

        if (isNumber(str) && (tmpCall = Integer.parseInt(str)) % 10 == 0 && tmpCall >= 80 && tmpCall <= 160 && tmpCall >= callValue) {
            passStack = 0;
            callValue = Integer.parseInt(str);
            return true;
        }
        else{
            System.out.println("Wrong call");
            getCurrentPlayer().sendMsg("Wrong call, try again");
            return false;
        }
    }

    public boolean checkCallColor(String str){
        if (str.equals("H"))
            callColor = TRUMP_COLOUR.HEARTS;
        else if (str.equals("D"))
            callColor = TRUMP_COLOUR.DIAMONDS;
        else if (str.equals("S"))
            callColor = TRUMP_COLOUR.SPADES;
        else if (str.equals("C"))
            callColor = TRUMP_COLOUR.CLUBS;
        else
            return false;
        return true;
    }

    public static boolean isNumber(String input){
        boolean parsable = true;
        try{
            Integer.parseInt(input);
        }catch(NumberFormatException e){
            parsable = false;
        }
        return parsable;
    }

    public Team getTeam(int idx){
        return (teams[idx]);
    }

    /*public void setMsgAnswered(boolean msgAnswered){
        this.msgAnswered = msgAnswered;
    }*/

    public WAIT_FOR getWait() {
        return wait;
    }

    /*public int getCurrentPlayerId(){
        return (currentPlayerId);
    }*/

    public Player getPlayerById(int id) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (teams[i].getPlayer(j).getId() == id)
                    return (teams[i].getPlayer(j));
            }
        }
        return (null);
    }

    public void distribution() {
        List<Player> my_players = getPlayers();
        for (int i = 0; i < 4; i++) {
            List<Card> cards = new ArrayList<Card>();
            for (int j = 0; j < 8; j++) {
                my_players.get(i).addCard(Deck.get(j + (j * i)));
            }
        }
    }

    public List<Player> getPlayers(){
        List<Player> players =  new ArrayList<Player>();
        for (int i = 0; i < 2; i++){
            players.add(teams[i].getPlayer(0));
            players.add(teams[i].getPlayer(1));
        }
        return players;
    }

    public void groupMsg(String msg){
        for (Player player: getPlayers()) {
            player.getChannel().writeAndFlush(msg + "\n");
        }
    }

    public Player getCurrentPlayer(){
        return (players.get(currentPlayerIdx));
    }

    public void nextPlayer() {
        currentPlayerIdx++;
        if (currentPlayerIdx == 4){
            currentPlayerIdx = 0;
            nbCycle++;
        }
    }
}
