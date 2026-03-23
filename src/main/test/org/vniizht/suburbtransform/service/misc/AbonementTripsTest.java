package org.vniizht.suburbtransform.service.misc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vniizht.suburbtransform.model.handbook.SeasonTrip;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbonementTripsTest {



    @Test
    void testTripsCase1() {
        assertThat(tripsFor(50,
                LocalDate.of(2024, 7, 26),
                LocalDate.of(2024, 10, 25),
                false))
                .isEqualTo(new HashMap<String, Integer>() {{
                    put("202407", 5);
                    put("202408", 25);
                    put("202409", 25);
                    put("202410", 20);
                }});
    }

    @Test
    void testTripsCase2() {
        assertThat(tripsFor(30,
                LocalDate.of(2024, 8, 20),
                LocalDate.of(2024, 9, 19),
                false))
                .isEqualTo(new HashMap<String, Integer>() {{
                    put("202408", 6);
                    put("202409", 9);
                }});
    }

    @Test
    void testTripsCase3() {
        assertThat(tripsFor(15,
                LocalDate.of(2024, 6, 27),
                LocalDate.of(2024, 10, 24),
                false))
                .isEqualTo(new HashMap<String, Integer>() {{
                    put("202406", 2);
                    put("202407", 7);
                    put("202408", 7);
                    put("202409", 7);
                    put("202410", 5);
                }});
    }

    @Test
    void testTripsCase4() {
        assertThat(tripsFor(50,
                LocalDate.of(2024, 10, 20),
                LocalDate.of(2025, 4, 19),
                false))
                .isEqualTo(new HashMap<String, Integer>() {{
                    put("202410", 10);
                    put("202411", 25);
                    put("202412", 25);
                    put("202501", 25);
                    put("202502", 25);
                    put("202503", 25);
                    put("202504", 15);
                }});
    }


    @Mock
    SeasonTrip seasonTrip;
    Map<String, Integer> tripsFor(
            int roundTripsCount,
            LocalDate begDate,
            LocalDate endDate,
            boolean isRefund
    ) {
        when(seasonTrip.getKol__round_trips()).thenReturn(roundTripsCount);
        return AbonementTrips.calculateTripsPerMonth(
                seasonTrip,
                Date.from(begDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                isRefund);
    }
}