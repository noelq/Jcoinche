package Server;

public class Card {
    public enum COLOUR{
        DIAMONDS, HEARTS, CLUBS, SPADES
    }

    COLOUR colour;
    String display_string;
    public int value;


    public Card(String display_string, COLOUR colour, int value){
        this.value = value;
        this.display_string = display_string;
        this.colour = colour;
    }

    public int getValue(){
        return this.value;
    }

    public String getDisplay_string(){
        return this.display_string;
    }

    public COLOUR getColour() {
        return this.colour;
    }
}
