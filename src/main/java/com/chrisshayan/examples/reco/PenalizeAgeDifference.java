package com.chrisshayan.examples.reco;

import com.graphaware.reco.generic.context.Context;
import com.graphaware.reco.generic.post.BasePostProcessor;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.reco.generic.result.Recommendations;
import com.graphaware.reco.generic.transform.ParetoFunction;
import com.graphaware.reco.generic.transform.TransformationFunction;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import static com.graphaware.common.util.PropertyContainerUtils.getInt;

/**
 * Created by chrisshayan on 12/24/15.
 */
public class PenalizeAgeDifference extends BasePostProcessor<Node, Node> {

    private final TransformationFunction function = new ParetoFunction(10, 20);

    @Override
    protected String name() {
        return "ageDifference";
    }

    @Override
    protected void doPostProcess(Recommendations<Node> recommendations, Node input, Context<Node, Node> context) {
        int age = getInt(input, "age", 40);

        for(Recommendation<Node> recommendation: recommendations.get()) {
            int diff = Math.abs(getInt(recommendation.getItem(), "age", 40) - age);
            recommendation.add(name(), function.transform(diff));
        }
    }
}
