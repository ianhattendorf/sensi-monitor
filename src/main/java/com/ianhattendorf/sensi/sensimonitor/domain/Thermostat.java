package com.ianhattendorf.sensi.sensimonitor.domain;

import javax.persistence.*;

@Entity
public final class Thermostat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // columnDefinition = "bpchar(23)" required for ddl-auto=validate with postgresql
    // hibernate doesn't seem to detect char column correctly
    @Column(unique = true, nullable = false, length = 23, columnDefinition = "bpchar(23)")
    private String icd;

    protected Thermostat() {}

    public Thermostat(String icd) {
        this.id = id;
        this.icd = icd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIcd() {
        return icd;
    }

    public void setIcd(String icd) {
        this.icd = icd;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Thermostat{");
        sb.append("id=").append(id);
        sb.append(", icd='").append(icd).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
