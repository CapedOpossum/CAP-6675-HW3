/**
 * Evolving player action biases.
 * @author Rolando J. Nieves
 */

class ActionSet {
    public static final int NONE = -1;
    public static final int DEFECT = 0;
    public static final int COOPERATE = 1;

    public static String ActionToString(int action) {
        if (ActionSet.NONE == action) {
            return "NONE";
        } else if (ActionSet.DEFECT == action) {
            return "DEFECT";
        } else if (ActionSet.COOPERATE == action) {
            return "COOPERATE";
        } else {
            return "Unknown<" + action + ">";
        }
    }
};

class ActionBias {
    private float defectBias;
    private float cooperateBias;

    public ActionBias() {
        this.defectBias = 0.5f;
        this.cooperateBias = 0.5f;
    }

    public ActionBias(ActionBias other) {
        this.defectBias = other.defectBias;
        this.cooperateBias = other.cooperateBias;
    }

    public float getDefectBias() {
        return this.defectBias;
    }

    public float getCooperateBias() {
        return this.cooperateBias;
    }

    public void favorAction(int anAction) {
        if (ActionSet.DEFECT == anAction) {
            this.defectBias = Math.min(this.defectBias + 0.01f, 1.0f);
            this.cooperateBias = 1.0f - this.defectBias;
        } else if (ActionSet.COOPERATE == anAction) {
            this.cooperateBias = Math.min(this.cooperateBias + 0.01f, 1.0f);
            this.defectBias = 1.0f - this.cooperateBias;
        }
    }

    public boolean isValid() {
        return (this.cooperateBias + this.defectBias) == 1.0f;
    }
}

class ProbabilityMatrix {
    private ActionBias rowBias;
    private ActionBias columnBias;

    public ProbabilityMatrix() {
        this.rowBias = new ActionBias();
        this.columnBias = new ActionBias();
    }

    public ProbabilityMatrix(ActionBias columnBias) {
        this.rowBias = new ActionBias();
        this.columnBias = new ActionBias(columnBias);
    }

    public ActionBias getColumnBias() {
        return this.columnBias;
    }

    public ProbabilityMatrix generateUpdate(int columnAction) {
        ProbabilityMatrix result = new ProbabilityMatrix();
        result.getColumnBias().favorAction(columnAction);

        return result;
    }
}

class ValueActionPair {
    private float value;
    private int action;

    public ValueActionPair(float value, int action) {
        this.value = value;
        this.action = action;
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public boolean betterThan(ValueActionPair other) {
        return this.value >= other.value;
    }

    public void updateWithBias(ActionBias theBias) {
        if (ActionSet.DEFECT == this.action) {
            this.value *= theBias.getDefectBias();
        } else if (ActionSet.COOPERATE == this.action) {
            this.value *= theBias.getCooperateBias();
        }
    }

    public String toString() {
        return "action: " + ActionSet.ActionToString(this.action) + ", value: " + this.value;
    }
}

class GameState {
    public static final float T = 7.0f;
    public static final float R = 5.0f;
    public static final float P = 3.0f;
    public static final float S = 1.0f;

    private float totalScore;

    public GameState() {
        this.totalScore = 0.0f;
    }

    public GameState(float initialScore) {
        this.totalScore = initialScore;
    }

    public float getTotalScore() {
        return this.totalScore;
    }

    public GameState generateSuccessor(int ourAction, int theirAction) {
        if ((ActionSet.DEFECT == ourAction) && (ActionSet.DEFECT == theirAction)) {
            return new GameState(this.totalScore + GameState.P);
        } else if ((ActionSet.DEFECT == ourAction) && (ActionSet.COOPERATE == theirAction)) {
            return new GameState(this.totalScore + GameState.T);
        } else if ((ActionSet.COOPERATE == ourAction) && (ActionSet.DEFECT == theirAction)) {
            return new GameState(this.totalScore + GameState.S);
        } else if ((ActionSet.COOPERATE == ourAction) && (ActionSet.COOPERATE == theirAction)) {
            return new GameState(this.totalScore + GameState.R);
        }
        throw new java.lang.RuntimeException(
            "Cannot generate successor for our (" +
            ActionSet.ActionToString(ourAction) +
            ") or their (" +
            ActionSet.ActionToString(theirAction) + ")"
        );
    }
}

/**
 * Player strategy based on a weighted tree search.
 * @author Rolando J. Nieves
 */
public class StrategyWeightedTree extends Strategy {

    private GameState actualGameState;
    private ActionBias oppoBias;

    public StrategyWeightedTree() {
        this.name = "WeightedTree";
        this.actualGameState = new GameState();
        this.oppoBias = new ActionBias();
    }

    public int nextMove() {
        ValueActionPair moveEval = this.evaluateNode(
            this.actualGameState,
            new ProbabilityMatrix(this.oppoBias),
            5
        );
        System.out.println("=== My next move will be " + ActionSet.ActionToString(moveEval.getAction()));
        return moveEval.getAction();
    }

    public void saveOpponentMove(int move) {
        super.saveOpponentMove(move);
        this.oppoBias.favorAction(move);
        this.actualGameState = this.actualGameState.generateSuccessor(
            this.getMyLastMove(),
            this.getOpponentLastMove()
        );
    }

    private ValueActionPair evaluateNode(GameState nodeState, ProbabilityMatrix startingProbs, int depth) {
        int coreActionSet[] = { ActionSet.DEFECT, ActionSet.COOPERATE };

        if (depth == 0) {
            return new ValueActionPair(nodeState.getTotalScore(), ActionSet.NONE);
        }
        ValueActionPair result = new ValueActionPair(Float.NEGATIVE_INFINITY, ActionSet.NONE);
        for (int ourAction : coreActionSet) {
            for (int theirAction : coreActionSet) {
                GameState successor = nodeState.generateSuccessor(
                    ourAction,
                    theirAction
                );
                ProbabilityMatrix nextProbs = startingProbs.generateUpdate(theirAction);
                System.out.println(
                    "At depth (" +
                    (5 - depth) +
                    ") the action pair (" +
                    ActionSet.ActionToString(ourAction) +
                    "," +
                    ActionSet.ActionToString(theirAction) +
                    ") is being evaluated."
                );
                ValueActionPair childResult = this.evaluateNode(successor, nextProbs, depth - 1);
                childResult.updateWithBias(startingProbs.getColumnBias());
                childResult.setAction(ourAction);
                if (childResult.betterThan(result)) {
                    System.out.println("New eval " + childResult + " better than current " + result);
                    result = childResult;
                } else {
                    System.out.println("Current " + result + " better than new eval " + childResult);
                }
            }
        }

        return result;
    }
}
