package com.ianhattendorf.sensi.sensimonitor.util;

import com.ianhattendorf.sensi.sensiapi.response.data.EnvironmentControls;
import com.ianhattendorf.sensi.sensiapi.response.data.OperationalStatus;
import com.ianhattendorf.sensi.sensiapi.response.data.Temperature;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
import com.ianhattendorf.sensi.sensimonitor.domain.HoldMode;
import com.ianhattendorf.sensi.sensimonitor.domain.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;

public final class Mapper {
    private static final Logger log = LoggerFactory.getLogger(Mapper.class);

    public static Status updateToStatus(Update update) {
        Status status = new Status();
        OperationalStatus operationalStatus = update.getOperationalStatus();
        EnvironmentControls environmentControls = update.getEnvironmentControls();

        // determine current set point temperature
        // first check if schedule mode is off/on
        // if off, set setpoint to current setpoint based on operatingMode
        // if on, set setpoint to schedule based on operatingMode
        if (environmentControls != null && operationalStatus != null
                && operationalStatus.getOperatingMode() != null
                && environmentControls.getScheduleMode() != null) {
            Temperature temperature = null;
            switch (environmentControls.getScheduleMode()) {
                case "Off":
                    switch (operationalStatus.getOperatingMode()) {
                        case "Cool":
                            temperature = environmentControls.getCoolSetpoint();
                            break;
                        case "Heat":
                            temperature = environmentControls.getHeatSetpoint();
                            break;
                        default:
                            log.warn("Unknown operationalStatus.operatingMode while schedule is off: {}",
                                    operationalStatus.getOperatingMode());
                    }
                    break;
                case "On":
                    if (operationalStatus.getScheduleTemps() == null) {
                        log.warn("schedule on but operationalStatus.scheduleTemps is null");
                        break;
                    }
                    switch (operationalStatus.getOperatingMode()) {
                        case "Cool":
                            temperature = operationalStatus.getScheduleTemps().getCool();
                            break;
                        case "Heat":
                            temperature = operationalStatus.getScheduleTemps().getHeat();
                            break;
                        case "AutoCool":
                            temperature = operationalStatus.getScheduleTemps().getAutoCool();
                            break;
                        case "AutoHeat":
                            temperature = operationalStatus.getScheduleTemps().getAutoHeat();
                            break;
                        default:
                            log.warn("Unknown operationalStatus.operatingMode while schedule is on: {}",
                                    operationalStatus.getOperatingMode());
                    }
                    break;
                default:
                    log.warn("Unknown environmentControls.scheduleMode: {}", environmentControls.getScheduleMode());
            }
            if (temperature != null && temperature.getF() != null) {
                status.setSetPoint(temperature.getF().shortValue());
            }
        }

        if (operationalStatus != null) {
            status.setUpdatedAt(ZonedDateTime.now());

            if (operationalStatus.getTemperature() != null
                    &&  operationalStatus.getTemperature().getF() != null) {
                status.setTemperature(operationalStatus.getTemperature().getF().shortValue());
            }

            if (operationalStatus.getHumidity() != null) {
                status.setHumidity(operationalStatus.getHumidity().shortValue());
            }

            if(operationalStatus.getBatteryVoltage() != null) {
                status.setBatteryVoltage(operationalStatus.getBatteryVoltage().shortValue());
            }

            if (operationalStatus.getRunning() != null) {
                status.setRunning(operationalStatus.getRunning().getMode());
            }

            status.setLowPower(operationalStatus.getLowPower());

            status.setOperatingMode(operationalStatus.getOperatingMode());

            if (operationalStatus.getPowerStatus() != null) {
                status.setPowerStatus(operationalStatus.getPowerStatus().shortValue());
            }
        }

        if (environmentControls != null) {
            if ("Off".equals(environmentControls.getScheduleMode())) {
                status.setScheduleMode(false);
            } else if ("On".equals(environmentControls.getScheduleMode())) {
                status.setScheduleMode(true);
            }

            if (environmentControls.getHoldMode() != null) {
                status.setHoldMode(new HoldMode(environmentControls.getHoldMode()));
            }
        }

        return status;
    }
}
