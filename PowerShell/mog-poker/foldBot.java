import java.security.SecureRandom;
import java.util.List;

/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 */

public class foldBot extends pokerPlayer {
    private int minBet=0;

    public foldBot() {
        super( "FoldBot", 0 );
    }

    public foldBot( String name, int chips ){
        super( name, chips );
    }

    public void userNotification( String msg )    {
        // Notifications from the dealer come through here, though many are handled in super().notification

        // The bot developer decides which messages to respond to, and what to do in response.

        return;
    }
   
    @Override
    public String chooseAction( List<String> actions ) {
        if ( actions.contains("fold") )
            return "fold";
        else {       
            SecureRandom rnd = new SecureRandom();
            return actions.get( rnd.nextInt( actions.size() ) );
        }
    }
    
    @Override
    public int raiseAmount() {
        return minBet;
    }
    
        @Override
    public int betAmount() {
        return minBet;
    }

    
}
