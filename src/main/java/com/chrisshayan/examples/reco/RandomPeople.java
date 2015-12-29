package com.chrisshayan.examples.reco;

import com.graphaware.common.policy.BaseNodeInclusionPolicy;
import com.graphaware.common.policy.NodeInclusionPolicy;
import com.graphaware.reco.generic.context.Context;
import com.graphaware.reco.generic.policy.ParticipationPolicy;
import com.graphaware.reco.neo4j.engine.RandomRecommendations;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;

/**
 * Created by chrisshayan on 12/24/15.
 */
public class RandomPeople extends RandomRecommendations {
    @Override
    protected NodeInclusionPolicy getPolicy() {
        return new BaseNodeInclusionPolicy() {
            @Override
            public boolean include(Node node) {
                return node.hasLabel(DynamicLabel.label("Person"));
            }
        };
    }

    @Override
    public String name() {
        return "random";
    }

    @Override
    protected int numberOfRecommendations(Context<Node, Node> context) {
        return context.config().limit() * 5;
    }

    @Override
    public ParticipationPolicy<Node, Node> participationPolicy(Context context) {
        try {
            Thread.sleep(1); // waste 1 ms
        } catch (InterruptedException e) {
            // do nothing
        }

        return ParticipationPolicy.IF_MORE_RESULTS_NEEDED_AND_ENOUGH_TIME;
    }
}
