package com.chrisshayan.examples.reco;

import com.graphaware.reco.generic.result.PartialScore;
import com.graphaware.reco.neo4j.post.RewardSomethingShared;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;

import java.util.Collections;

/**
 * Created by chrisshayan on 12/24/15.
 */
public class RewardSameLocation extends RewardSomethingShared {
    @Override
    protected RelationshipType type() {
        return Relationships.LIVES_IN;
    }

    @Override
    protected Direction direction() {
        return Direction.OUTGOING;
    }

    @Override
    protected PartialScore partialScore(Node recommendation, Node input, Node sharedThing) {
        return new PartialScore(10, Collections.singletonMap("location", sharedThing.getProperty("name")));
    }

    @Override
    protected String name() {
        return "sameLocation";
    }
}
