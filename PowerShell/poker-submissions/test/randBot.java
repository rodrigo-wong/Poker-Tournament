import java.security.SecureRandom;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 * This trivial bot helps to demonstrate the functionality students must implement without
 * any signficant effort made to implement a strategy. This bot makes random choices from
 * the available options.
 *
 */

public class randBot extends pokerPlayer {
    final static long version = 20223500;
    public randBot() {
        super( "Uninitialized", 0 );
    }

    public randBot( String name, int chips ){
        this.name = name;
        if ( chips < 0 )
            chips = 0;
        this.chipTotal = chips;
        this.holeCards = new ArrayList<>();
        this.tableCards = new ArrayList<>();
    }

    public void userNotification( String msg )    {
        // Notifications from the dealer come through here, though many are handled in super().notification

        // The bot developer decides which messages to respond to, and what to do in response.

        return;
    }
    
    public String chooseAction( List<String> actions ) {
        // Randomly choose from the available options.
        return actions.get( new SecureRandom().nextInt( actions.size() ) );
    }
       
    @Override
    public int raiseAmount() {
        // Raise by random amounts up to all available chips.
        return Math.min(rules[MAXBET] , new SecureRandom().nextInt(chipTotal) );
    }
    
    @Override
    public int betAmount() {
        // Bet a random amount up to all available chips.
        return Math.min(rules[MAXBET] , new SecureRandom().nextInt(chipTotal) );
    }
}
