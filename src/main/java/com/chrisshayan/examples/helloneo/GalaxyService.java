package com.chrisshayan.examples.helloneo;

import com.chrisshayan.examples.helloneo.domain.World;
import com.chrisshayan.examples.helloneo.repositories.WorldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by chris on 12/21/15.
 */

@Service
@Transactional
public class GalaxyService {

    @Autowired
    private WorldRepository worldRepository;

    /**
     * @return number of worlds
     */
    public Long getWorldCount() {
        return worldRepository.count();
    }

    /**
     * @param name  name of the world
     * @param moons number of moons
     * @return the created {@linkplain com.chrisshayan.examples.helloneo.domain.World}
     */
    public World createWorld(final String name, final int moons) {
        return worldRepository.save(new World(name, moons));
    }


    /**
     * @return list of {@linkplain com.chrisshayan.examples.helloneo.domain.World}
     */
    public Iterable<World> getAllWorlds() {
        return worldRepository.findAll();
    }

    /**
     * @param id the unique
     * @return {@linkplain com.chrisshayan.examples.helloneo.domain.World}
     */
    public World findById(final Long id) {
        return worldRepository.findOne(id);
    }

    /**
     * @param name name of the world
     * @return an instance of {@linkplain com.chrisshayan.examples.helloneo.domain.World}
     */
    public World findByName(final String name) {
        return worldRepository.findBySchemaPropertyValue("name", name);
    }

    /**
     * @param moons number of moons
     * @return list of worlds with @param moons
     */
    public Iterable<World> findAllByNumberOfMoons(final int moons) {
        return worldRepository.findAllBySchemaPropertyValue("moons", moons);
    }

    public Collection<World> makeSomeWorlds() {
        Collection<World> worlds = new ArrayList<World>();

        // Solar worlds
        worlds.add(createWorld("Mercury", 0));
        worlds.add(createWorld("Venus", 0));

        World earth = createWorld("Earth", 1);
        World mars = createWorld("Mars", 2);
        mars.addRocketRouteTo(earth);
        worldRepository.save(mars);
        worlds.add(earth);
        worlds.add(mars);

        worlds.add(createWorld("Jupiter", 63));
        worlds.add(createWorld("Saturn", 62));
        worlds.add(createWorld("Uranus", 27));
        worlds.add(createWorld("Neptune", 13));

        // Norse worlds
        worlds.add(createWorld("Alfheimr", 0));
        worlds.add(createWorld("Midgard", 1));
        worlds.add(createWorld("Muspellheim", 2));
        worlds.add(createWorld("Asgard", 63));
        worlds.add(createWorld("Hel", 62));

        return worlds;
    }
}
