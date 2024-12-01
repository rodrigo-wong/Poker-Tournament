import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class pokerGame {
    enum Phases { ANTE, BURN, HOLE, FLOP, TURN, RIVER, BET, REVEAL }
    int bigBlindAmount;
    int stake;
    int doubleTurns;
    pokerDealer dealer;

    public pokerGame(List<pokerPlayer> players, int bigBlindAmount, int stake, int doubleTurns ) {
        dealer = new pokerDealer();
        this.bigBlindAmount = bigBlindAmount;
        this.stake = stake;
        this.doubleTurns = doubleTurns;

        // subscribe players to the dealer's announcements
        players.stream().forEach((player) -> {
            dealer.addListener(player);
        });

        // randomize seating positions at table as position can confer advantage.
        Collections.shuffle(players);
    }

    public HashMap<String, Integer> doGame(List<pokerPlayer> players ) {
        /*  *** ** *** ** *** **[  Start of game sequence ]*** ** *** ** *** ** */

        dealer.startGame( bigBlindAmount, doubleTurns, players );

        // The game ends when there are no more players, or when the big blind
        // amount is more than the available chips on the table.

        // Rationale: forcing 2 bots to have a 1 hand showdown for all the chips
        // implies that the bots are mismatched, when in fact they are apparently
        // quite evenly matched.  I choose to record the draw.
        boolean liveGame = true;
        while ( players.size() > 1 && bigBlindAmount < pokerDealer.getInitialChips() ) {
            if ( !PokerTournament.quiet ) System.out.println();
            if ( !PokerTournament.quiet ) System.out.println();
            /***************************************/
            /*  Pay blinds                         */
            /***************************************/
            liveGame = dealer.bettingRound( Phases.ANTE, null, players );
            assert dealer.sumchips(players)+ dealer.pot==dealer.getInitialChips();
            if ( !PokerTournament.quiet ) System.out.println();

            /***************************************/
            /*  Deal the Hole Cards                */
            /***************************************/
            if ( liveGame ) {
                dealer.deal( Phases.HOLE, players );
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Bidding Round                      */
            /***************************************/
            // after dealing hole cards, small blind payer bids first
            if ( liveGame ) {
                liveGame = dealer.bettingRound( Phases.BET, Phases.HOLE, players );
                assert dealer.sumchips(players)+ dealer.pot==dealer.getInitialChips();
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Deal the Flop Cards                */
            /***************************************/
            // Burn 1 card before dealing the flop.
            if ( liveGame ) {
                dealer.deal( Phases.BURN, players );
                dealer.deal( Phases.FLOP, players );
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Bidding Round                      */
            /***************************************/
            // after dealing flop cards, big blind player bids first
            if ( liveGame ) {
                liveGame = dealer.bettingRound( Phases.BET, Phases.FLOP, players );
                assert dealer.sumchips(players)+ dealer.pot==dealer.getInitialChips();
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Deal the River Cards                */
            /***************************************/
            // Burn 1 card before dealing the river.
            if ( liveGame ) {
                dealer.deal( Phases.BURN, players );
                dealer.deal( Phases.RIVER, players );
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Bidding Round                      */
            /***************************************/
            // after dealing flop cards, big blind player bids first
            if ( liveGame ) {
                liveGame = dealer.bettingRound(Phases.BET, Phases.RIVER, players);
                assert dealer.sumchips(players)+ dealer.pot==dealer.getInitialChips();
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Deal the Turn Cards                */
            /***************************************/
            // Burn 1 card before dealing the turn.
            if ( liveGame ) {
                dealer.deal( Phases.BURN, players );
                dealer.deal( Phases.TURN, players );
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Bidding Round                      */
            /***************************************/
            // after dealing turn cards, big blind player bids first
            if ( liveGame ) {
                dealer.bettingRound(Phases.BET, Phases.TURN, players);
                assert dealer.sumchips(players)+ dealer.pot==dealer.getInitialChips();
                if ( !PokerTournament.quiet ) System.out.println();
            }

            /***************************************/
            /*  Reveal the winner                  */
            /***************************************/
            dealer.showWinner( players );
            assert dealer.sumchips(players)==dealer.getInitialChips();
            if ( !PokerTournament.quiet ) System.out.println();

            dealer.bustOut( players );
            assert dealer.sumchips(players)==dealer.getInitialChips();

            // move the button and adjust blind payers
            // current big blind player becomes last, the small blind player becomes the big blind, etc
            Collections.rotate( players, -1);
        }

        if ( players.size() == 1 ) {
            dealer.announce(String.format("%s wins %d chips after %d hands!", players.get(0).name, players.get(0).chipTotal, dealer.handsPlayed), false);
            dealer.setResults(players.get(0).name, 0);
        } else {
            StringBuilder message=new StringBuilder();
            message.append("Due to strategies used, the game is a draw between " );
            for ( int i = 0; i < players.size(); i++ ) {
                dealer.setResults( players.get(0).name, 0);

                message.append(players.get( i ).name);
                if ( i + 2 < players.size() ) {
                    message.append(", ");
                } else if ( i + 1 < players.size() ) {
                    message.append(" and ");
                }
            }
            message.append(".");
            dealer.announce( message.toString(), false );
        }

        return dealer.getResults();
    }

    /*
    public static void showdownShow( pokerDealer dealer, List<pokerPlayer> players, String[] statuses ) {
        dealer.announce( "Showdown show, players reveal your hole cards.");
        for ( int i = 0; i < players.size(); i++ ) {
            if ( statuses[i].equals("AllIn") )
                dealer.announce( String.format( "%s shows %s and %s",
                    players.get(i).name,
                    players.get(i).holeCards.get(0)[0] + players.get(i).holeCards.get(0)[1],
                    players.get(i).holeCards.get(1)[0] + players.get(i).holeCards.get(1)[1] ) );
        }

 */
//    }

}
