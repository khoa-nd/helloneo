/*
 * Copyright (c) 2013-2015 GraphAware
 *
 * This file is part of the GraphAware Framework.
 *
 * GraphAware Framework is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package com.chrisshayan.examples.reco;

import com.chrisshayan.examples.reco.recruiting.RecommendationVO;
import com.chrisshayan.examples.reco.recruiting.RecruitingRecommendationEngine;
import com.chrisshayan.examples.reco.recruiting.RecruitingRelationshipType;
import com.graphaware.reco.generic.config.KeyValueConfig;
import com.graphaware.reco.generic.result.Recommendation;
import com.graphaware.reco.generic.web.ConfigParser;
import com.graphaware.reco.generic.web.KeyValueConfigParser;
import com.graphaware.reco.neo4j.engine.Neo4jTopLevelDelegatingRecommendationEngine;
import com.graphaware.test.data.DatabasePopulator;
import com.graphaware.test.data.GraphgenPopulator;
import com.graphaware.test.integration.WrappingServerIntegrationTest;
import org.junit.Test;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.graphaware.common.util.IterableUtils.getSingle;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * A demonstration of an end-to-end recommendation engine. Mostly for showcasing and documenting functionality rather
 * than functional testing.
 */
public class RecruitingRecommendationEngineTest extends WrappingServerIntegrationTest {

    private Neo4jTopLevelDelegatingRecommendationEngine recommendationEngine;
    private RecommendationsRememberingLogger rememberingLogger = new RecommendationsRememberingLogger();
    private final ConfigParser<KeyValueConfig> parser = new KeyValueConfigParser(":");

    @Override
    public void setUp() throws Exception {
        super.setUp();
        recommendationEngine = new RecruitingRecommendationEngine();
        rememberingLogger.clear();
    }

    @Override
    protected DatabasePopulator databasePopulator() {
        return new GraphgenPopulator() {
            @Override
            protected String file() throws IOException {
                return new ClassPathResource("RecruitingGraphData.cyp").getFile().getAbsolutePath();
            }
        };
    }

    @Test
    public void shouldRecommendPersonForCompanyWhoHasBestRequiredSkillSet() throws IOException {
        String companyName = "Tromp Inc";
        int RECOMMENDED_ITEM_LIMIT = 10;
        String RECOMMENDATION_CONFIGURATION = "";
        KeyValueConfig keyValueConfig = parser.produceConfig(RECOMMENDED_ITEM_LIMIT, RECOMMENDATION_CONFIGURATION);

        try (Transaction tx = getDatabase().beginTx()) {
            Node company = findCompanyByName(companyName);
            List<String> skills = getRequiredSkillSetByCompany(company);
            System.out.println(companyName + " is looking for these skills : " + skills);

            List<Recommendation<Node>> recommendForCompany = recommendationEngine.recommend(company, keyValueConfig);
            assertNotNull(recommendForCompany);
            assertTrue(recommendForCompany.size() == RECOMMENDED_ITEM_LIMIT);

            List<RecommendationVO> recommendationVOs = convert(recommendForCompany);
            for (RecommendationVO recommendationVO : recommendationVOs) {
                System.out.println(recommendationVO);
                // The number of skills of recommended person should be greater than 3/4 the number of required skills by company
                assertTrue(recommendationVO.getScore().getTotalScore() > skills.size() * 0.75);
            }
            tx.success();
        }
    }

    private List<String> getRequiredSkillSetByCompany(Node company) {
        Iterator<Relationship> iterator = company.getRelationships(RecruitingRelationshipType.LOOKS_FOR_SKILL).iterator();

        List<String> skills = new ArrayList<>();
        while (iterator.hasNext()) {
            Node skillNode = iterator.next().getEndNode();
            skills.add(skillNode.getProperty("name").toString());
        }
        return skills;
    }

    private Node findCompanyByName(String name) {
        return getSingle(getDatabase().findNodes(DynamicLabel.label("Company"), "name", name), "Company with name " + name + " does not exist.");
    }

    private List<RecommendationVO> convert(List<Recommendation<Node>> recommendations) {
        List<RecommendationVO> result = new LinkedList<>();

        for (Recommendation<Node> recommendation : recommendations) {
            result.add(new RecommendationVO(recommendation.getUuid(), recommendation.getItem().getProperty("name", "unknown").toString(), recommendation.getScore()));
        }

        return result;
    }

}
