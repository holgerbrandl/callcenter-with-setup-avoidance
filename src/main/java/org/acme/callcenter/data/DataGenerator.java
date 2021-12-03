/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.callcenter.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;

import org.acme.callcenter.domain.Agent;
import org.acme.callcenter.domain.Call;
import org.acme.callcenter.domain.CallCenter;
import org.acme.callcenter.domain.Skill;

@ApplicationScoped
public class DataGenerator {

    private static Random r = new Random(2);

    private static final AtomicLong NEXT_ID = new AtomicLong(0L);

    private final Random RANDOM = new Random(37);

    private static final Agent[] AGENTS = new Agent[] {
            new Agent(nextId(), "Ann", randomSkill()),
            new Agent(nextId(), "Beth", randomSkill()),
            new Agent(nextId(), "Carl", randomSkill()),
            new Agent(nextId(), "Dennis", randomSkill()),
//            new Agent(nextId(), "Elsa", randomSkill()),
//            new Agent(nextId(), "Francis", randomSkill()),
//            new Agent(nextId(), "Gus", randomSkill()),
//            new Agent(nextId(), "Hugo", randomSkill())
    };


    private static Skill randomSkill() {
        return Arrays.asList(Skill.values()).get(r.nextInt(Skill.values().length-1));
    }

    public CallCenter generateCallCenter() {
        return new CallCenter(EnumSet.allOf(Skill.class), Arrays.asList(AGENTS), new ArrayList<>());
    }

    public Call generateCall(int durationSeconds) {
        return new Call(nextId(), generatePhoneNumber(), randomSkill(),
                durationSeconds);
    }


    private synchronized String generatePhoneNumber() {
        int firstGroup = RANDOM.nextInt(1_000);
        int secondGroup = RANDOM.nextInt(1_000);
        int thirdGroup = RANDOM.nextInt(10_000);
        return firstGroup + "-" + secondGroup + "-" + thirdGroup;
    }

    private static long nextId() {
        return NEXT_ID.getAndIncrement();
    }
}
