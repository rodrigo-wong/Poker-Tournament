import java.util.List;
import java.util.Scanner;

/*
*
* @author Stephen Adams
*
* This class allows for a numan interactive player to be inserted in the Tournament without writing
* code in the main Tournament loop. Since the project is intended to be run in an automated way, this
* player really breaks the design intention. To make it work I violate some ideas about input/output
* from within a class. Thinking ahead to a javafx gui, I will overload anything that uses textual
* input/output inside the class.
*
* */

public class humanPlayer extends pokerPlayer {
    final static long version = 20223501;
    Scanner in = new Scanner(System.in);

    public humanPlayer( String name ) {
        this( name, 0 );
        showHints=true;
    }

    public humanPlayer( String name, int chips ){
        super( name, chips );
        showHints=true;
    }

    public void userNotification( String msg )    {
        // Notifications from the dealer come through here, though many are handled in super().notification

        // The bot developer decides which messages to respond to, and what to do in response.

        return;
    }

    @Override
    public String chooseAction( List<String> actions ) {
        if ( actions.size() == 1 ) {
            System.out.printf("No choices, can only %s.\n", actions.get(0));
            return ( actions.get(0) );
        }

        System.out.print( String.format( "%d chips on hand, available actions: ", chipTotal ) );
        for ( int i=0; i<actions.size(); i++ ) {
            System.out.printf( "[%d] %s\t", i, actions.get(i) );
        }

        int choice = -1;
        while ( choice < 0 || choice > actions.size() ) {
            System.out.print("\nPlease enter your choice: ");
            choice = in.nextInt();

            if ( choice < 0 || choice > actions.size() )
                System.out.printf( "I'm sorry, that's an invalid choice. Please enter an integer between 0 and %d.\n", actions.size() );
        }

        return actions.get( choice );
    }

    @Override
    public int raiseAmount() {
        System.out.printf( "Your chip total is $%d. How much would you like to raise? ", chipTotal );
        int bet = -1;

        while ( bet < 0 || bet > chipTotal ) {
            bet = in.nextInt();

            if ( bet < 0 || bet > chipTotal ) {
                System.out.println("Sorry, you don't have that many chips, raising all in.");
                bet = chipTotal;
            }
        }

        return bet;
    }

    @Override
    public int betAmount() {
        System.out.printf( "Your chip total is $%d. How much would you like to bet? ", chipTotal );
        int bet = -1;

        while ( bet < 0 || bet > chipTotal ) {
            bet = in.nextInt();

            if ( bet < 0 || bet > chipTotal ) {
                System.out.println("Sorry, you don't have that many chips, calling all in.");
                bet = chipTotal;
            }
        }

        return bet;
    }

}
