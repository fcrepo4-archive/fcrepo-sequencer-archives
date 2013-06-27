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

package org.fcrepo.events;

import static org.modeshape.jcr.api.observation.Event.Sequencing.SEQUENCED_NODE_PATH;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.modeshape.jcr.api.observation.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequencingTestListener implements EventListener {

    private static Logger LOG = LoggerFactory
            .getLogger(SequencingTestListener.class);

    private ConcurrentHashMap<String, Event> sequencingEvents;

    public SequencingTestListener(
            ConcurrentHashMap<String, Event> sequencingEvents) {
        this.sequencingEvents = sequencingEvents;
    }

    @Override
    public void onEvent(EventIterator events) {
        while (events.hasNext()) {
            try {
                Event event = (Event) events.nextEvent();
                LOG.info("Received event: " + event.toString());
                Map<?, ?> info = event.getInfo();
                for (Object key : info.keySet()) {
                    LOG.debug("EVENT INFO: " + key + " => " + info.get(key));
                }
                sequencingEvents.putIfAbsent((String) event.getInfo().get(
                        SEQUENCED_NODE_PATH), event);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}
