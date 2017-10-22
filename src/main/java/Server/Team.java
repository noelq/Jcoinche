package Server;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int score = 0;
    private int nbPlayers = 0;
    public List<Player> players = new ArrayList<Player>();

    public Player getPlayer(int idx){
        return (players.get(idx));
    }

    public void addPlayer(Player player) {
        players.add(player);
        nbPlayers += 1;
    }

    public void addScore(int scoreToAdd){
        score += scoreToAdd;
    }

    public int getScore(){
        return (score);
    }

    public int getNbPlayers() {
        return (nbPlayers);
    }

    public void setNbPlayers(int nb){
        this.nbPlayers = nb;
    }
}
