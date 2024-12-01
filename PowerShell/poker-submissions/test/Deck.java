import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 */

public class Deck {
    final static long version = 20173500;
    private static String[] suits;
    private static String[] longSuits;
    private final String[] ranks;
    private final List<String[]> deck;
    
    public final static int RANK = 0;
    public final static int SUIT = 1;
    
    public Deck() {
        this.suits = new String[]{"H", "D", "C", "S"};
        this.longSuits = new String[]{"hearts", "diamonds", "clubs", "spades"};
        this.ranks = new String[]{"A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K"};
        this.deck = new ArrayList<>();
        
        for( String suit : suits )
            for ( String rank : ranks ) {
                String[] card = new String[2]; // need a new string each time
                card[RANK] = rank;
                card[SUIT] = suit;
                deck.add( card );
            }
    }

    public static String getLongSuit( String suit ){
        return longSuits[Arrays.asList(suits).indexOf(suit)];
    }
    
    @Override
    public String toString() {
        StringBuilder returnValue = new StringBuilder();
        deck.stream().forEach((card) -> {
            returnValue.append(card[RANK]).append(card[SUIT]).append(" ");
        });
        
        return returnValue.toString();
    }
 
    public void shuffle() {
        Collections.shuffle( deck );
    }
    
    public String[] dealCard( ) {
        String[] card = deck.get( 0 );
        deck.remove( 0 );
        
        return card;
    }
    
    
}
