/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.modeshape.jcr;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.ImmutableList.builder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modeshape.jcr.JcrRepository.RunningState;
import org.modeshape.jcr.api.sequencer.Sequencer;

import com.google.common.collect.ImmutableList.Builder;

public class RunningStateProxy {

    private final RunningState state;

    public static RunningState getRunningState(final JcrRepository repo) {
        return repo.runningState();
    }

    public RunningStateProxy(final JcrRepository repo) {
        this.state = repo.runningState();
    }

    public Sequencers getSequencers() {
        return state.sequencers();
    }

    public <T extends Sequencer> List<T> getSequencersByType(
            final Class<T> clazz) {
        final Builder<T> result = builder();

        try {
            final Field sequencers =
                    Sequencers.class.getDeclaredField("sequencersById");
            sequencers.setAccessible(true);
            @SuppressWarnings("unchecked")
            final Map<UUID, T> map =
                    (Map<UUID, T>) sequencers.get(getSequencers());
            for (final T s : map.values()) {
                if (clazz.isAssignableFrom(s.getClass())) {
                    result.add(s);
                }
            }
        } catch (final Exception e) {
            throw propagate(e);
        }
        return result.build();
    }
}
