package com.chrisshayan.examples.reco;

import com.graphaware.reco.generic.transform.ParetoScoreTransformer;
import com.graphaware.reco.generic.transform.ScoreTransformer;
import com.graphaware.reco.neo4j.engine.SomethingInCommon;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import java.util.Collections;
import java.util.Map;

/**
 * Created by chrisshayan on 12/24/15.
 */
public class FriendsInCommon extends SomethingInCommon {

    private ScoreTransformer scoreTransformer = ParetoScoreTransformer.create(100, 10);

    @Override
    protected RelationshipType getType() {
        return Relationships.FRIEND_OF;
    }

    @Override
    protected Direction getDirection() {
        return Direction.BOTH;
    }

    @Override
    public String name() {
        return "friendInCommon";
    }

    @Override
    protected ScoreTransformer<Node> scoreTransformer() {
        return super.scoreTransformer();
    }

    @Override
    protected Map<String, Object> details(Node thingInCommon, Relationship withInput, Relationship withOutput) {
        return Collections.singletonMap("name", thingInCommon.getProperty("name"));
    }
}
