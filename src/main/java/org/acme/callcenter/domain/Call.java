package org.acme.callcenter.domain;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

import org.acme.callcenter.solver.ResponseTimeUpdatingVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

@PlanningEntity
public class Call extends PreviousCallOrAgent {

    private String phoneNumber;
    private Skill requiredSkill;
    private Duration duration = Duration.ZERO;
    private LocalTime startTime;
    private LocalTime pickUpTime;

    @PlanningPin
    private boolean pinned;

    @JsonIgnore
    @PlanningVariable(valueRangeProviderRefs = { "agentRange", "callRange" }, graphType = PlanningVariableGraphType.CHAINED)
    private PreviousCallOrAgent previousCallOrAgent;

    @JsonIgnore
    @AnchorShadowVariable(sourceVariableName = "previousCallOrAgent")
    private Agent agent;

    @CustomShadowVariable(variableListenerClass = ResponseTimeUpdatingVariableListener.class,
            sources = { @PlanningVariableReference(variableName = "previousCallOrAgent") })
    private Duration estimatedWaiting;

    public Call() {
        // Required by OptaPlanner.
    }

    public Call(long id, String phoneNumber) {
        super(id);
        this.phoneNumber = phoneNumber;
        this.requiredSkill = null;
        this.startTime = LocalTime.now();
    }

    public Call(long id, String phoneNumber, Skill requiredSkill, int durationSeconds) {
        super(id);
        this.phoneNumber = phoneNumber;
        this.requiredSkill = requiredSkill;
        this.duration = Duration.ofSeconds(durationSeconds);
        this.startTime = LocalTime.now();
    }

    public Call(long id, String phoneNumber, Skill requiredSkill) {
        this(id, phoneNumber);
        this.requiredSkill = requiredSkill;
    }

    public int getMissingSkillCount() {
        if (agent == null) {
            return 0;
        }

        return agent.getSkill() != requiredSkill ? 1 :0;
    }

    @Override
    public Duration getDurationTillPickUp() {
        Duration durationTillPickUp;
        if (estimatedWaiting == null) {
            return null;
        } else {
            durationTillPickUp = estimatedWaiting.plus(getDuration());
            if (pickUpTime != null) {
                durationTillPickUp = durationTillPickUp.minus(Duration.between(pickUpTime, LocalTime.now()));
            }
        }
        return durationTillPickUp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Skill getRequiredSkill() {
        return requiredSkill;
    }

    @Override
    public Skill getSkill() {
        return requiredSkill;
    }

    public boolean isPinned() {
        return pinned;
    }

    public PreviousCallOrAgent getPreviousCallOrAgent() {
        return previousCallOrAgent;
    }

    public Agent getAgent() {
        return agent;
    }

    public Duration getEstimatedWaiting() {
        return estimatedWaiting;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public void setPreviousCallOrAgent(PreviousCallOrAgent previousCallOrAgent) {
        this.previousCallOrAgent = previousCallOrAgent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setEstimatedWaiting(Duration estimatedWaiting) {
        this.estimatedWaiting = estimatedWaiting;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getPickUpTime() {
        return pickUpTime;
    }

    public void setPickUpTime(LocalTime pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Call))
            return false;
        Call call = (Call) o;
        return getId().equals(call.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
