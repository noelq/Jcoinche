package Server;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private int score;
    public List<Player> players = new ArrayList<Player>();

    public Player getPlayer(int idx){
        return (players.get(idx));
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public void addScore(int scoreToAdd){
        score += scoreToAdd;
    }

    public int getScore(){
        return (score);
    }
}
