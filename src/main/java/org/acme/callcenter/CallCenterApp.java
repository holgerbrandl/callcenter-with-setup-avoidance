package org.acme.callcenter;

import org.acme.callcenter.data.DataGenerator;
import org.acme.callcenter.domain.Agent;
import org.acme.callcenter.domain.Call;
import org.acme.callcenter.domain.CallCenter;
import org.acme.callcenter.domain.PreviousCallOrAgent;
import org.acme.callcenter.solver.CallCenterConstraintsProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.time.Duration;
import java.util.stream.*;

public class CallCenterApp {
    public static void main(String[] args) {

        DataGenerator dataGenerator = new DataGenerator();

        CallCenter callCenter = dataGenerator.generateCallCenter();

//        Call call2 = new Call(2L, "123-456-7892", Skill.CAR_INSURANCE);
        for (int i = 0; i < 30; i++) {
            callCenter.getCalls().add(dataGenerator.generateCall(30));
        }

        SolverFactory<CallCenter> solverFactory = SolverFactory.create(new SolverConfig()
                .withSolutionClass(CallCenter.class)
                .withEntityClasses(Call.class, PreviousCallOrAgent.class)
//                .withDomainAccessType(DomainAccessType.GIZMO)
                .withConstraintProviderClass(CallCenterConstraintsProvider.class)
                // The solver runs only for 5 seconds on this small dataset.
                // It's recommended to run for at least 5 minutes ("5m") otherwise.
                .withTerminationSpentLimit(Duration.ofSeconds(10)));

        Solver<CallCenter> solver = solverFactory.buildSolver();

        CallCenter bestSolution = solver.solve(callCenter);


        // print the solution
        for (Agent agent : bestSolution.getAgents()) {
            System.out.print(agent.getName()+": ");
            String callSeq = agent.getAssignedCalls().stream().map(it ->
                    it.getSkill().getName().substring(0, 1)
            ).collect(Collectors.joining());

            System.out.println(callSeq);
        }
    }
}
