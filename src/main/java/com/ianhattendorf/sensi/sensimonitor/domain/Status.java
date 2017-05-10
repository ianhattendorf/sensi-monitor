package com.ianhattendorf.sensi.sensimonitor.domain;

import com.ianhattendorf.sensiapi.response.data.OperationalStatus;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
public final class Status {
    @Id
    @GeneratedValue
    private Long id;
    private ZonedDateTime timestamp;
    private Integer temperature;
    private Integer humidity;
    private Integer batteryVoltage;
    private String running;
    private Boolean lowPower;
    private String operatingMode;
    private Integer powerStatus;

    protected Status() {}

    public Status(OperationalStatus operationalStatus) {
        timestamp = ZonedDateTime.now();
        temperature = operationalStatus.getTemperature() == null ? null : operationalStatus.getTemperature().getF();
        humidity = operationalStatus.getHumidity();
        batteryVoltage = operationalStatus.getBatteryVoltage();
        running = operationalStatus.getRunning() == null ? null : operationalStatus.getRunning().getMode();
        lowPower = operationalStatus.getLowPower();
        operatingMode = operationalStatus.getOperatingMode();
        powerStatus = operationalStatus.getPowerStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Integer getBatteryVoltage() {
        return batteryVoltage;
    }

    public void setBatteryVoltage(Integer batteryVoltage) {
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

    public Integer getPowerStatus() {
        return powerStatus;
    }

    public void setPowerStatus(Integer powerStatus) {
        this.powerStatus = powerStatus;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Status{");
        sb.append("id=").append(id);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", temperature=").append(temperature);
        sb.append(", humidity=").append(humidity);
        sb.append(", batteryVoltage=").append(batteryVoltage);
        sb.append(", running='").append(running).append('\'');
        sb.append(", lowPower=").append(lowPower);
        sb.append(", operatingMode='").append(operatingMode).append('\'');
        sb.append(", powerStatus=").append(powerStatus);
        sb.append('}');
        return sb.toString();
    }
}
