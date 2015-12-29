package com.chrisshayan.examples.reco;


import com.graphaware.reco.generic.config.SimpleConfig;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingRecommendationEngine;
import com.graphaware.reco.neo4j.module.RecommendationModule;
import com.graphaware.reco.neo4j.module.RecommendationModuleConfiguration;
import com.graphaware.runtime.GraphAwareRuntime;
import com.graphaware.runtime.GraphAwareRuntimeFactory;
import com.graphaware.runtime.config.FluentRuntimeConfiguration;
import com.graphaware.runtime.schedule.FixedDelayTimingStrategy;
import com.graphaware.test.integration.WrappingServerIntegrationTest;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chrisshayan on 12/24/15.
 */
public class ModuleIntegrationTest extends WrappingServerIntegrationTest {

    private Neo4jTopLevelDelegatingRecommendationEngine recommendationEngine;
    private RecommendationsRememberingLogger rememberingLogger = new RecommendationsRememberingLogger();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        recommendationEngine = new FriendsRecommendationEngine();
        rememberingLogger.clear();
    }

    @Override
    protected void populateDatabase(GraphDatabaseService database) {
        database.execute(
                "CREATE " +
                        "(m:Person:Male {name:'Michal', age:30})," +
                        "(d:Person:Female {name:'Daniela', age:20})," +
                        "(v:Person:Male {name:'Vince', age:40})," +
                        "(a:Person:Male {name:'Adam', age:30})," +
                        "(l:Person:Female {name:'Luanne', age:25})," +
                        "(b:Person:Male {name:'Christophe', age:60})," +
                        "(j:Person:Male {name:'Jim', age:38})," +

                        "(lon:City {name:'London'})," +
                        "(mum:City {name:'Mumbai'})," +
                        "(br:City {name:'Bruges'})," +

                        "(m)-[:FRIEND_OF]->(d)," +
                        "(m)-[:FRIEND_OF]->(l)," +
                        "(m)-[:FRIEND_OF]->(a)," +
                        "(m)-[:FRIEND_OF]->(v)," +
                        "(d)-[:FRIEND_OF]->(v)," +
                        "(b)-[:FRIEND_OF]->(v)," +
                        "(j)-[:FRIEND_OF]->(v)," +
                        "(j)-[:FRIEND_OF]->(m)," +
                        "(j)-[:FRIEND_OF]->(a)," +
                        "(a)-[:LIVES_IN]->(lon)," +
                        "(d)-[:LIVES_IN]->(lon)," +
                        "(v)-[:LIVES_IN]->(lon)," +
                        "(m)-[:LIVES_IN]->(lon)," +
                        "(j)-[:LIVES_IN]->(lon)," +
                        "(c)-[:LIVES_IN]->(br)," +
                        "(l)-[:LIVES_IN]->(mum)");
    }

    @Test
    public void shouldRecommendRealTime() {
        try(Transaction tx = getDatabase().beginTx()) {

            // verifying Vince
            List<Recommendation<Node>> recoForVince = recommendationEngine.recommend(getPersonByName("Vince"), new SimpleConfig(2));
            assertNotNull(recoForVince);
            assertTrue(recoForVince.size() == 2);
            assertEquals("Adam", recoForVince.get(0).getItem().getProperty("name"));
            assertEquals("Luanne", recoForVince.get(1).getItem().getProperty("name"));

            // verifying Adam
            List<Recommendation<Node>> adamRecoList = recommendationEngine.recommend(getPersonByName("Adam"), new SimpleConfig(2));
            assertNotNull(adamRecoList);
            assertTrue(adamRecoList.size() == 2);
            assertEquals("Vince", adamRecoList.get(0).getItem().getProperty("name"));
            assertEquals("Daniela", adamRecoList.get(1).getItem().getProperty("name"));

            // verifying Luanne
            List<Recommendation<Node>> luanneRecoList = recommendationEngine.recommend(getPersonByName("Luanne"), new SimpleConfig(4));
            assertNotNull(luanneRecoList);
            assertTrue(luanneRecoList.size() == 4);
            assertEquals("Daniela", luanneRecoList.get(0).getItem().getProperty("name"));
            assertEquals(14, luanneRecoList.get(0).getScore().getTotalScore(), 0.5);

            assertEquals("Vince", luanneRecoList.get(1).getItem().getProperty("name"));
            assertEquals(8, luanneRecoList.get(1).getScore().getTotalScore(), 0.5);

            assertEquals("Jim", luanneRecoList.get(2).getItem().getProperty("name"));
            assertEquals(7, luanneRecoList.get(2).getScore().getTotalScore(), 0.5);

            assertEquals("Adam", luanneRecoList.get(3).getItem().getProperty("name"));
            assertEquals(4, luanneRecoList.get(3).getScore().getTotalScore(), 0.5);


            tx.success();
        }
    }

    private Node getPersonByName(String name) {
        return getDatabase().findNode(DynamicLabel.label("Person"), "name", name);
    }
}
