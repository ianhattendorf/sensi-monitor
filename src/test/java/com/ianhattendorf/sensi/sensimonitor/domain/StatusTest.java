package com.ianhattendorf.sensi.sensimonitor.domain;

import com.ianhattendorf.sensi.sensiapi.response.data.*;
import com.ianhattendorf.sensi.sensimonitor.util.Mapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public final class StatusTest {

    private Update update;

    @Before
    public void setUp() {
        update = new Update();
        update.setOperationalStatus(new OperationalStatus());
        update.getOperationalStatus().setScheduleTemps(
                new ScheduleTemps(null, null,
                        new Temperature(62, 17), new Temperature(79, 26)));
        update.setEnvironmentControls(new EnvironmentControls());
        update.getEnvironmentControls().setCoolSetpoint(new Temperature(75, 23));
        update.getEnvironmentControls().setHeatSetpoint(new Temperature(70, 21));
        update.getEnvironmentControls().setScheduleMode("On");
    }

    @Test
    public void testSetPoint() {
        Status status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());

        update.getOperationalStatus().setOperatingMode("Cool");
        status = Mapper.updateToStatus(update);
        assertEquals(75, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("Heat");
        status = Mapper.updateToStatus(update);
        assertEquals(70, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("AutoCool");
        status = Mapper.updateToStatus(update);
        assertEquals(75, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("AutoHeat");
        status = Mapper.updateToStatus(update);
        assertEquals(70, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("asdf");
        status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());

        update.getEnvironmentControls().setScheduleMode("Off");
        update.getOperationalStatus().setOperatingMode("Cool");
        status = Mapper.updateToStatus(update);
        assertEquals(75, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("Heat");
        status = Mapper.updateToStatus(update);
        assertEquals(70, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("AutoCool");
        status = Mapper.updateToStatus(update);
        assertEquals(75, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("AutoHeat");
        status = Mapper.updateToStatus(update);
        assertEquals(70, status.getSetPoint().shortValue());

        EnvironmentControls environmentControls = update.getEnvironmentControls();
        update.setEnvironmentControls(null);
        status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());

        update.setOperationalStatus(null);
        status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());

        update.setEnvironmentControls(environmentControls);
        status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());
    }
}
