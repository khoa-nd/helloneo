package com.chrisshayan.examples.reco;

import com.graphaware.reco.generic.context.Context;
import com.graphaware.reco.generic.post.BasePostProcessor;
import com.graphaware.reco.generic.result.Recommendations;
import com.graphaware.reco.generic.result.Recommendation;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.Arrays;

import static org.neo4j.helpers.collection.Iterables.toArray;


/**
 * Created by chrisshayan on 12/24/15.
 */
public class RewardSameLabels extends BasePostProcessor<Node, Node> {
    @Override
    protected String name() {
        return "sameGender";
    }

    @Override
    protected void doPostProcess(Recommendations<Node> recommendations, Node input, Context<Node, Node> context) {
        Label[] inputLabels = toArray(Label.class, input.getLabels());

        for (Recommendation<Node> recommendation : recommendations.get()) {
            if(Arrays.equals(inputLabels, toArray(Label.class, recommendation.getItem().getLabels()))) {
                recommendation.add(name(), 10);
            }
        }
    }
}
