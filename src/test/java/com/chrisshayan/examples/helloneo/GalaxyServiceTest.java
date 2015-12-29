package com.chrisshayan.examples.helloneo;

import com.chrisshayan.examples.helloneo.domain.World;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by chris on 12/21/15.
 */

@ContextConfiguration("classpath:/spring/helloWorldContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class GalaxyServiceTest {

    @Autowired
    private GalaxyService galaxyService;

    @Autowired
    private Neo4jTemplate neo4jTemplate;

    @Rollback(false)
    @BeforeTransaction
    public void cleanUpGraph() {
//        Neo4jHelper.cleanDb(neo4jTemplate);
    }

    @Test
    public void shouldAllowDirectWorldCreation() {
        assertEquals(0, galaxyService.getWorldCount().longValue());
        final World mine = galaxyService.createWorld("mine", 0);
        assertEquals(1, galaxyService.getWorldCount().longValue());

        final Iterable<World> allWorlds = galaxyService.getAllWorlds();
        final World myWorld = allWorlds.iterator().next();
        assertEquals(mine.getName(), myWorld.getName());
    }

    @Test
    public void shouldHaveCorrectNumberOfWorlds() {
        galaxyService.makeSomeWorlds();
        assertEquals(13, galaxyService.getWorldCount().longValue());
    }

    @Test
    public void shouldFindWorldsById() {
        galaxyService.makeSomeWorlds();

        for (World world: galaxyService.getAllWorlds()) {
            final World foundWorld = galaxyService.findById(world.getId());
            assertNotNull(foundWorld);
        }
    }

    @Test
    public void shouldFindWorldsByName() {
        galaxyService.makeSomeWorlds();

        for (World world: galaxyService.getAllWorlds()) {
            final World foundWorld = galaxyService.findByName(world.getName());
            assertNotNull(foundWorld);
        }
    }

    @Test
    public void shouldReachMarsFromEarth() {
        galaxyService.makeSomeWorlds();

        final World earth = galaxyService.findByName("Earth");
        final World mars  = galaxyService.findByName("Mars");

        assertTrue(mars.canBeReachedFrom(earth));
        assertTrue(earth.canBeReachedFrom(mars));
    }

    @Test
    public void shouldFindWorldsWith1Moon() {
        galaxyService.makeSomeWorlds();

        for(World worldWithOneMoon: galaxyService.findAllByNumberOfMoons(1)) {
            assertThat(
                    worldWithOneMoon.getName(),
                    is(anyOf(containsString("Earth"), containsString("Midgard")))
            );
        }
    }

    @Test
    public void shouldNotFindKrypton() {
        galaxyService.makeSomeWorlds();

        World krypton = galaxyService.findByName("Krypton");
        assertNull(krypton);
    }
}
