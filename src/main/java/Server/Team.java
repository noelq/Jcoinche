package Server;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int contractValue;
    private int score = 0;
    private int roundScore = 0;
    private int nbPlayers = 0;
    public List<Player> players = new ArrayList<Player>();

    public Player getPlayer(int idx){
        return (players.get(idx));
    }

    public void addPlayer(Player player) {
        players.add(player);
        nbPlayers += 1;
    }

    public void reset(){
        contractValue = 0;
        roundScore = 0;
    }

    public void setContractValue(int contractValue){
        this.contractValue = contractValue;
    }

    public int getContractValue(){
        return contractValue;
    }

    public void addRoundScore(int scoreToAdd){
        roundScore += scoreToAdd;
    }

    public void addScore(int scoreToAdd){
        score += scoreToAdd;
    }

    public int getRoundScore(){
        return (roundScore);
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
