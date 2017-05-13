package com.ianhattendorf.sensi.sensimonitor.domain;

import org.springframework.data.repository.CrudRepository;

public interface ThermostatRepository extends CrudRepository<Thermostat, Integer> {
    <S extends Thermostat> S findFirstByIcd(String icd);
}
