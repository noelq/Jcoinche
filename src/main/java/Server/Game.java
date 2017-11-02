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

    private int callingTeam = -1;
    private int defendingTeam = -1;
    private int callValue = -1;
    private Card.COLOUR callColor;
    private int passStack = 0;

    private Team[] teams = new Team[2];
    private List<Player> players = new ArrayList<Player>();

    private int currentPlayerIdx = 0;
    private int turnState = 0;
    private int nbTurns = 0;
    private int nbRounds = 0;
    private int firstPlayerIdx = 0;

    private WAIT_FOR wait = WAIT_FOR.NOTHING;

    public Game(){
        for (int i = 0; i < 2; i++){
            teams[i] = new Team();
        }
        this.setDeck();
    }

    public void start() {
        players = getPlayers();

        board = new Board();
        this.distribution();
        players.get(0).showCards();
        players.get(1).showCards();
        players.get(2).showCards();
        players.get(3).showCards();
        this.askPlayerToCall(getCurrentPlayer());
    }

    private void askPlayerToCall(Player player){
        player.sendMsg("It is your turn to make a call, [contract value] [trump color] (ex: 120 H)");
        wait = WAIT_FOR.CALL;
    }

    private void askPlayerToPlay(Player player){
        player.sendMsg("It is your turn to play a card, [card sign][card color] (ex: 10H)");
        wait = WAIT_FOR.CARD;
    }

    private void setDeck() {
        for (int i = 0; i < 4; i++){
                Deck.add(new Card(cards_name[0] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 0, 0));
                Deck.add(new Card(cards_name[1] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 0, 0));
                Deck.add(new Card(cards_name[2] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 0, 14));
                Deck.add(new Card(cards_name[3] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 10, 10));
                Deck.add(new Card(cards_name[4] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 2, 20));
                Deck.add(new Card(cards_name[5] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 3, 3));
                Deck.add(new Card(cards_name[6] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 4, 4));
                Deck.add(new Card(cards_name[7] + Card.COLOUR.values()[i].toString().charAt(0), Card.COLOUR.values()[i], 11, 11));

            }
        Collections.shuffle(Deck);
    }

    public void addPlayer(Player player) {

        for (int i = 0; i < 2; i++) {
            if (teams[i].getNbPlayers() <= 1) {
                teams[i].addPlayer(player);
                break;
            }
        }
    }

    private void validateTurn(){
        board.countPoints(teams, nbTurns);
        groupMsg("Team 1 : " + teams[0].getRoundScore() + " points  " + "Team 2 : " + teams[1].getRoundScore() + "points");
        board.cleanBoard(Deck);
        turnState = 1;
        nbTurns++;
        firstPlayerIdx++;
        if (firstPlayerIdx == 4)
            firstPlayerIdx = 0;
        currentPlayerIdx = firstPlayerIdx;
        if (nbTurns > 8)
            validateRound();
        else {
            groupMsg("current player : " + (currentPlayerIdx + 1));
            askPlayerToPlay(getCurrentPlayer());
        }
    }

    private void validateRound(){
        Team cTeam = teams[callingTeam];
        Team dTeam = teams[defendingTeam];

        if (cTeam.getRoundScore() >= cTeam.getContractValue()){
            cTeam.addScore(cTeam.getRoundScore() + cTeam.getContractValue());
            dTeam.addScore(dTeam.getRoundScore());
            groupMsg("Team " + (callingTeam + 1) + " completed the contract and won " + cTeam.getScore() + " points.");
            groupMsg("Team " + (defendingTeam + 1) + " failed to stop the other team and won " + dTeam.getScore() + " points.");
        }
        else {
            dTeam.addScore(162 + cTeam.getContractValue());
            groupMsg("Team " + (callingTeam + 1) + " prevented the other team from completing the contract and won " + dTeam.getScore() + " points");
            groupMsg("Team " + (defendingTeam + 1) + " failed to complete the contract and won " + cTeam.getScore() + " points");
        }
        for (Team team : teams){
            team.reset();
        }
        nbRounds++;
        if (cTeam.getScore() >= 701 && dTeam.getScore() >= 701){
            if (cTeam.getScore() == dTeam.getScore())
                groupMsg("Draw with " + cTeam.getScore() + "points");
            else if (cTeam.getScore() > dTeam.getScore())
                groupMsg("Team " + callingTeam + " won the game with " + cTeam.getScore());
            else
                groupMsg("Team " + defendingTeam + " won the game with " + dTeam.getScore());
            groupMsg("Reset the client to play another game");
        }
        else if (cTeam.getScore() >= 701){
            groupMsg("Team " + callingTeam + " won the game with " + cTeam.getScore());
            groupMsg("Reset the client to play another game");
        }
        else if (dTeam.getScore() >= 701){
            groupMsg("Team " + defendingTeam + " won the game with " + dTeam.getScore());
            groupMsg("Reset the client to play another game");
        }
        callingTeam = -1;
        defendingTeam = -1;
        callValue = -1;
        passStack = 0;
        currentPlayerIdx = 0;
        turnState = 0;
        nbTurns = 0;
        firstPlayerIdx = 0;
        wait = WAIT_FOR.NOTHING;
        Collections.shuffle(Deck);
        distribution();
        askPlayerToCall(getCurrentPlayer());
    }

    public void scanMsg(String msg) {
        if (wait == WAIT_FOR.CALL) {
            scanCall(msg);
        }
        else if (wait == WAIT_FOR.CARD){
            scanCard(msg);
        }
    }

    private void scanCard(String msg){
        if (!board.putCard(msg, getCurrentPlayer())){
            getCurrentPlayer().sendMsg("You can't play this card");
            return ;
        }

        groupMsg("Player " + getCurrentPlayer().getId() + " played " + msg);
        if (turnState < 4) {
            nextPlayer();
            askPlayerToPlay(getCurrentPlayer());
        }
        else
            validateTurn();
    }

    private void scanCall(String msg) {
        String tokens[] = msg.split(" ");

        if (tokens[0].equals("pass")){
            passStack++;
            groupMsg("Player " + getCurrentPlayer().getId() + " passed");
            nextPlayer();
            if (callValue != 0 && passStack == 3){
                turnState = 1;
                nbTurns = 1;
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
                    callingTeam = getCurrentPlayer().getTeam_id();
                    defendingTeam = (callingTeam + 1) % 2;
                    teams[callingTeam].setContractValue(callValue);
                    nextPlayer();
                    askPlayerToCall(getCurrentPlayer()); }
                else {
                    getCurrentPlayer().sendMsg("Wrong Call, try again");
                }
            } catch (Exception e) {
                getCurrentPlayer().sendMsg("Wrong Call");
            }
        }
    }

    private boolean checkCall(String str){
        int tmpCall;

        if (isNumber(str) && (tmpCall = Integer.parseInt(str)) % 10 == 0 && tmpCall >= 80 && tmpCall <= 160 && tmpCall > callValue) {
            passStack = 0;
            callValue = Integer.parseInt(str);
            return true;
        }
        else{
            getCurrentPlayer().sendMsg("Wrong call, try again");
            return false;
        }
    }

    private boolean checkColor(String str) {
        if (str.equals("H") || str.equals("h") || str.equals("D") || str.equals("d") || str.equals("S") || str.equals("s") || str.equals("C") || str.equals("c")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkCallColor(String str){
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

    private static boolean isNumber(String input){
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

    public WAIT_FOR getWait() {
        return wait;
    }

    private Player getPlayerById(int id) {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (teams[i].getPlayer(j).getId() == id)
                    return (teams[i].getPlayer(j));
            }
        }
        return (null);
    }

    private void distribution() {
        List<Player> my_players = getPlayers();
        for (int i = 0; i < 4; i++) {
            List<Card> cards = new ArrayList<Card>();
            for (int j = 0; j < 8; j++) {
                my_players.get(i).addCard(Deck.get(0));
                Deck.remove(0);
            }
        }
    }

    private List<Player> getPlayers(){
        List<Player> players =  new ArrayList<Player>();
        for (int i = 0; i < 2; i++){
            players.add(teams[i].getPlayer(0));
            players.add(teams[i].getPlayer(1));
        }
        return players;
    }

    private void groupMsg(String msg){
        for (Player player: getPlayers()) {
            player.getChannel().writeAndFlush(msg + "\n");
        }
    }

    public Player getCurrentPlayer(){
        return (players.get(currentPlayerIdx));
    }

    private void nextPlayer() {
        currentPlayerIdx++;
        turnState++;
        if (currentPlayerIdx == 4){
            currentPlayerIdx = 0;
        }
    }

    public Player getPlayerbyChannel(Channel channel){
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (teams[i].getPlayer(j).getChannel() == channel)
                    return (teams[i].getPlayer(j));
            }
        }
        return (null);
    }
}
