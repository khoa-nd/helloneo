package com.chrisshayan.examples.helloneo.domain;


import org.neo4j.graphdb.Direction;
import org.springframework.data.neo4j.annotation.*;

import java.util.Set;

/**
 * Created by chris on 12/16/15.
 */

@NodeEntity
public class World {
    public static final String REACHABLE_BY_ROCKET = "REACHABLE_BY_ROCKET";

    @GraphId
    private Long id;

    @Indexed
    private String name;


    @Indexed
    private int moons;


    @Fetch
    @RelatedTo(type = REACHABLE_BY_ROCKET, direction = Direction.BOTH)
    private Set<World> reachableByRocket;

    public World() {
    }

    public World(String name, int moons) {
        this.name = name;
        this.moons = moons;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoons() {
        return moons;
    }

    public void setMoons(int moons) {
        this.moons = moons;
    }

    public Boolean canBeReachedFrom(World otherWorld) {
        return reachableByRocket.contains(otherWorld);
    }

    public void addRocketRouteTo(World otherWorld) {
        reachableByRocket.add(otherWorld);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        World other = (World) obj;
        if (id == null) return other.id == null;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }
}
