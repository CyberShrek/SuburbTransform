package org.vniizht.suburbtransform.service.dao;

import org.vniizht.suburbtransform.model.handbook.SeasonTrip;
import org.vniizht.suburbtransform.model.level2.PrigMain;
import org.vniizht.suburbtransform.util.Util;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class TripsDao { private TripsDao() {}

    public static Map<String, Integer> calculateTripsPerMonth(PrigMain main, boolean isRefund) {
        Date startDate = isRefund ? main.operation_date : main.ticket_begdate;

        return calculateTripsPerMonth(
                HandbookDao.cache.findTrip(
                        abonementType2ticketCode(main.abonement_subtype, main.abonement_type),
                        main.seatstick_limit,
                        startDate),
                main.operation_date,
                startDate,
                main.ticket_enddate,
                isRefund
        );
    }

    public static Map<String, Integer> calculateTripsPerMonth(SeasonTrip seasonTrip,
                                                       Date saleDate,
                                                       Date begDate,
                                                       Date endDate,
                                                       boolean isRefund) {

        Map<String, Integer> totalTripsPerMonth = new LinkedHashMap<>();
        calculateDaysWithTripsPerMonth(saleDate, begDate, endDate)
                .forEach((yyyymm, daysWithTrips) -> {
                    int trips = seasonTrip == null ? 0 :
                            Math.round(((float) seasonTrip.kol__round_trips / 2)
                                    * ((float) daysWithTrips / 31));

                    if(trips == 0 && Util.formatDate(begDate, "yyyyMM").equals(yyyymm))
                        trips = 1;

                    totalTripsPerMonth.put(yyyymm, trips * (isRefund ? -1 : 1));
                });

        return totalTripsPerMonth;
    }

    private static Map<String, Byte> calculateDaysWithTripsPerMonth(Date saleDate,
                                                             Date begDate,
                                                             Date endDate) {
        Map<String, Byte> daysWithTripsPerMonth = new LinkedHashMap<>();

        for (Date iterDate = new Date(Math.min(saleDate.getTime(), begDate.getTime()));
             iterDate.before(endDate) || iterDate.equals(endDate);
             iterDate = new Date(iterDate.getTime() + 86400000)) {

            byte daysWithTrips = daysWithTripsPerMonth.computeIfAbsent(yyyymm(iterDate), k -> (byte) 0);
            if (iterDate.getTime() >= begDate.getTime())
                daysWithTrips++;

            daysWithTripsPerMonth.put(yyyymm(iterDate), daysWithTrips);
        }
        return daysWithTripsPerMonth;
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
