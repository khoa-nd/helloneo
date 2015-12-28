package com.chrisshayan.examples.reco;

import com.graphaware.reco.generic.engine.RecommendationEngine;
import com.graphaware.reco.generic.filter.BlacklistBuilder;
import com.graphaware.reco.generic.filter.Filter;
import com.graphaware.reco.generic.log.Logger;
import com.graphaware.reco.generic.log.Slf4jRecommendationLogger;
import com.graphaware.reco.generic.log.Slf4jStatisticsLogger;
import com.graphaware.reco.neo4j.engine.Neo4jPrecomputedEngine;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingRecommendationEngine;
import com.graphaware.reco.neo4j.filter.ExcludeSelf;
import com.graphaware.reco.neo4j.filter.ExistingRelationshipBlacklistBuilder;
import org.neo4j.graphdb.Node;

import java.util.Arrays;
import java.util.List;

import static org.neo4j.graphdb.Direction.BOTH;
/**
 * Created by chrisshayan on 12/24/15.
 */
public class FriendsRecommendationEngine extends Neo4jTopLevelDelegatingRecommendationEngine {
    @Override
    protected List<RecommendationEngine<Node, Node>> engines() {
        return Arrays.<RecommendationEngine<Node, Node>>asList(
                new Neo4jPrecomputedEngine(),
                new FriendsComputingEngine()
        );
    }

    @Override
    protected List<BlacklistBuilder<Node, Node>> blacklistBuilders() {
        return Arrays.asList(
                new ExcludeSelf(),
                new ExistingRelationshipBlacklistBuilder(Relationships.FRIEND_OF, BOTH)
        );
    }

    @Override
    protected List<Filter<Node, Node>> filters() {
        return Arrays.<Filter<Node, Node>>asList(
                new ExcludeSelf()
        );
    }

    @Override
    protected List<Logger<Node, Node>> loggers() {
        return Arrays.asList(
                new RecommendationsRememberingLogger(),
                new Slf4jRecommendationLogger<Node, Node>(),
                new Slf4jStatisticsLogger<Node, Node>()
        );
    }
}
