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
    private String[] cards_name = {"7", "8", "9", "10","J", "Q", "K", "A"};
    private List<Card> Deck = new ArrayList<Card>();
    private int callValue = -1;

    private Team[] teams = new Team[2];
    //private Player players = new players[4];

    private int currentPlayerId = 0;
    private int firstPlayer = 1;
    private int nbPlayers = 0;
    private WAIT_FOR wait = WAIT_FOR.NOTHING;
    private boolean msgAnswered;

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

    public void start(){
        System.out.println("La on start la game");
        List<Player> players = new ArrayList<Player>();
        players = getPlayers();

        while (teams[0].getScore() < 2000 && teams[1].getScore() < 2000){
            this.distribution();
            this.calls();
            /*while (){

            }*/
        }
//        System.out.println(teams[0].getPlayer(0).getCards().get(0).getDisplay_string() + teams[0].getPlayer(0).getCards().get(0).getColour());
    }

    public void calls(){
        List<Player> players =  new ArrayList<Player>();
        players = getPlayers();
        for (int i = 0; i < 4; i++){
            players.get(i).getChannel().write("It is your turn to make a call");
            System.out.println("je lui ai écrit");
            players.get(i).getChannel().flush();
            msgAnswered = false;
            wait = WAIT_FOR.CALL;
            currentPlayerId = players.get(i).getId();
            while (msgAnswered == false)
                players.get(i).getChannel().flush();
            currentPlayerId = 0;
            wait = WAIT_FOR.NOTHING;
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

    public void addPlayer(Player player) {

        for (int i = 0; i < 2; i++) {
            if (teams[i].getNbPlayers() <= 1) {
                teams[i].addPlayer(player);
                break;
            }
        }
    }

    public void scanMsg(String msg){
        System.out.println("Le bon joueur me parle");
        int nb;
        if (wait == WAIT_FOR.CALL){
            if (msg == "capot"){
                System.out.println("on verra pls tard");
            }
            else if (isNumber(msg) && (nb = Integer.parseInt(msg)) % 10 == 0 && nb >= 80 && nb <= 160 && nb >= callValue) {
                callValue = Integer.parseInt(msg);
                getPlayerById(getCurrentPlayerId()).getChannel().write("Call accepted");
                groupMsg("Player " + getCurrentPlayerId() + " called " + callValue + "\n");
                msgAnswered = true;
            }
            else{
                System.out.println("Wrong call");
                getPlayerById(getCurrentPlayerId()).getChannel().write("Wrong call, try again");
            }
        }
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

    public void setMsgAnswered(boolean msgAnswered){
        this.msgAnswered = msgAnswered;
    }

    public WAIT_FOR getWait() {
        return wait;
    }

    public int getCurrentPlayerId(){
        return (currentPlayerId);
    }

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
            player.getChannel().write(msg);
        }
    }
}
