package org.drools.ruleops;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.ruleops.model.Advice;

@Path("/advices")
public class AdviceResource {

    @Inject
    DroolsSingleton drools;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Advice> hello() {
        return drools.evaluateAllRulesStateless();
    }
}
