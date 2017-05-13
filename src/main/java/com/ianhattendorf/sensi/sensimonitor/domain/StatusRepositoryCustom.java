package com.ianhattendorf.sensi.sensimonitor.domain;

public interface StatusRepositoryCustom {
    <S extends Status> S save(S entity, String icd);
}
