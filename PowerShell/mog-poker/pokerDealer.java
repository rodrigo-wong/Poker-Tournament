import java.util.*;


/**
 *
 * @author Stephen Adams
 *
 * Do not edit this source file.  Report all bugs to stephen.adams5@mohawkcollege.ca
 *
 * The dealer maintains the game state while the tournament acts as a controller for the game
 * rules. There are some useful static methods to rank hands, etc.
 */

public class pokerDealer {

    final static long version = 20223501;
    private final List<DealerListener> listeners = new ArrayList<>();
    private String tableCards[][]; // 5 cards are dealt to the table
    private String holeCards[][][]; // track player's hole cards
    Deck deck;
    int handsPlayed=0;
    private static int initialChips=0;
    private int bigBlindAmount, smallBlindAmount, minBet, maxBet, doubleTurns;
    // to do : side pots are currently disabled, players can only win up to
    //         what they are invested, but the current representation is losing
    //         chips.
    int pot = 0;

    private String[] playerStatus;
    private HashMap<String, Integer> results; // results records what hand someone was eliminated

    public HashMap<String, Integer> getResults() {return results;} // to do ... clone()
    public void setResults( String name, int hands ) {
        results.put( name, hands );
    }
    public static int getInitialChips() {return initialChips;}
    public static int sumchips( List<pokerPlayer> players ) {
        int total=0;
        for ( pokerPlayer p : players )
            total+=p.chipTotal;
        return total;
    }
    public void addListener(DealerListener toAdd) {        listeners.add(toAdd);    }

    public void startGame( int blindAmount, int doubleTurns, List<pokerPlayer> players ) {
        announce( "Hello Players.  New Game Starting." );
        initialChips=0;
        results = new HashMap<>();

        // blind values change during game play
        setBlinds( blindAmount );
        this.doubleTurns = doubleTurns;

        for ( pokerPlayer p : players )
            initialChips += p.getChipTotal();

        System.out.println();

        // tightly coupled to pokerPlayer.rules[] order
        announce( String.format( "The rules for this game are: %s is %d.\t%s is %d.\t%s is %d.\t%s is %d.", "Large Blind", bigBlindAmount, "Small Blind", smallBlindAmount, "Min bet", minBet, "Max bet", maxBet) );

        String doubleStr;
        if ( doubleTurns == -1 ) doubleStr = "on player elmination";
        else if (doubleTurns == 0 ) doubleStr = "never";
        else doubleStr = String.format("after %d turns",doubleTurns);

        announce( String.format("Blinds will double %s. There are a total of %d chips in play.", doubleStr, initialChips));

        System.out.println();
        announce( seated( players ) );
    }

    private String seated( List<pokerPlayer> players ) {
        StringBuilder message = new StringBuilder("Seated at this game are ");
        for ( pokerPlayer player: players ) {
            message.append(player.name);
            message.append( ", " );
        }
        message.delete( message.length() - 2, message.length() );
        message.append( "." );

        message.insert( message.lastIndexOf( ", " ), " and" );

        return message.toString();
    }

    private void setBlinds(int blindAmount) {
        this.bigBlindAmount = blindAmount;
        this.smallBlindAmount = bigBlindAmount / 2;
        this.minBet = smallBlindAmount;
        this.maxBet = 10*bigBlindAmount;
    }

    public void announce( String msg ) {
        announce( msg, true );
    }

    public void announce( String msg, boolean canGag ) {
        // See http://stackoverflow.com/a/6270150/759749 for more information
        if ( !(PokerTournament.quiet && canGag) )
            System.out.printf( "Dealer announces:\t%s%n", msg );

        // Notify everybody that may be interested.
        listeners.stream().forEach((hl) -> { hl.notification(msg); });
    }

    public void newHand() {
        tableCards = new String[5][2]; // 5 cards
        holeCards = new String[listeners.size()][2][2]; // 2 cards per player

        playerStatus = new String[listeners.size()];
        for (int i = 0; i < playerStatus.length; i++) playerStatus[i] = "";

        pot = 0;
        handsPlayed++;

        checkBlinds();

        deck = new Deck();
        deck.shuffle();

    }

    // for side pots track how many chips the player has invested in the pot
    // to do: needs to be an arraylist of pots, the side pots gets weird sometimes
    // side pots temporarily disabled, this is causing chip bleed.
    // int[] invested = new int[ players.size() ];

