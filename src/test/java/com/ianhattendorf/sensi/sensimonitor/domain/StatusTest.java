package com.ianhattendorf.sensi.sensimonitor.domain;

import com.ianhattendorf.sensi.sensiapi.response.data.OperationalStatus;
import com.ianhattendorf.sensi.sensiapi.response.data.ScheduleTemps;
import com.ianhattendorf.sensi.sensiapi.response.data.Temperature;
import com.ianhattendorf.sensi.sensiapi.response.data.Update;
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
    }

    @Test
    public void testSetPoint() {
        update.getOperationalStatus().setScheduleTemps(
                new ScheduleTemps(null, null,
                        new Temperature(62, 17), new Temperature(79, 26)));

        Status status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());

        update.getOperationalStatus().setOperatingMode("Cool");
        status = Mapper.updateToStatus(update);
        assertEquals(79, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("Heat");
        status = Mapper.updateToStatus(update);
        assertEquals(62, status.getSetPoint().shortValue());

        update.getOperationalStatus().setOperatingMode("AutoCool");
        status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());

        update.getOperationalStatus().setOperatingMode("asdf");
        status = Mapper.updateToStatus(update);
        assertNull(status.getSetPoint());
    }
}
