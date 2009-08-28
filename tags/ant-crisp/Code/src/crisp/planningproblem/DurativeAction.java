package crisp.planningproblem;

import crisp.planningproblem.goal.Goal;
import crisp.planningproblem.effect.Effect;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * This class adds durations to the representation of a planning action.
 *
 * @author Daniel Bauer
 */

public class DurativeAction extends Action {
    
    private double duration;
    public DurativeAction(Predicate label, Goal precondition, Effect effect, HashMap<String, String> constants, ArrayList<Predicate> predicates, double duration) {
        super(label, precondition, effect, constants, predicates);
        this.duration = duration;
    }

    public double getDuration(){
        return duration;
    }
}