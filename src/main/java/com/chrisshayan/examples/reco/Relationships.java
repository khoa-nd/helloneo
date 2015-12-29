package com.chrisshayan.examples.reco;

import org.neo4j.graphdb.RelationshipType;

/**
 * Created by chrisshayan on 12/24/15.
 */
public enum Relationships implements RelationshipType {
    FRIEND_OF,
    LIVES_IN,
    KNOWS
}
