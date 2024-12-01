import java.security.SecureRandom;
import java.util.List;

/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 * This trivial bot helps to demonstrate the functionality students must implement without
 * any signficant effort made to implement a strategy. This bot always chooses to call the bet,
 * regardless of the game state.
 *
 */

public class callBot extends pokerPlayer {
    final static long version = 20223500;

    public callBot() {
        super( "Uninitialized", 0 );
    }

    public callBot( String name, int chips ){
        super( name, chips );
    }

        @Override
        public void userNotification( String msg )    {
            return;
        }

    @Override
    public String chooseAction( List<String> actions ) {
        if ( actions.contains( "call" ) )
            return ( "call" );
        else if ( actions.contains( "check" ) )
            return ( "check" );

        // in the absence of a preferred option default to a random move
        return actions.get( new SecureRandom().nextInt( actions.size() ) );
    }

    @Override
    public int raiseAmount() {
        return 0;
    }
    
    @Override
    public int betAmount() {
        return 0;
    }
    
}
