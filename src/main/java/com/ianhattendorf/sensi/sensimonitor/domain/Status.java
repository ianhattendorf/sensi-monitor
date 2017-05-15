package com.ianhattendorf.sensi.sensimonitor.domain;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
public final class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(optional = false)
    private Thermostat thermostat;
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
    private Short temperature;
    private Short setPoint;
    private Short humidity;
    private Short batteryVoltage;
    @Column(length = 4)
    private String running;
    private Boolean lowPower;
    @Column(length = 8)
    private String operatingMode;
    private Short powerStatus;
    private Boolean scheduleMode;
    @ManyToOne
    private HoldMode holdMode;

    public Status() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Thermostat getThermostat() {
        return thermostat;
    }

    public void setThermostat(Thermostat thermostat) {
        this.thermostat = thermostat;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Short getTemperature() {
        return temperature;
    }

    public void setTemperature(Short temperature) {
        this.temperature = temperature;
    }

    public Short getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(Short setPoint) {
        this.setPoint = setPoint;
    }

    public Short getHumidity() {
        return humidity;
    }

    public void setHumidity(Short humidity) {
        this.humidity = humidity;
    }

    public Short getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Short batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
    }

    public String getRunning() {
        return running;
    }

    public void setRunning(String running) {
        this.running = running;
    }

    public Boolean getLowPower() {
        return lowPower;
    }

    public void setLowPower(Boolean lowPower) {
        this.lowPower = lowPower;
    }

    public String getOperatingMode() {
        return operatingMode;
    }

    public void setOperatingMode(String operatingMode) {
        this.operatingMode = operatingMode;
    }

    public Short getPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(Short powerStatus) {
        this.powerStatus = powerStatus;
    }

    public Boolean getScheduleMode() {
        return scheduleMode;
    }

    public void setScheduleMode(Boolean scheduleMode) {
        this.scheduleMode = scheduleMode;
    }

    public HoldMode getHoldMode() {
        return holdMode;
    }

    public void setHoldMode(HoldMode holdMode) {
        this.holdMode = holdMode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Status{");
        sb.append("id=").append(id);
        sb.append(", thermostat=").append(thermostat);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", temperature=").append(temperature);
        sb.append(", setPoint=").append(setPoint);
        sb.append(", humidity=").append(humidity);
        sb.append(", batteryVoltage=").append(batteryVoltage);
        sb.append(", running='").append(running).append('\'');
        sb.append(", lowPower=").append(lowPower);
        sb.append(", operatingMode='").append(operatingMode).append('\'');
        sb.append(", powerStatus=").append(powerStatus);
        sb.append(", scheduleMode=").append(scheduleMode);
        sb.append(", holdMode=").append(holdMode);
        sb.append('}');
        return sb.toString();
    }
}
