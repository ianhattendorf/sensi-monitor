package com.ianhattendorf.sensi.sensimonitor.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public final class HoldMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String mode;

    public HoldMode() {}

    public HoldMode(String mode) {
        this.mode = mode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HoldMode{");
        sb.append("id=").append(id);
        sb.append(", mode='").append(mode).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