    private void checkBlinds() {
        // double the blinds every 15 hands to push to completion
        if ( handsPlayed % 15 == 0 && handsPlayed != 0 ) {
            if ( bigBlindAmount < initialChips ) {
                setBlinds(bigBlindAmount*2);
                announce( "Blinds are doubling!");
                announce( String.format( "%s is %d.%n\t%s is %d.%n\t%s is %d.%n\t%s is %d.%n",
                        "Large Blind", bigBlindAmount,
                        "Small Blind", smallBlindAmount,
                        "Min bet", minBet,
                        "Max bet", maxBet) );
            }
        }
    }

    public void showWinner( List<pokerPlayer> players ) {
        double highRank = -1;
        double handRank;
        List<Integer> winners = new ArrayList<>();

        int eligible=0;
        for ( String s : playerStatus )
            if ( !s.equals("Folded") ) eligible++;

        if ( eligible == 1 ) {
            for ( int i=0; i<playerStatus.length; i++ ) {
                if ( !playerStatus[i].equals("Folded" )) {
                    announce( String.format("With all players folded, %s takes the hand!", players.get(i).name ));
                    winners.add( i );
                }
            }
        } else {
            // show hands beginning with the player who bet on the river or with the
            // first player to the left of the dealer if no bet was made
            int lastRaise=-1;
            for ( int i=0; i<playerStatus.length; i++ ) {
                if ( playerStatus[i].equals("Raised") ) {
                    lastRaise = i;
                    break;
                }
            }

            if ( lastRaise > -1 ) {
                Collections.rotate(players, -1 * lastRaise);
                List<String> statuses = Arrays.asList( playerStatus );
                Collections.rotate(statuses, -1 * lastRaise);
                //playerStatus =  statuses.toArray( new String[0] );
                announce("Checking hands, starting with last raise: ");
            } else {
                announce( "Checking hands, starting on the button: " );
            }

            for ( int i = 0; i < players.size(); i++ ) {
                if ( playerStatus[ i ].equals( "Folded" ) )
                    continue;

                String[][] hand =  players.get(i).showHand();
                handRank = rankHand( hand, players.get(i).name );

                // as we go along in order, rank the hand if it is lower
                // than the highest then we may either show or muck
                List<String> actions = new ArrayList<>();
                actions.add( "show" );

                // if a player has a losing hand compared to revealed hands,
                // they may fold (muck) or show
                if ( handRank < highRank ) {
                    actions.add( "muck" );
                }
                String action = players.get(i).chooseAction( actions );

                if ( action.equals( "muck" ) ) {
                    announce( players.get(i).name + " folds, mucking their hand.");
                    PokerTournament.debugWrite( String.format( "They had %s %s %s %s %s.\n",
                            hand[0][0]+hand[0][1], hand[1][0]+hand[1][1], hand[2][0]+hand[2][1],
                            hand[3][0]+hand[3][1], hand[4][0]+hand[4][1] ) );
                } else {
                    announce( players.get(i).name +
                            String.format( " reveals their hand. They had %s %s %s %s %s.",
                                    hand[0][0]+hand[0][1], hand[1][0]+hand[1][1], hand[2][0]+hand[2][1],
                                    hand[3][0]+hand[3][1], hand[4][0]+hand[4][1]
                            ) );

                    if ( handRank == highRank) {
                        winners.add( i );
                    } else if ( handRank > highRank ) {
                        winners.clear();
                        winners.add( i );
                        highRank = handRank;
                    }
                }

            }
        }

        // all players in winners have the same rank split the pot
        int[] split = new int[winners.size()];
        Arrays.fill(split,pot/winners.size() );
        pot = pot - (( pot/winners.size() ) * winners.size());
        if ( pot > 0 ) {
            int i = 0;
            while ( pot > 0 ) {
                split[i]++;
                pot--;
                i++;
                if ( i > winners.size() ) i = 0;
            }
        }

        for ( int i=0; i<winners.size(); i++ ) {

            players.get( winners.get(i) ).getChips( split[i] );
            if ( winners.size() > 1 )
                announce( String.format("%s splits the pot and wins %d chips.", players.get(winners.get(i)).name, split[i] ) );
            else
                announce( String.format("%s wins the hand and a pot of %d chips.", players.get(winners.get(i)).name, split[i] ) );
        }
        pot=0;
    }


    private int badBet( pokerPlayer player, int betAmount ) {
        // player cannot cover bet
        betAmount = player.getChipTotal();
        player.bet( betAmount );
        return betAmount;
    }

