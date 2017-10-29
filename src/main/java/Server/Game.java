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

    private Board board;
    private String[] cards_name = {"7", "8", "9", "10","J", "Q", "K", "A"};
    private List<Card> Deck = new ArrayList<Card>();

    private int callValue = -1;
    private Card.COLOUR callColor;
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

        /*for (int i = 0; i < 2; i++){
            System.out.println(teams[i].getPlayer(0).getId());
            System.out.println(teams[i].getPlayer(0).getTeam_id());
            System.out.println(teams[i].getPlayer(1).getId());
            System.out.println(teams[i].getPlayer(1).getTeam_id());
        }*/
    }

    public void start() {
        System.out.println("La on start la game");
        players = getPlayers();

        board = new Board();
        this.distribution();
        players.get(0).showCards();
        players.get(1).showCards();
        players.get(2).showCards();
        players.get(3).showCards();
        System.out.println("on va ask au player");
        this.askPlayerToCall(getCurrentPlayer());
    }

    public void askPlayerToCall(Player player){
        player.sendMsg("It is your turn to make a call");
        System.out.println("je lui ai écrit");
        wait = WAIT_FOR.CALL;
        System.out.println("j'attend son call");
    }

    public void askPlayerToPlay(Player player){
        player.sendMsg("It is your turn to play");
        wait = WAIT_FOR.CARD;
    }

    public void setDeck() {
        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 8; j++){
                //tmp_card = new Card(cards_name[j], Card.COLOUR.values()[i], 7 + j);
                Deck.add(new Card(cards_name[j] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 7 + j));
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
        System.out.println(msg);
        if (wait == WAIT_FOR.CALL) {
            scanCall(msg);
        }
        else if (wait == WAIT_FOR.CARD){
            if (msg.equals("SHOW")) {
                getCurrentPlayer().showCards();
                return;
            }
            if (!board.putCard(msg, getCurrentPlayer())){
                getCurrentPlayer().sendMsg("can't play this card");
                return ;
            }

            groupMsg("Player " + getCurrentPlayer().getId() + " played " + msg);
            nextPlayer();
            askPlayerToPlay(getCurrentPlayer());

           // scanCard(msg);
        }
    }

    public void scanCard(String msg){
        Player player = getCurrentPlayer();
        Card cardToplay;

        if ((cardToplay = player.playCard(msg)) == null) {
            player.sendMsg("You don't have this card or you used the wrong format");
            return;
        }
        if (!isCardPlayable(cardToplay, player.getCards())){ //check couleur
            player.sendMsg("You can't play this card");
            return ;
        }
       /* if (!board.putCard(cardToplay, player)){
            player.sendMsg("You can't play this card");
            return ;
        }*/
        groupMsg("Player " + player.getId() + " played " + cardToplay.getDisplay_string());
        if (turnState < 4)
            nextPlayer();
        else
            validateTurn();
    }

    private boolean isCardPlayable(Card cardToPlay, List<Card> cards){
        if (cardToPlay.getValue() > board.getMasterCard().getValue()) {
            return true;
        }
        return false;
    }

    public void scanCall(String msg) {
        String tokens[] = msg.split(" ");

        if (tokens[0].equals("pass")){
            passStack++;
            groupMsg("Player " + getCurrentPlayer().getId() + " passed");
            nextPlayer();
            if (passStack == 3){
                board.setMasterCardIdx(getCurrentPlayer().getId());
                board.setCurrent_trump(callColor);
                askPlayerToPlay(getCurrentPlayer());
            }
            else {
                askPlayerToCall(getCurrentPlayer());
            }
        }
        else {
            try {
                if (checkCall(tokens[0]) && checkCallColor(tokens[1])) {
                    passStack = 0;
                    getCurrentPlayer().sendMsg("Call accepted");
                    groupMsg("Player " + getCurrentPlayer().getId() + " called " + callValue + " " + callColor);
                    nextPlayer();
                    askPlayerToCall(getCurrentPlayer());
                } else {
                    getCurrentPlayer().sendMsg("Wrong Call, try again");
                }
            } catch (Exception e) {
                getCurrentPlayer().sendMsg("Wrong Call");
            }
        }
    }

    public boolean checkCall(String str){
        int tmpCall;

        if (isNumber(str) && (tmpCall = Integer.parseInt(str)) % 10 == 0 && tmpCall >= 80 && tmpCall <= 160 && tmpCall >= callValue) {
            passStack = 0;
            System.out.println("le call est bon");
            callValue = Integer.parseInt(str);
            return true;
        }
        else{
            System.out.println("Wrong call");
            getCurrentPlayer().sendMsg("Wrong call, try again");
            return false;
        }
    }

    public boolean checkColor(String str) {
        if (str.equals("H") || str.equals("h") || str.equals("D") || str.equals("d") || str.equals("S") || str.equals("s") || str.equals("C") || str.equals("c")) {
            System.out.println("bonne colour");
            return true;
        } else {
            System.out.println("mauvaise colour");
            return false;
        }
    }

    public boolean checkCallColor(String str){
        if (str.equals("H") || str.equals("h"))
            callColor = Card.COLOUR.HEARTS;
        else if (str.equals("D") || str.equals("d"))
            callColor = Card.COLOUR.DIAMONDS;
        else if (str.equals("S") || str.equals("s"))
            callColor = Card.COLOUR.SPADES;
        else if (str.equals("C") || str.equals("c"))
            callColor = Card.COLOUR.CLUBS;
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
                my_players.get(i).addCard(Deck.get(0));
                Deck.remove(0);
            }
        }
    }

    public List<Player> getPlayers(){
        List<Player> players =  new ArrayList<Player>();
        System.out.println("player");
        for (int i = 0; i < 2; i++){
            System.out.println(teams[i].getPlayer(0));
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
