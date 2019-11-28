# UCF Fall 2019 CAP 6675 Homework #3: Iterated Prisoners Dilemma

This repository contains the work done by our team in response to homework
assignment #3 in Dr. Annie Wu's Fall 2019 session of CAP 6675: Complex Adaptive
Systems.

The majority of the code was as given to the team by Dr. Wu. The primary
contribution from our team is the addition of the class `StrategyWeightedTree`.

## Compiling

To compile, simply run `javac` against the `RunIPD.java` class:

    javac ./RunIPD.java

After repeated code edits, it may be necessary to erase all `.class` files, as
the bare `javac` compiler has some trouble properly identifying dependencies.

## Running

The baseline version of this software only required a single command-line
argument when running: the number of iterations to do. That baseline version
required code changes in order to try different agent strategies. The team made
some modifications to the baseline so that the strategy for player 1 and player
2 can be specified at the command line. Each strategy was given a character
string label for use with these new command line arguments. The general
invocation form is as follows (assuming it is run from the top of the source
tree):

    java -cp . RunIPD -l <iterations> -p1 <p1 strategy> -p2 <p2 strategy>

Where:

`<iterations>`
: Number of iterations to run. Must be a positive integer.

`<p1 strategy>`
: Strategy that player 1 will use.

`<p2 strategy>`
: Strategy that player 2 will use.

The list of strategies available include the following:

* `Random` - A strategy that chooses a move at random.
* `AlwaysCooperate` - Always selects the `COOPERATE` action.
* `AlwaysDefect` - Always selects the `DEFECT` action.
* `TitForTat` - Selects the same action the opponent selected in the last
  iteration.
* `TitForTwoTats` - Selects the `DEFECT` action when the opponent chooses
  `DEFECT` at least twice.
* `WeightedTree` - Does an *Expectimax-like* tree search through the possible
  outcomes of the next five (5) iterations, selecting the action with the
  highest most likely outcome, weighed using the apparent preference of the
  opponent given their move history.

