import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 * Create a new file that implements this abstract class.  Add your player to the
 * PokerTournament.java file
 *
 */

// See http://stackoverflow.com/a/6270150/759749 for more information
// An interface to be implemented by everyone interested in "Hello" events
interface DealerListener {
    void notification( String message );
}

public abstract class pokerPlayer implements DealerListener {
    final static long version = 20223500;

    protected int chipTotal;
    protected List<String[]> holeCards;
    protected List<String[]> tableCards;
    public String name;
    protected int[] rules  = new int[4];
    // tightly coupled to dealer announcement order
    protected final short SMALL=0;
    protected final short LARGE=1;
    protected final short MINBET=2;
    protected final short MAXBET=3;

    protected boolean showHints = false;
    public abstract String chooseAction( List<String> actions );
    public abstract int betAmount();
    public abstract int raiseAmount();
    
    public pokerPlayer() {
        this( "Uninitialized", 0 );
    }

    public pokerPlayer( String name, int chips ){
        this.name = name;
        if ( chips < 0 )
            chips = 0;
        this.chipTotal = chips;
        this.holeCards = new ArrayList<>();
        this.tableCards = new ArrayList<>();
    }

    // to implement any custom annnouncement matching code
    public abstract void userNotification(String msg);

    public void notification( String msg )    {
        // Notifications from the dealer come through here.
        Matcher matcher;
        HashMap< String, Pattern > patterns = new HashMap<>();
        Pattern rulesPat = Pattern.compile( "^The rules for this game are:.*Large Blind is (\\d+).*Small Blind is (\\d+).*Min bet is (\\d+).*Max bet is (\\d+).*$");
        Pattern startPat = Pattern.compile( "^Starting hand \\d+, please ante up\\.$" );
        Pattern flopPat = Pattern.compile( "^.*Dealer shows (.)(.) (.)(.) (.)(.).*$", Pattern.DOTALL );
        Pattern riverTurnPat = Pattern.compile( "^.*Dealer shows (.)(.).*$", Pattern.DOTALL );
        Pattern lostPat = Pattern.compile("^(.*) has busted at hand (\\d+) and must leave the table" );

        // To Do: number of patterns is going to increase, let's set up a hash map
        //patterns.put( "rules", Pattern.compile( "^The rules for this game are:.*Large Blind is (\\d+).*Small Blind is (\\d+).*Min bet is (\\d+).*Max bet is (\\d+).*$") );

        // Sample response, you can also call your own processing
        if ( msg.equals( "Hello Players.  New Game Starting." )) {
            PokerTournament.debugWrite(String.format("%s replies:\tHello dealer%n", name));
        } else if ( (matcher = rulesPat.matcher(msg) ).matches() ) {
            rules[SMALL] = Integer.parseInt(matcher.group(SMALL+1));
            rules[LARGE] = Integer.parseInt(matcher.group(LARGE+1));
            rules[MINBET] = Integer.parseInt(matcher.group(MINBET+1));
            rules[MAXBET] = Integer.parseInt(matcher.group(MAXBET+1));
        } else if ( ( matcher = startPat.matcher(msg) ).matches() ) {
            // New hand, reset internal variables tracking the cards
            this.holeCards.clear();
            this.tableCards.clear();
        } else if ( ( matcher = flopPat.matcher(msg) ).matches() || ( matcher = riverTurnPat.matcher(msg) ).matches() ) {
            String[] card = new String[2];
            for ( int i = 1; i <= matcher.groupCount(); i++ ) {
                card[ (i - 1) % 2] = matcher.group( i );
                if ( i % 2 == 0 ) {
                    tableCards.add( card );
                    card = new String[2];
                }
            }
        }
    }

    public int getChipTotal() { return chipTotal; }
    
    public void receiveCard( String[] card ) { holeCards.add(card); }
    
    public boolean bet( int amount ) {
        boolean validBet = false;
        if ( amount >= 0 && amount <= chipTotal ) {
            chipTotal -= amount;
            validBet = true;
        }       
        return validBet;
    }
    
    public void allIn() {
        bet( chipTotal );
    }
        
    public void getChips( int amount ) { chipTotal += amount; }
    
    public String[][] showHand() {
        String[][] cardsAvailable = new String[7][2];
        String[][] bestHand = new String[5][2];
        String[][] testHand = new String[5][2];
        double bestHandRank = 0.0;

        cardsAvailable[0] = (String[])(holeCards.toArray())[0];
        cardsAvailable[1] = (String[])(holeCards.toArray())[1];
        
        String[][] tc = tableCards.toArray(new String[0][0]);
        int a = 2;
        for ( String[] c : tc ) {
            cardsAvailable[a++] = c;
        }
        if (a<7) {
            // The player has not submitted enough cards
            bestHand = cardsAvailable;
            PokerTournament.debugWrite( "Cannot determine best hand on fewer than 7 cards.");
        } else {
            for( int i = 0; i<cardsAvailable.length; i++ ) {
                testHand[0] = cardsAvailable[i];
                for ( int j = i+1; j < cardsAvailable.length; j++ ) {
                    testHand[1] = cardsAvailable[j];
                    for ( int k = j+1; k < cardsAvailable.length; k++ ) {
                        testHand[2] = cardsAvailable[k];
                        for ( int l = k+1; l < cardsAvailable.length; l++ ) {
                            testHand[3] = cardsAvailable[l];
                            for ( int m = l+1; m < cardsAvailable.length; m++ ) {
                                testHand[4] = cardsAvailable[m];
                                //System.out.println(Arrays.toString(testHand));
                                double testRank = pokerDealer.rankHand( testHand, name,false );
                                if ( testRank > bestHandRank ) {
                                    bestHandRank = testRank;
                                    // to do : verify this is safe, are we deep copying?
                                    bestHand = Arrays.copyOf( testHand, testHand.length );
                                }
                            }//m
                        }//l
                    }//k
                }//j
            }//i
        }        
        return bestHand;
    }

    public String showHole() {
        StringBuilder hand = new StringBuilder();
        for ( String[] card : holeCards ) {
            hand.append( card[0] );
            hand.append( card[1] );
            hand.append( ' ' );
        }
        return hand.toString();
    }

}