    public boolean bettingRound( pokerGame.Phases phase, pokerGame.Phases prevPhase, List<pokerPlayer> players ) {
        switch ( phase ) {
            case ANTE:
            {
                // Each hand begins with a fresh deck of cards.
                newHand();
                announce( seated( players ) );
                pot = 0;

                // Start of hand players have no status/state
                announce(String.format("Starting hand %d, please ante up.", handsPlayed));

                // set the big and small blind bet amounts, if players cannot make the blinds they are all in, and a side pot will be created
                String[] b = {"big", "small"};
                for (int i = 0; i < 2; i++) {
                    int bid;
                    if (i == 0) bid = bigBlindAmount;
                    else bid = smallBlindAmount;

                    if (players.get(i).bet(bid)) {
                        //invested[i] = bid;
                        pot += bid;
                        announce(players.get(i).name + " has paid the " + b[i] + " blind.");
                    } else {
                        playerStatus[i] = "AllIn";
                        pot += players.get(i).chipTotal;
                        players.get(i).allIn();
                        announce(players.get(i).name + " has failed to pay the " + b[i] + " blind and is all in.");
                        //invested[i] = players.get( i ).getChipTotal();
                    }
                }
                announce( String.format("The pot is currently %d chips.", pot ));
                break;
            }
            case BET:
            {
                // A showdown occurs if all players have gone "all in".
                //    - all in allows a player to continue playing if they have insufficient chips to call
                //        - such a player can only win the portion of the pot they can actually bet on (investment),
                //          with the remainder going to the next highest hand's player, who has a remaining
                //          investment in the pot until the entire pot is dispersed.
                //    - a player can voluntarily bet their remaining chips to force other players to call with
                //          a matching bet - this can lead to cascading investments

                // playerStatus is persistant if all in or folded between orbits.
                // betting statuses, Called, Raised, must be reset
                for (int i = 0; i < playerStatus.length; i++)
                    if (!playerStatus[i].equals("Folded") && !playerStatus[i].equals("AllIn")) playerStatus[i] = "";

                int eligible = 0;
                // If everyone has folded or gone all in there is a showdown.
                // Similarly if only 1 better is eligible, there is no betting.
                for (String status : playerStatus)
                    eligible += (status.equals("Folded") || status.equals("AllIn")) ? 0 : 1;

                // If we are in a showdown there is no betting round, everyone has already called the AllIn
                // with their own AllIn, or has folded. Note: players with sufficient chips to call the all in bet
                // without going all in themselves may still participate in betting
                if (eligible == 0 ) {
                    announce("Players are in a showdown, no betting allowed.");
                } else if ( eligible == 1 ) {
                    announce( "Only 1 player is eligble to bet, no betting allowed.");
                } else {
                    announce("Starting betting round.");

                    // A betting round consists of check, call, or raise actions
                    //    - a check is a no bet, performed when the amount to call is 0
                    //    - bet, if they are the first non-checking player to do so
                    //    - a call is a bet that covers the amount already previously called
                    //    - a raise is a bet that covers the amount previously called, and increases it
                    //          - a raise must be at least the size of the largest previous bet or raise
                    //          - some rules require a raise to be 2x the largest previous bet

                    int bidder;
                    int[] toCall = new int[players.size()];
                    if (prevPhase == pokerGame.Phases.HOLE) {
                        bidder = 1;
                        toCall[0] = 0; // big blind is paid
                        toCall[1] = (bigBlindAmount / 2); // small blind must call the big blind but may raise
                        for (int i = 2; i < players.size(); i++) {
                            toCall[i] = bigBlindAmount;
                        }
                    } else {
                        bidder = 0;
                        for (int i = 0; i < players.size(); i++) toCall[i] = 0;
                    }

                    // An orbit, or a pass of each player during a betting round consists of them taking an
                    // action.
                    //
                    // Initial actions are bet, check, or fold.
                    // Subsequent players can either call, raise, or fold.
                    //
                    // The orbit ends when all but one player have folded, or all bidders have contributed
                    // an equal amount of chips or have called all-in with a short stack.
                    //
                    // Re-raises are allowed until all players have called or folded.
                    //   - A short all in call or raise does not reopen betting, only a full all in raise does

                    boolean betOpened = false;
                    ArrayList<String> actions;
                    while (isLiveOrbit(playerStatus)) {
                        boolean wrapped = false;
                        // for the betting round following the hole card deal the starting bidder is not
                        // the first player, in such a case we have to wrap around to get the first player
                        // at the end...
                        for (int i = bidder; i < players.size(); i++) {
                            if ( !isLiveHand() ) {
                                // the orbit can become dead between player's turns.
                                playerStatus[i]="Called";
                                break;
                            }

                            if (playerStatus[i].equals("Folded")
                                    || (playerStatus[i].equals("Called") && toCall[i] == 0)
                                    || (playerStatus[i].equals("Bet") )
                                    || playerStatus[i].equals("AllIn")
                                    || playerStatus[i].equals("Raised")
                            )
                                if ( bidder == 0 )
                                    continue;
                                else {
                                    // when the initial bidder is 1, we need to explicitly check player 0
                                    // to prevent an infinite loop in a situation like:
                                    // playerStatus = ["", "Raised", "Folded", "Called", "Folded"]
                                    // We could use wrapped and be clever, but that has caused enough
                                    // difficulties, so I'd rather just do it explicitly once.
                                    if ( i+1 == players.size() ) {
                                        if (playerStatus[0].equals("Folded")
                                                || (playerStatus[0].equals("Called") && toCall[0] == 0)
                                                || (playerStatus[0].equals("Bet") )
                                                || playerStatus[0].equals("AllIn")
                                                || playerStatus[0].equals("Raised") ) {
                                            continue;
                                        } else {
                                            i=0;
                                        }

                                    } else
                                        continue;
                                }

                            // Set up available player actions based on the round state.
                            actions = new ArrayList<>();

                            // No prior bet action alows an opening bet.
                            // Pot will not be 0 due to blinds.
                            if (!betOpened && toCall[i] == 0 && players.get(i).chipTotal > 0 ) {
                                actions.add("bet");
                            } else {
                                // a prior bet action can be raised but you can't raise yourself
                                if (!playerStatus[i].equals("Bet") && !playerStatus[i].equals("Raised") && players.get(i).chipTotal > 0 )
                                    actions.add("raise");
                            }

                            // If the call is for 0, that is a check, otherwise it's a call
                            if (toCall[i] == 0) {
                                actions.add("check");
                            } else {
                                actions.add("call");
                            }

                            // one may always fold
                            actions.add("fold");

                            if (!betOpened && i == 0 && toCall[i] == 0) {
                                announce(String.format("%s is on the button and they have the first bet.", players.get(i).name));
                            } else if (!betOpened && toCall[i] == 0) {
                                announce(String.format("%s has the opportunity to open the betting.", players.get(i).name));
                            } else {
                                announce(String.format("The bet is %d chips to %s.", toCall[i], players.get(i).name));
                            }

                            // once the available actions are determined retrieve the player's choice
                            // hints are to help the human player with game play but can be enabled
                            // for testing and debugging bots as well.
                            if ( players.get(i).showHints ) {
                                String table = showTableCards();
                                if (!table.equals(""))
                                    System.out.printf("The table cards are: %s\n", table);

                                System.out.printf("%s's hole cards are: %s\n", players.get(i).name, players.get(i).showHole());
                            }
                            String playerChoice = players.get(i).chooseAction(actions);

                            // precision is expected, making an invalid action results in a force fold
                            if (!actions.contains(playerChoice)) {
                                playerChoice = "fold";
                                toCall[i] = 0;
                                announce(String.format("%s has given the dealer an invalid choice and is being forced to leave the hand.", players.get(i).name));
                            }

                            // process the player's choice, verify bets, and update toCall if needed
                            int betAmount;
                            switch (playerChoice) {
                                case "bet": {
                                    // player has chosen to bet, how much?
                                    betAmount = players.get(i).betAmount();

                                    // To Do: Relax the rules to no limit texas hold 'em?
                                    if (betAmount < minBet) {
                                        announce(String.format("%s bid below the minimum bet. Bid increased from %d chips to %d chips.",
                                                players.get(i).name, betAmount, minBet));
                                        betAmount = minBet;
                                    } else if (betAmount > maxBet) {
                                        announce(String.format("%s bid above the maximum bet. Bid decreased from %d chips to %d chips",
                                                players.get(i).name, betAmount, maxBet));
                                        betAmount = maxBet;
                                    }

                                    // having bet the player has performed an action and cannot bet further unless raised
                                    if (!players.get(i).bet(betAmount)) {
                                        playerStatus[i] = "AllIn";
                                        betAmount = badBet(players.get(i), betAmount);
                                        announce(String.format("%s cannot cover bet of %d chips, and is all in, betting %d instead.", players.get(i).name, betAmount, players.get(i).getChipTotal()));
                                    } else {
                                        playerStatus[i] = "Bet";
                                        announce(String.format("%s bets %d chips.", players.get(i).name, betAmount));
                                    }

                                    pot += betAmount;
                                    toCall[i] = 0;
                                    // starting a new betting cycle
                                    for (int j = 0; j < players.size(); j++) {
                                        if (i != j && !playerStatus[i].equals("Folded")) toCall[j] += betAmount;
                                        if (i != j &&
                                                (playerStatus[j].equals("Called") || playerStatus[j].equals("Bet") || playerStatus[j].equals("Raised") )
                                           )
                                            playerStatus[j] = "";
                                    }
                                    betOpened = true;
                                    announce( String.format("The pot is currently %d chips.", pot ));
                                    break;
                                }
                                case "check":
                                    announce(String.format("%s has checked.", players.get(i).name));
                                    playerStatus[i] = "Called";
                                    break;
                                case "call": {
                                    betAmount = toCall[i];

                                    if (!players.get(i).bet(betAmount)) {
                                        playerStatus[i] = "AllIn";
                                        betAmount = badBet(players.get(i), betAmount);
                                        announce(String.format("%s calls all in with %d chips!", players.get(i).name, betAmount));
                                    } else {
                                        announce(String.format("%s calls %d chips ... ", players.get(i).name, betAmount));
                                        playerStatus[i] = "Called";
                                    }

                                    pot += betAmount;
                                    announce( String.format("The pot is currently %d chips.", pot ));
                                    toCall[i] = 0;
                                    betOpened = true;
                                    break;
                                }
                                case "raise": {
                                    int raiseAmount = players.get(i).raiseAmount();
                                    if (raiseAmount < minBet) {
                                        announce(String.format("%s's raise is below the minimum bet. Bid increased from %d chips to %d chips",
                                                players.get(i).name, raiseAmount, minBet));
                                        raiseAmount = minBet;
                                    } else if (raiseAmount > maxBet) {
                                        announce(String.format("%s's raise is above the maximum bet. Bid decreased from %d chips to %d chips",
                                                players.get(i).name, raiseAmount, maxBet));
                                        raiseAmount = maxBet;
                                    }

                                    // Player must pay to call amount plus the raise
                                    betAmount = toCall[i] + raiseAmount;
                                    if (!players.get(i).bet(betAmount)) {
                                        // player has insufficient chips to raise, so call all in
                                        playerStatus[i] = "AllIn";
                                        betAmount = badBet(players.get(i), betAmount);
                                        announce(String.format("%s calls %d and raises all in, betting %d!", players.get(i).name, toCall[i], raiseAmount));
                                    } else {
                                        playerStatus[i] = "Raised";
                                        announce(String.format("%s has called %d and raised by %d.", players.get(i).name, toCall[i], raiseAmount));
                                    }

                                    pot += betAmount;
                                    announce( String.format("The pot is currently %d chips.", pot ));

                                    toCall[i] = 0;
                                    // a raise invalidates a previous player's call and updates the amount to call
                                    // starting a new betting cycle
                                    for (int j = 0; j < players.size(); j++) {
                                        if (playerStatus[j].equals("Called") || playerStatus[j].equals("Bet") ) playerStatus[j] = "";
                                        if (playerStatus[j].equals("Raised") && j != i) playerStatus[j] = "";

                                        if (i != j && !playerStatus[i].equals("Folded"))
                                            toCall[j] += raiseAmount;
                                    }
                                    betOpened = true;
                                    break;
                                }
                                case "fold":
                                    playerStatus[i] = "Folded";
                                    toCall[i] = 0;
                                    announce(players.get(i).name + " has folded.");
                                    break;
                            }

                            // wrap around to get the first bidder(s)
                            if (bidder == 1 && i + 1 == players.size()) {
                                i = -1;
                                wrapped = true;
                            } else if (wrapped)
                                i = players.size();
                        }
                    }
                }
                break;
            }
        }

        // Check if the game is still live.
        return isLiveHand( );
    }

