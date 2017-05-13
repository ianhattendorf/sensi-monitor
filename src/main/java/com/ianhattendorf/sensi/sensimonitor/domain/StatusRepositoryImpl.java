package com.ianhattendorf.sensi.sensimonitor.domain;

import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Provider;
import javax.transaction.Transactional;

public class StatusRepositoryImpl implements StatusRepositoryCustom {

    private final Provider<StatusRepository> statusRepository;
    private final ThermostatRepository thermostatRepository;

    @Autowired
    public StatusRepositoryImpl(Provider<StatusRepository> statusRepository, ThermostatRepository thermostatRepository) {
        this.statusRepository = statusRepository;
        this.thermostatRepository = thermostatRepository;
    }

    @Override
    @Transactional
    public <S extends Status> S save(S  entity, String icd) {
        Thermostat thermostat = thermostatRepository.findFirstByIcd(icd);
        if (thermostat == null) {
            thermostat = thermostatRepository.save(new Thermostat(icd));
        }
        entity.setThermostat(thermostat);
        return statusRepository.get().save(entity);
    }
}
