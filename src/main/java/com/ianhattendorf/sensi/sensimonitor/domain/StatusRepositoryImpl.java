package com.ianhattendorf.sensi.sensimonitor.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Provider;

public class StatusRepositoryImpl implements StatusRepositoryCustom {

    private final Provider<StatusRepository> statusRepository;
    private final ThermostatRepository thermostatRepository;
    private final HoldModeRepository holdModeRepository;

    @Autowired
    public StatusRepositoryImpl(Provider<StatusRepository> statusRepository,
                                ThermostatRepository thermostatRepository,
                                HoldModeRepository holdModeRepository) {
        this.statusRepository = statusRepository;
        this.thermostatRepository = thermostatRepository;
        this.holdModeRepository = holdModeRepository;
    }

    @Override
    @Transactional
    public <S extends Status> S save(S  entity, String icd) {
        Thermostat thermostat = thermostatRepository.findFirstByIcd(icd);
        if (thermostat == null) {
            thermostat = thermostatRepository.save(new Thermostat(icd));
        }
        entity.setThermostat(thermostat);

        if (entity.getHoldMode() != null && entity.getHoldMode().getId() == null) {
            HoldMode holdMode = holdModeRepository.findFirstByMode(entity.getHoldMode().getMode());
            if (holdMode == null) {
                holdMode = holdModeRepository.save(entity.getHoldMode());
            }
            entity.setHoldMode(holdMode);
        }

        return statusRepository.get().save(entity);
    }
}