    private boolean isLiveOrbit( String[] statuses ) {
        boolean allDoneBetting = true;
        for ( String s : statuses )
            allDoneBetting &= ( s.equals("Folded") || s.equals("Called") || s.equals("AllIn") || s.equals("Raised") || s.equals("Bet") );

        return !allDoneBetting;
    }

    private boolean isLiveHand() {
        int livePlayers = 0;
        for ( int i=0; i<playerStatus.length; i++ ) {
            // you can continue to play the hand without chips if you are already all in.
            if ( !playerStatus[i].equals("Folded") )
                livePlayers++;
        }

        return livePlayers > 1;
    }

    private boolean liveGame( List<pokerPlayer> players ) {
        int livePlayers = 0;
        for ( int i=0; i<players.size(); i++ ) {
            if ( players.get(i).getChipTotal() > 0 )
                livePlayers++;
        }

        return livePlayers > 1;
    }

    public void deal ( pokerGame.Phases phase, List<pokerPlayer> players ) {
        switch ( phase ) {
            case HOLE: {
                announce("Dealing hole cards.");
                for (int i = 0; i < 2; i++) {
                    // deal first card to player who paid the big blind and rotate around the table
                    int h = 0;
                    for (pokerPlayer player : players) {
                        String[] card = deck.dealCard();
                        PokerTournament.debugWrite(String.format("Dealing %s to %s.%n", card[deck.RANK] + card[deck.SUIT], player.name));
                        player.receiveCard(card);

                        holeCards[h][i] = card;
                        h++;
                    }
                }
                break;
            }
            case BURN: {
                String[] burnCard = deck.dealCard();
                announce("Burning one card ... ");
                PokerTournament.debugWrite(String.format("The dealer discards %s%s.\n", burnCard[Deck.RANK], burnCard[Deck.SUIT]));
                break;
            }
            case FLOP: {
                tableCards[0] = deck.dealCard();
                tableCards[1] = deck.dealCard();
                tableCards[2] = deck.dealCard();
                announce(String.format("%sDealer shows %s%s %s%s %s%s ",
                        "Dealing the flop ... ",
                        tableCards[0][0], tableCards[0][1], tableCards[1][0], tableCards[1][1], tableCards[2][0],
                        tableCards[2][1]));
                break;
            }
            case TURN:
                tableCards[3] = deck.dealCard();
                announce( String.format( "%sDealer shows %s", "Dealing the turn ... ", tableCards[3][0]+tableCards[3][1] ) );
                break;
            case RIVER:
                tableCards[4] = deck.dealCard();
                announce( String.format( "%sDealer shows %s%s", "Dealing the river ... ", tableCards[4][0], tableCards[4][1] ) );
                break;
        }
    }

