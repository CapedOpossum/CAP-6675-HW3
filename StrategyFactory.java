/**
 * Factory to create instances of all available strategies.
 *
 * Strategies may be created by using their identifying character string name.
 *
 * @author Rolando J. Nieves
 */
public class StrategyFactory {
    public static Strategy createFromName(String strategyName) {
        if (strategyName.equals("Random")) {
            return new StrategyRandom();
        } else if (strategyName.equals("AlwaysCooperate")) {
            return new StrategyAlwaysCooperate();
        } else if (strategyName.equals("AlwaysDefect")) {
            return new StrategyAlwaysDefect();
        } else if (strategyName.equals("TitForTat")) {
            return new StrategyTitForTat();
        } else if (strategyName.equals("TitForTwoTats")) {
            return new StrategyTitForTwoTats();
        } else if (strategyName.equals("WeightedTree")) {
            return new StrategyWeightedTree();
        } else {
            return null;
        }
    } // end createFromName()
} // end StrategyFactory

// vim: set ts=4 sw=4 expandtab:
