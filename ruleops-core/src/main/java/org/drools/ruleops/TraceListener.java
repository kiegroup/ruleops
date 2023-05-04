package org.drools.ruleops;

import org.drools.core.event.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.ObjectUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TraceListener extends DefaultRuleRuntimeEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TraceListener.class);

    @Override
    public void objectDeleted(ObjectDeletedEvent event) {
        LOG.trace("<<< DELETED: {}", event.getOldObject());
    }

    @Override
    public void objectInserted(ObjectInsertedEvent event) {
        LOG.trace(">>> INSERTED: {}", event.getObject());
    }

    @Override
    public void objectUpdated(ObjectUpdatedEvent event) {
        LOG.trace("><> UPDATED: {}", event.getObject());
    }
}