package com.chrisshayan.examples.helloneo.repositories;

import com.chrisshayan.examples.helloneo.domain.World;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Created by chris on 12/21/15.
 */
public interface WorldRepository extends GraphRepository<World> {
}
