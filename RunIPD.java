/**
 * General class containing main program to run the
 * iterated Prisoner's Dilemma (IPD).
 * @author	081028AW
 */
public class RunIPD extends Object {
    /**
     * Main program to start IPD program.
     */

    public static void main(String args[]) {
        int i;
        int maxSteps = 0;

        Strategy player1 = null;
        Strategy player2 = null;
        IteratedPD ipd;

        for (i = 0; i < args.length; i++) {
            /* check parameters */
            if (args[i].equals("-l") || args[i].equals("-L")) {
                maxSteps = Integer.parseInt(args[++i]);
                System.out.println(" Max steps = " + maxSteps);
            } else if (args[i].equals("-p1") || args[i].equals("-P1")) {
                player1 = StrategyFactory.createFromName(
                    args[++i]
                );
            } else if (args[i].equals("-p2") || args[i].equals("-P2")) {
                player2 = StrategyFactory.createFromName(
                    args[++i]
                );
            }
        }

        if (null == player1) {
            System.err.println("Invalid strategy for P1.");
            System.exit(-1);
        }

        if (null == player2) {
            System.err.println("Invalid strategy for P2.");
            System.exit(-1);
        }

        ipd = new IteratedPD(player1, player2);

        ipd.runSteps(maxSteps);

        System.out.printf(" Player 1 score = %d\n", ipd.player1Score());
        System.out.printf(" Player 2 score = %d\n", ipd.player2Score());

    } /* main */
} /* class RunIPD */
