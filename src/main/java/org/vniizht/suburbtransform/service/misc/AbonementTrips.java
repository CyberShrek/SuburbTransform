package org.vniizht.suburbtransform.service.misc;

import org.vniizht.suburbtransform.model.handbook.SeasonTrip;
import org.vniizht.suburbtransform.model.level2.PrigMain;
import org.vniizht.suburbtransform.service.dao.HandbookDao;
import org.vniizht.suburbtransform.util.Util;

import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AbonementTrips { private AbonementTrips() {}

    public static Map<String, Integer> calculateTripsPerMonth(PrigMain main, boolean isRefund) {
        Date startDate = isRefund ? main.operation_date : main.ticket_begdate;

        int ticketCode = abonementType2ticketCode(main.abonement_subtype, main.abonement_type);
        return calculateTripsPerMonth(
                HandbookDao.cache.findTrip(
                        ticketCode,
                        main.seatstick_limit,
                        startDate),
                new Date(Math.min(main.operation_date.getTime(), startDate.getTime())),
                main.ticket_enddate,
                isRefund
        );
    }

    public static Map<String, Integer> calculateTripsPerMonth(SeasonTrip seasonTrip,
                                                              Date begDate,
                                                              Date endDate,
                                                              boolean isRefund) {

        Map<String, Integer> totalTripsPerMonth = new LinkedHashMap<>();

        String begYyyymm = yyyymm(begDate);
        String endYyyymm = yyyymm(endDate);

        int begDays = begDate.getDate();
        int endDays = endDate.getDate();

        int tripsPerMonth = (Optional.ofNullable(seasonTrip.getKol__round_trips()).orElse(0) / 2) * (isRefund ? -1 : 1);

        if (begYyyymm.equals(endYyyymm)) {
            totalTripsPerMonth.put(begYyyymm, tripsPerMonth);
        } else {
            int endTripsCount = calculateTripsCount(endDays, tripsPerMonth);
            totalTripsPerMonth.put(begYyyymm, tripsPerMonth - endTripsCount);

            Date iterDate = begDate;
            while (iterDate.before(endDate)) {
                iterDate = Date.from(iterDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .plusMonths(1)
                        .toInstant());
                String iterYyyymm = yyyymm(iterDate);
                if (iterYyyymm.equals(endYyyymm)) break;
                totalTripsPerMonth.put(iterYyyymm, tripsPerMonth);
            }

            totalTripsPerMonth.put(endYyyymm, endTripsCount);
        }

        return totalTripsPerMonth;
    }

    private static int calculateTripsCount(int activeDays, int tripsPerMonth) {
        return (int) (tripsPerMonth * ((float) activeDays / 31));
    }

    private static Integer abonementType2ticketCode(String abonementSubtype, String abonementType) {
        switch (abonementType.charAt(0)) {
            case '1': return 9; // билет на количество поездок
            case '2': switch (abonementSubtype.charAt(0)) {
                case '1': return 6;
                case '2': return 5;
                default : return 7; // билет на определенные даты
            }
            case '3': return 1; // билет «ежедневно» (помесячный)
            case '4': return 2; // билет «ежедневно» (посуточный)
            case '5':
            case '6': return 8; // билет «выходного дня»
            case '7': return 3; // билет «рабочего дня» (помесячный)
            case '8': return 4; // билет «рабочего дня» (посуточный)
            default : return 0;
        }
    }

    private static String yyyymm(Date date) {
        return Util.formatDate(date, "yyyyMM");
    }
}