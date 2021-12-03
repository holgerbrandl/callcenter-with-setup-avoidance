package org.acme.callcenter.domain;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Agent extends PreviousCallOrAgent {

    private String name;
    private Skill skill;

    public Agent() {
        // Required by OptaPlanner.
    }

    public Agent(long id, String name, Skill skill) {
        super(id);
        this.name = name;
        this.skill = skill;
    }

    @JsonProperty(value = "calls", access = JsonProperty.Access.READ_ONLY)
    public List<Call> getAssignedCalls() {
        Call nextCall = getNextCall();
        List<Call> assignedCalls = new ArrayList<>();
        while (nextCall != null) {
            assignedCalls.add(nextCall);
            nextCall = nextCall.getNextCall();
        }
        return assignedCalls;
    }

    @Override
    public Duration getDurationTillPickUp() {
        return Duration.ZERO;
    }

    public String getName() {
        return name;
    }

    public Skill getSkill() {
        return skill;
    }
}
