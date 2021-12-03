package org.acme.callcenter.solver;

import org.acme.callcenter.domain.Call;
import org.acme.callcenter.domain.Skill;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class CallCenterConstraintsProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                noRequiredSkillMissing(constraintFactory),
                minimizeWaitingTime(constraintFactory),
                minimizeSetupChanges(constraintFactory),
        };
    }

    Constraint noRequiredSkillMissing(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Call.class)
                .filter(call -> call.getMissingSkillCount() > 0)
                .penalize("No required skills are missing", HardSoftScore.ONE_HARD, call -> call.getMissingSkillCount());
    }

    Constraint minimizeWaitingTime(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Call.class)
                .filter(call -> call.getNextCall() == null)
                .penalize("Minimize waiting time",
                        HardSoftScore.ONE_SOFT, call -> Math.toIntExact(call.getEstimatedWaiting().getSeconds()
                                * call.getEstimatedWaiting().getSeconds()));
    }


    Constraint minimizeSetupChanges(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Call.class)
//                .filter(call -> call.getPreviousCallOrAgent())
                .penalize("Minimize Agent Setup",
                        HardSoftScore.ONE_SOFT, call -> {
                            Skill from = call.getPreviousCallOrAgent().getSkill();
                            Skill to = call.getRequiredSkill();

                            return Math.toIntExact(calculateSetupCost(from, to));
                        });
    }

    public static int calculateSetupCost(Skill from, Skill to){
        // admittedly a crude cost matrix, but good enough for a PoC
//        return Math.abs(from.getName().length() - to.getName().length());
        return from.equals(to) ? 0 : 5;
    }
}