    public static double rankHand(String[][] hand, String name) { return rankHand(hand, name, true); }

    private static double hand2decimal( int[] ranks ) {
        String decimal = "0.";
        // each rank is 1 - 13, requiring 0 padding.
        for ( int i = ranks.length-1; i >= 0; i-- )
            if ( ranks[i] >= 10 )
                decimal = decimal + Integer.toString(ranks[i]);
            else
                decimal = decimal + "0" + Integer.toString(ranks[i]);

        return Double.parseDouble(decimal);
    }

    private static char rankToCardRank( int rank ) {
        char cardRank;

        switch ( rank ) {
            case 14: cardRank='A'; break;
            case 13: cardRank='K'; break;
            case 12: cardRank='Q'; break;
            case 11: cardRank='J'; break;
            case 10: cardRank='T'; break;
            default:
                cardRank=Integer.toString(rank).charAt(0);
        }

        return cardRank;
    }

    public static  double rankHand( String[][] hand, String name, boolean debug ) {
        double rank = 0;
        StringBuilder debugString = new StringBuilder();
        debugString.append( name );
        debugString.append( "'s Hand Ranking = ");

        if ( hand.length == 5 ) {
            // check flush
            
            // note to check flush you could sort the array
            // by suit then check first == last but this
            // solution is O(n) so ...
            boolean isFlush = true;
            String initialSuit = hand[0][ Deck.SUIT ];
            
            for ( String[] card : hand ) {
                isFlush &= initialSuit.equals( card[ Deck.SUIT ] );
            }

            int[] ranks = new int[ hand.length ];
            int i = 0;
            for (  String[] card : hand) {
                switch ( card[ Deck.RANK ] ) {
                    case "A": ranks[i] = 14; break;
                    case "K": ranks[i] = 13; break;
                    case "Q": ranks[i] = 12; break;
                    case "J": ranks[i] = 11; break;
                    case "T": ranks[i] = 10; break;
                    default:
                        ranks[i] = Integer.parseInt( card[ Deck.RANK ] );
                }
                i++;
            }
            
            // Sort by rank to check straights easily
            java.util.Arrays.sort( ranks );
                       
            // check royal flush
            if ( isFlush && ranks[0] == 10 && ranks[1] == 11 && ranks[2] == 12 && 
                    ranks[3] == 13 && ranks[4] == 14 ) {
                rank = 9.14_13_12_11_10;
                debugString.append("Royal Flush");
            } else {
                // check straight

                // normal run or ace low or ace high for straight
                boolean isStraight = ( ranks[ 0 ] == ranks[ 1 ] - 1 && ranks[ 0 ] == ranks[ 2 ] - 2 && 
                                       ranks[ 0 ] == ranks[ 3 ] - 3 && ranks[ 0 ] == ranks[ 4 ] - 4 );
                boolean isAceLow = ( ranks[ 0 ] == 2 && ranks[ 1 ] == 3 && ranks[ 2 ] == 4 && ranks[ 3 ] == 5 &&
                                       ranks[ 4 ] == 14 ) ;

                isStraight |= isAceLow;

                // check straight flush
                if ( isStraight && isFlush ) {
                    // ranks[4] is the high card due to the above sort
                    // unless we are using the ace as the low card
                    debugString.append("Straight Flush - ");

                    if ( isAceLow ) {
                        List<int[]> rankList = Arrays.asList( ranks );
                        Collections.rotate(rankList, 1);
                    }

                    rank = 8 + hand2decimal(ranks);
                    debugString.append(rankToCardRank(ranks[4]));
                    debugString.append(" high");
                } else if ( ( ranks[ 0 ] == ranks[ 3 ] ) || ( ranks[ 1 ] == ranks[ 4 ] ) ) {
                    // check 4 of a kind
                    // 1st 4 or last 4, only need to check the ends
                    // don't need kickers, can't have ties on 4 of a kind.
                    rank = 7 + ( ranks[ 2 ] / 100.0 );

                    debugString.append("4 of a Kind - ");
                    debugString.append( rankToCardRank( ranks[2] ) );
                    debugString.append("'s");
                } else if ( ( ( ranks[ 0 ] == ranks[ 2 ] ) && ( ranks[ 3 ] == ranks[ 4 ] ) ) ||
                         ( ( ranks[ 0 ] == ranks[ 1 ] ) && ( ranks[ 2 ] == ranks[ 4 ] ) ) ) {
                    // check full house
                    // 1st 3 and last 2 pr first 2 and last 3
                    
                    if ( ranks[ 0 ] == ranks[ 2 ] ) {
                        // highest 3 of a kind wins, pair only matters if 3 of a kind is tied
                        rank = 6 + ( ranks[ 2 ] / 100.0 ) + ( ranks[ 3 ] /10000.0 );
                    } else {
                        rank = 6 + ( ranks[ 2 ] / 100.0 ) + ( ranks[ 0 ] /10000.0 );
                    }
                    debugString.append("Full House - ");
                    debugString.append( rankToCardRank(ranks[2]) );
                    debugString.append( "s high.");
                } else if ( isFlush ) {
                    rank = 5 + hand2decimal(ranks);
                    debugString.append("Hand Ranked: Flush - ");
                    debugString.append( rankToCardRank(ranks[4]) );
                    debugString.append( " high in ");
                    debugString.append( Deck.getLongSuit( initialSuit ) );
                } else if ( isStraight ) {
                    if ( isAceLow ) {
                        int temp = ranks[ranks.length-1];
                        for ( int j = ranks.length-1; j > 0; j--)
                            ranks[j] = ranks[j-1];
                        ranks[0]=temp;
                    }

                    rank = 4 + hand2decimal(ranks);
                    debugString.append(rankToCardRank(ranks[4]));
                    debugString.append(" high");
                } else if ( ( ranks[ 0 ] == ranks[ 2 ] ) || ( ranks[ 1 ] == ranks[ 3 ] ) || ranks[ 2 ] == ranks[ 4 ] ) {
                    // 3 of a kind
                    // 012, 123, 234
                    
                    // note that card 2 is in every 3 of a kind
                    // cannot tie on 3 of a kind, kicker not needed
                    rank = 3 + ( ranks[ 2 ] / 100.0 );

                    debugString.append("3 of a Kind - ");
                    debugString.append(rankToCardRank(ranks[2]));
                    debugString.append("s");
                } else {
                    int pairs = 0;
                    int highPair = -1;
                    int lowPair = -1;
                    
                    // not mutually exclusive
                    if ( ranks[ 3 ] == ranks[ 4 ] ) { pairs++; highPair = 4;}
                    if ( ranks[ 2 ] == ranks[ 3 ] ) { pairs++; if ( highPair == - 1 ) highPair = 3; else lowPair = 3;} // 2+3 can only be the high pair?
                    if ( ranks[ 1 ] == ranks[ 2 ] ) { pairs++; if ( highPair == - 1 ) highPair = 2; else lowPair = 2;}
                    if ( ranks[ 0 ] == ranks[ 1 ] ) { pairs++; if ( highPair == - 1 ) highPair = 1; else lowPair = 1;}
                    
                    rank += pairs;
                    switch ( pairs ) {
                        case 2 :
                            debugString.append("2 Pair - ");

                            int kicker=0;
                            if ( highPair == 4 && lowPair == 2 ) kicker = 0; // high is 4/3, low is 2/1
                            else if ( highPair == 4 && lowPair == 1 ) kicker = 2; // high is 4/3, low is 1/0
                            else if ( highPair < 4 ) kicker = 4; // if high is not 4, the kicker is.

                            debugString.append( rankToCardRank(ranks[highPair]));
                            debugString.append( "s over " );
                            debugString.append( rankToCardRank(ranks[lowPair]));
                            debugString.append( "s, with a ");
                            debugString.append( rankToCardRank(ranks[kicker]));
                            debugString.append( " kicker." );

                            rank += ranks[ highPair ] / 100.0 + ranks[ lowPair ] / 10000.0 + ranks[kicker] / 1_000_000.0;
                            break;
                        case 1 :
                            debugString.append("Pair - ");
                            debugString.append( rankToCardRank(ranks[highPair]));
                            debugString.append( "s, with ");

                            String rankStr = "1." + (ranks[highPair] >= 10 ? "" : "0") + Integer.toString(ranks[highPair]);
                            int kickerCount = 1;
                            for ( int j = ranks.length-1 ; j >= 0; j-- )
                                if ( j != highPair && j != highPair -1 ) {
                                    if ( ranks[j] < 10 )
                                        rankStr += "0";
                                    rankStr += Integer.toString(ranks[j]);

                                    debugString.append( rankToCardRank(ranks[j]));
                                    if ( kickerCount < 2 )  debugString.append( ", " );
                                    else if ( kickerCount == 2 ) debugString.append( ", and " );
                                    kickerCount++;
                                }


                            debugString.append( " as kickers." );

                            rank = Double.parseDouble(rankStr);
                            break;
                        default:
                            debugString.append("High Card - ");
                            debugString.append(rankToCardRank(ranks[4]));
                            debugString.append(" with " );

                            kickerCount = 1;

                            for ( int j=ranks.length-2; j>=0; j-- ) {
                                debugString.append(rankToCardRank(ranks[j]));
                                if (kickerCount < 3) debugString.append(", ");
                                else if (kickerCount == 3) debugString.append(", and ");
                                kickerCount++;
                            }

                            debugString.append( " as kickers, ");
                            rank = hand2decimal(ranks);
                    }
                }
            } // if royal
            debugString.append( String.format( " value = %1.10f%n", rank ) );
            if (debug)
                PokerTournament.debugWrite( debugString.toString() );
        } // if 5 cards in hand
        
        return rank;
    }

    public String showTableCards() {
        StringBuilder cards = new StringBuilder();

        for (String[] card : tableCards ) {
            if ( card[0] == null )
                continue;

            cards.append( card[0] );
            cards.append( card[1] );
            cards.append( " " );
        }

        return cards.toString();
    }

    public void bustOut( List<pokerPlayer> players ) {
        // remove busted out players
        for (Iterator<pokerPlayer> iterator = players.iterator(); iterator.hasNext(); ) {
            pokerPlayer p = iterator.next();
            if ( p.getChipTotal() <= 0 ) {
                announce( String.format( "%s has busted at hand %d and must leave the table.", p.name, handsPlayed ), false );
                iterator.remove();
                listeners.remove(p);
                results.put( p.name, handsPlayed );
            }
        }
    }
}
