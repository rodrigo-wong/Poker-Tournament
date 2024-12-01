/**
 * @author Stephen Adams
 * 
 * This is designed for use in COMP 10185 where the students will provide
 * a player bot to be imported to the tournament.
 * 
 * My role is that of dealer/house.
 * 
 * Rules from http://www.pokerlistings.com/poker-rules-texas-holdem
 *
 * Do not edit this source file except to add your player for testing purposes.
 * Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 */

import java.util.*;

public class PokerTournament {
//    final static boolean debug = false; // switching to false reduces many extraneous messages
    static boolean debug = true; // you are allowed to enable and disable debug messages throughout
    static boolean quiet = false; // quiet will gag most of the announce output and overrides debug
    final static long version = 20223501;
    List<pokerPlayer> players;
    static int[] params;

    public static void debugWrite( String str ) {
        if ( debug && !quiet ) {
            System.out.print( str );
        }
    }
    public List<pokerPlayer> loadPlayers(  int stake ) {
        players = new ArrayList<>();

        /**
         *  Add your player here, it must be in the same package.
        */

        // to do: read directories and add players from presence of files
        //players.add( new pokerPlayer( "Your bot", stake ) );
        //players.add( new humanPlayer( "Tess Ting", stake ) );
        players.add( new randBot( "Randy McRandom", stake ) );
        players.add( new foldBot( "Fearless Folder", stake ) );
        players.add( new callBot( "Casual Caller", stake ) );
        players.add( new maxBetBot( "Maxine McMaxBet", stake ) );

        // Until I restructure to do this more correctly just
        // return the global reference and live with it.
        return players;
    }

    public int[] setParameters( int bigBlindAmount, int stake, int doubleTurns ) {
        // tournament initial settings
        // to do: externalize to config file/params

        // small blind is half big blind. Min bet is small blind, max bet is 10*big blind

        // how many turns to play before doubling the blinds
        //  0 means disabled/never double blinds
        // -1 means on player elimination

        params = new int[3];
        params[0]=bigBlindAmount;
        params[1]=stake;
        params[2]=doubleTurns;

        // again, this is silly but part of a future fix
        return params;
    }
    public HashMap<String, Integer> run() {
        try {
            var game = new pokerGame(players, params[0], params[1], params[2]);
            return game.doGame( players );
        } catch ( Exception e ) {
            System.out.println( "Game has terminated with an execption or assertion fault" );
            System.out.println( e.getMessage() );
            System.out.println( "Please report to stephen.adams5@mohawkcollege.ca with the log of the last game played.");
        }
        return null;
    }
    public static void main(String[] args) {
        PokerTournament tournament = new PokerTournament();

        int N = 1;
        HashMap<String,Integer> stats = new HashMap<String, Integer>(),
                            wins = new HashMap<String, Integer>(),
                            temp;


        tournament.setParameters( 100, 10185, 15);

        for ( int i = 0; i<N; i++ ) {
            System.out.printf( "Playing game #%d%n", i );
            tournament.loadPlayers(params[1]);
            temp = tournament.run();

            if ( temp == null ) {
                i--;
                continue;
            }

            for (Map.Entry<String, Integer> result : temp.entrySet() ) {
                if (stats.containsKey(result.getKey())) {
                    stats.put( result.getKey(), result.getValue() +
                            stats.get( result.getKey() ) );
                } else {
                    stats.put( result.getKey(), result.getValue() );
                }

                if ( result.getValue() == 0 ) {
                    if (wins.containsKey(result.getKey())) {
                        wins.put(result.getKey(), 1 + wins.get(result.getKey()));
                    } else {
                        wins.put(result.getKey(), 1);
                    }
                }

            }
        }

        System.out.println( String.format("Results of tournament (%d games):", N ) );
        System.out.println( String.format("%40s\t%s\t%s", "Player","Wins","Average Bust Round"));

        for (Map.Entry<String, Integer> result : stats.entrySet() ) {
            int totalWins=0;
            if ( wins.containsKey(result.getKey()))
                totalWins = wins.get(result.getKey());
            System.out.println( String.format("%40s\t%d\t\t%2.1f", result.getKey(), totalWins,
                                                                   ((float)result.getValue()/(1.0*N))));
        }
    }
}
