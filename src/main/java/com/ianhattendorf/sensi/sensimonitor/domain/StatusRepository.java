package com.ianhattendorf.sensi.sensimonitor.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StatusRepository extends CrudRepository<Status, Integer>, StatusRepositoryCustom {

    @Query("select s from Status s join fetch s.thermostat left outer join fetch s.holdMode")
    Iterable<Status> findAllJoin();
}
