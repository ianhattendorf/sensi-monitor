package com.ianhattendorf.sensi.sensimonitor.domain;

import org.springframework.data.repository.CrudRepository;

public interface HoldModeRepository extends CrudRepository<HoldMode, Integer> {
    <S extends HoldMode> S findFirstByMode(String mode);
}
