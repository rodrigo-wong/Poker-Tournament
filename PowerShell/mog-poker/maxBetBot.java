import java.security.SecureRandom;
import java.util.List;

/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 * This trivial bot helps to demonstrate the functionality students must implement without
 * any signficant effort made to implement a strategy. This bot always chooses to make the
 * maximum bet, regardless of the game state.
 *
 */

public class maxBetBot extends pokerPlayer {
    final static long version = 20223500;

    public maxBetBot() {
        super( "Uninitialized", 0 );
    }

    public maxBetBot( String name, int chips ){
        super( name, chips );
    }

    public void userNotification( String msg )    {
        // Notifications from the dealer come through here, though many are handled in super().notification

        // The bot developer decides which messages to respond to, and what to do in response.

        return;
    }

    @Override
    public String chooseAction( List<String> actions ) {
        if ( actions.contains( "bet" ) ) {
            return ( "bet" );
        } else if ( actions.contains( "raise" ) ) {
            return ( "raise" );
        } else if ( actions.contains( "call" ) ) {
            return ( "call" );
        } else if ( actions.contains( "check" ) ) {
            return ( "check" );
        }

        // in the absence of a preferred option, simply pick at random
        return actions.get( new SecureRandom().nextInt( actions.size() ) );
    }
    
    @Override
    public int raiseAmount() {
        return Math.min( chipTotal, rules[MAXBET] );
    }
    
    @Override
    public int betAmount() {
        return Math.min( chipTotal, rules[MAXBET]);
    }
    
}
