package org.vniizht.suburbtransform.service;

import lombok.SneakyThrows;
import org.vniizht.suburbtransform.model.level2.PrigAdi;
import org.vniizht.suburbtransform.model.level2.PrigCost;
import org.vniizht.suburbtransform.model.level2.PrigMain;
import org.vniizht.suburbtransform.model.level3.CO22Meta;
import org.vniizht.suburbtransform.model.level3.Lgot;
import org.vniizht.suburbtransform.model.level3.T1;
import org.vniizht.suburbtransform.model.routes.RouteGroup;
import org.vniizht.suburbtransform.service.dao.HandbookDao;
import org.vniizht.suburbtransform.service.dao.Level2Dao;
import org.vniizht.suburbtransform.service.dao.RoutesDao;
import org.vniizht.suburbtransform.service.dao.TripsDao;
import org.vniizht.suburbtransform.util.Util;

import java.util.*;
import java.util.function.Function;

public final class Level3Prig extends Level3 <Level2Dao.PrigCursor> {

    // Переменные для каждой записи
    private Integer         yyyyMM;
    private String          fullBenefit;
    private PrigMain        main;
    private List<PrigCost>  costList;
    private PrigAdi         adi;
    private boolean         isAbonement;
    private boolean         isBasic;
    private boolean         isRefund;
    private boolean         isRefuse;
    private boolean         isCancel;

    public Level3Prig(Long initialT1Serial) {
        super(initialT1Serial);
    }

    @Override
    protected void next(Level2Dao.PrigCursor cursor) {
        main     = cursor.getMain();
        costList = cursor.getCost();
        adi      = cursor.getAdi();
        isAbonement = getT1P21().equals("5");
        fullBenefit = main.benefitgroup_code + main.benefit_code;
        yyyyMM = Integer.parseInt(Util.formatDate(main.operation_date, "yyyyMM"));
        if (main.no_use == null) main.no_use = "0";
        isBasic  = main.oper.charAt(0) == 'O' && main.oper_g.charAt(0) == 'N';
        isRefund = main.oper.charAt(0) == 'V' && main.oper_g.charAt(0) == 'N';
        isRefuse = main.oper.charAt(0) == 'O' && main.oper_g.charAt(0) == 'O';
        isCancel = main.oper.charAt(0) == 'O' && main.oper_g.charAt(0) == 'G';
    }

    @Override
    protected boolean t1Exists() {
        return !Objects.equals(main.no_use, "1");
    }

    @Override
    protected boolean lgotExists() {
        return t1Exists()
                && !main.benefit_code.equals("00")
                && !main.benefitgroup_code.equals("21");
    }

    @Override
    protected T1 getT1() {
        String saleRoad = HandbookDao.getRoad3(main.sale_station, main.operation_date);
        return T1.builder()
                .request_date  (main.request_date)
                .operation_date(main.operation_date)
                .ticket_begdate(main.ticket_begdate)
                .yyyymm(yyyyMM)
                .p1("tab1")
                .p3(Util.formatDate(main.operation_date, "yyyy"))
                .p4(Util.formatDate(main.operation_date, "MM"))
                .p5("017")
                .p6(saleRoad)
                .p7(saleRoad)
                .p8(main.sale_station)
                .p9(String.format("%09d", main.carriage_code))
                .p10(main.region_code)
                .p11(HandbookDao.getOkatoByStation(main.sale_station, main.operation_date))
                .p12(getT1P12())
                .p15(main.departure_station)
                .p17(HandbookDao.getOkatoByStation(main.departure_station, main.operation_date))
                .p18(HandbookDao.getArea(main.departure_station, main.operation_date))
                .p19(getT1P19())
                .p20("0" + main.carriage_class)
                .p21(getT1P21())
                .p22(getT1P22())
                .p23("3")
                .p24(Optional.ofNullable(fullBenefit).orElse("0000"))
                .p25(getT1P25())
                .p26(HandbookDao.getGvc(main.benefitgroup_code + main.benefit_code, main.operation_date))
                .p30(HandbookDao.getOkatoByStation(main.arrival_station, main.operation_date))
                .p31(HandbookDao.getArea(main.arrival_station, main.operation_date))
                .p32(costList.stream().mapToInt(costItem -> costItem.route_distance).sum())

                .p33(getT1P33())
                .p34(0.0)
                .p35(0.0)
                .p36(getT1P36())
                .p37(0.0)
                .p38(0.0)
                .p39(getT1P39())
                .p40(0.0)
                .p41(0.0)
                .p42(0.0)
                .p43(0.0)
                .p44(getT1P44())
                .p45(0.0)
                .p46(0.0)
                .p47(0.0)
                .p48(0.0)
                .p49(0.0)
                .p50(0.0)
                .p51(getT1P51())

                .p52(getT1P52())
                .p53(String.valueOf(main.agent_code))
                .p54(main.arrival_station)
                .p55(getT1P55())
                .p56(getT1P56())
                .p57(getT1P57())
                .p58(getT1P58())
                .p59(getT1P59())
                .p60("000")
                .p61(main.train_num.matches("^\\d {4}") ? (main.train_num.trim().charAt(0) + "") : "0")

                .build();
    }

    @Override
    protected Lgot getLgot() {
        int single_qty = 0;
        int abonem_qty = 0;
        int pass_qty = main.pass_qty;
        if(main.abonement_type.equals("0  ")) single_qty = pass_qty;
        else                                  abonem_qty = pass_qty;

        return Lgot.builder()
                .request_date(main.request_date)
                .yyyymm(yyyyMM)
                .list("R064" + (main.benefitgroup_code.equals("22") ? 'Z' : 'G'))
                .p2(getLgotP4() + HandbookDao.getRoad2(main.sale_station, main.operation_date))
                .p3(HandbookDao.getDepartment(main.sale_station, main.operation_date))
                .p4(getLgotP4())
                .p5(getLgotP5())
                .p6(!Objects.equals(main.train_category, "O") ? main.train_category : "0")
                .p7(fullBenefit)
                .p8(Util.addLeadingZeros(String.valueOf(main.carriage_code), 4))
                .p9(HandbookDao.getOkatoByRegion(main.benefit_region, main.operation_date))
                .p10(adi == null ? null : adi.benefit_doc)
                .p11(Util.addLeadingZeros(main.benefitgroup_code.equals("22") && adi != null
                        ? adi.bilgroup_secur + adi.bilgroup_code
                        : main.benefit_region, 5))
                .p12(Util.addLeadingZeros(adi == null ? null : adi.employee_unit, 10))
                .p13(main.benefitgroup_code.equals("22") && adi != null ? adi.employee_cat : "0")
                .p14(getLgotP14())
                .p15(getLgotP15())
                .p16(getLgotP16())
                .p17(getLgotP17())
                .p18(getLgotP18())

                .p19(getLgotP20().equals("9") ? main.seatstick_limit : 0)

                .p20(getLgotP20())
                .p21(!getLgotP20().equals("9") && abonem_qty != 0 ? main.seatstick_limit : 0)
                .p22(main.operation_date)
                .p23(main.ticket_begdate)
                .p24(main.ticket_ser + Util.addLeadingZeros(String.valueOf(main.ticket_num), 6))
                .p25(main.departure_station)
                .p26(main.arrival_station)

                .p27(getLgotP27())
                .p28(getLgotP28())

                .p30(main.server_datetime)
                .p31(Util.addLeadingZeros(main.server_reqnum == null ? null : main.server_reqnum.toString(), 7))
                .p32(adi == null ? null : adi.snils)

                .p33(main.carryon_weight)

                .p34(main.sale_station)
                .p35(main.agent_code == null ? null : String.valueOf(main.agent_code))

                .build();
    }

    @Override
    protected RouteGroup getRouteGroup() {
        RouteGroup routeGroup = new RouteGroup();
        Map<Integer, List<PrigCost>> costsPerRouteNum = new LinkedHashMap<>();
        costList.forEach(cost -> {
            if (!costsPerRouteNum.containsKey(cost.route_num)) {
                costsPerRouteNum.put(cost.route_num, new ArrayList<>());
            }
            costsPerRouteNum.get(cost.route_num).add(cost);
        });
//        costsPerRouteNum.forEach((routeNum, costs) -> {
//            PrigCost firstCost = costs.get(0);
//            PrigCost lastCost  = costs.get(costs.size() - 1);
//            routeGroup.merge(RoutesDao.getRouteGroup(
//                    routeNum,
//                    firstCost.departure_station,
//                    lastCost.arrival_station,
//                    firstCost.request_date
//            ));
//        });
        return routeGroup;
    }

    @Override
    protected Set<T1> multiplyT1(T1 t1) {
        Set<T1> t1Set = new LinkedHashSet<>();

        if (isAbonement) {
            TripsDao.calculateTripsPerMonth(main, isRefund)
                    .forEach((yyyymm, trips) -> {
                        boolean isActual = t1.yyyymm == Integer.parseInt(yyyymm);
                        t1Set.add(t1.toBuilder()
                                .yyyymm(Integer.parseInt(yyyymm))
                                .p12(yyyymm.substring(2))

                                .p33(Long.valueOf(trips) * main.pass_qty)
                                // Стоимости
                                .p34(isActual ? t1.p34 : 0)
                                .p35(isActual ? t1.p35 : 0)
                                .p36(isActual ? t1.p36 : 0)
                                .p37(isActual ? t1.p37 : 0)
                                .p38(isActual ? t1.p38 : 0)
                                .p39(isActual ? t1.p39 : 0)
                                .p40(isActual ? t1.p40 : 0)
                                .p41(isActual ? t1.p41 : 0)
                                .p42(isActual ? t1.p42 : 0)
                                .p43(isActual ? t1.p43 : 0)
                                .p44(isActual ? t1.p44 : 0)
                                .p45(isActual ? t1.p45 : 0)
                                .p46(isActual ? t1.p46 : 0)
                                .p47(isActual ? t1.p47 : 0)
                                .p48(isActual ? t1.p48 : 0)
                                .p49(isActual ? t1.p49 : 0)
                                .p50(isActual ? t1.p50 : 0)
                                .build());
                    });
        } else {
            t1Set.add(t1);
        }

        return t1Set;
    }

    @Override
    protected CO22Meta getMeta() {
        return CO22Meta.builder()
                .request_date(main.request_date)
                .l2_prig_idnum(main.idnum)
                .operation_date(main.operation_date)
                .ticket_begdate(main.ticket_begdate)
                .ticket_enddate(main.ticket_enddate)
                .build();
    }

    @Override
    protected double getRegionIncomePerKm(String region) {
        return calculateRegionSumPerKm(
                region,
                cost -> cost.tariff_sum.longValue(), main.tariff_sum.doubleValue()
        );
    }

    @Override
    protected double getRegionOutcomePerKm(String region) {
        return calculateRegionSumPerKm(
                region,
                cost -> cost.department_sum.longValue(), main.department_sum.doubleValue()
        );
    }

    private double calculateRegionSumPerKm(
            String region,
            Function<PrigCost, Long> costSumExtractor,
            double mainSum
    ) {
        int distance = 0;
        double costSum = 0;
        for (PrigCost cost : costList) {
            if (cost.region_code.equals(region)) {
                distance += cost.route_distance;
                costSum += costSumExtractor.apply(cost);
            }
        }

        return distance > 0
                ? (costSum > 0 ? costSum : mainSum) / distance
                : 0;
    }

    private String getT1P12() {
        switch (getT1P21()) {
            // Для разового билета и абонементов - yymm даты начала действия
            case "2": case "3": case "5":
                return Util.formatDate(main.ticket_begdate, "yyMM");
        }
        return main.yyyymm.toString().substring(2); // yymm
    }

    private String getT1P19() {
        switch (main.train_category) {
            case "Л": return "Л";           // просто скорые пригородные
            case "С": return "6";           // скорые пригородные поезда типа «Спутник» (7ХХХ)
            case "7": return "5";           // скорые пригородные поезда без предоставления мест (7ХХХ)
            case "А": return "8";           // рельсовые автобусы 6000-е
            case "Б": return "7";           // рельсовые автобусы 7000-е
            case "Г": return "9";           // городские линии
            case "1":   case "М": return "4"; // скорые пригородные с предоставлением мест (7XXX(8xx-c АМГ))
            default: return "1";            // пригородные пассажирские
        }
    }

    @SneakyThrows
    private String getT1P21() {
        if (main.flg_passtype[0])    return "8";    // Квитанция за оформление в поезде
        if (main.flg_stickettype[1]) return "6";    // Перевозочный документ (для багажа)
        switch (main.abonement_type.charAt(0)) {
            case '5': case '6': return "4";         // Билет выходного дня
            case '0': return main.flg_stickettype[2]
                    ? "3"                           // В обоих направлениях
                    : "2";                          // В одном направлении
        }
        return "5";
    }

    private String getT1P22() {
        if (main.flg_passtype[0])           return "2"; // Детский
        if (main.benefit_code.equals("00")) return "1"; // Полный
        return "3";
    }

    private String getT1P25() {
        if(getTSite()
                .equals("09") && HandbookDao.getPlagnVr(main.payagent_id, main.departure_station, main.operation_date)
                .equals("6 "))
            return "4"; // Электронный кошелёк

        switch (main.paymenttype) {
            case "8": return "3";             // Банковские карты
            case "9": return "1";             // Льготные
            case "1": case "3": return "2";   // Наличные
            case "6": return "5";             // Безнал для юр. лиц
            default: return "6";              // Интернет
        }
    }

    private Long getT1P33() {
        return (long)
                (isRefund || isRefuse || isCancel ? -1 : 1) * (getT1P21().equals("6")
                ? main.carryon_weight
                : main.pass_qty);
    }

    private Double getT1P36() {
        return isRefund
                ? -main.refund_sum.doubleValue()
                : isRefuse || isCancel
                ? -main.tariff_sum.doubleValue()
                : main.tariff_sum.doubleValue();
    }

    private Double getT1P39() {
        return isRefund
                ? -main.refundfee_sum.doubleValue()
                : isRefuse || isCancel
                ? -main.fee_sum.doubleValue()
                : main.fee_sum.doubleValue();
    }

    private Double getT1P44() {
        return isRefund
                ? -main.refunddepart_sum.doubleValue()
                : isRefuse || isCancel
                ? -main.department_sum.doubleValue()
                : main.department_sum.doubleValue();
    }

    private Long getT1P51() {
        return (long) (isRefund || isRefuse || isCancel
                ? -main.pass_qty
                : main.pass_qty);
    }

    private String getT1P52() {
        String tSite = getTSite();
        if(tSite.equals("  ")) {
            if(main.request_type == 64)
                return "1";
            switch (main.request_subtype / 256) {
                case 0: return "3";
                case 1: return "2";
                default: return "5";
            }
        }

        return tSite.trim().substring(1, 1);
    }

    private String getT1P55() {
        switch (main.abonement_type.charAt(0)){
            case '1': return "5";           // билет на количество поездок
            case '2': switch (main.abonement_subtype.charAt(0)){
                case '0': return "4";       // билет на определенные даты
                case '1': return "6";       // билет на определенные нечетные даты
                case '2': return "7";       // билет на определенные четные даты
            }
            case '3': case '4': return "1"; // билет «ежедневно»
            case '5': case '6': return "2"; // билет «выходного дня»
            case '7': case '8': return "3"; // билет «рабочего дня»
        }
        return "0";                         //
    }

    private String getT1P56() {
        if(main.abonement_type.charAt(0) == '0'
                || main.seatstick_limit == 0
                || main.seatstick_limit == 10
                || main.seatstick_limit == 11)
            return "000";

        char type;
        switch (main.abonement_type.charAt(0)) {
            case '3': case '5': case '7': type = '0'; break; // Месячный
            case '2': case '4': case '6': type = '1'; break; // Посуточный
            default: type = '4';                      // Количество поездок
        }
        return type + String.format("%02d", main.seatstick_limit);
    }

    private String getT1P57() {
        String type = main.carryon_type;
        switch (type.charAt(0)) {
            case 'Ж': type = "1"; break; // живность
            case 'Т': type = "2"; break; // телевизор
            case 'В': type = "3"; break; // велосипед
            case 'Р': type = "4"; break; // излишний вес ручной клади
        }
        return type;
    }

    private String getT1P58() {
        return main.benefitgroup_code.equals("22")
                && adi != null
                ? (Integer.parseInt(adi.bilgroup_code) > 4 ? "1" : "0")
                : null;
    }

    private String getT1P59() {
        return main.benefitgroup_code.equals("22")
                && adi != null
                ? adi.employee_cat.charAt(0) == 'Ф' ? "1" : "0"
                : null;
    }

    private String getLgotP4() {
        if (main.request_type != 64) switch (main.request_subtype / 256){
            case 1:  return "1";
            case 0:
            case 2:  return "2";
        }
        return "0";
    }

    private String getLgotP5() {
        switch (main.oper + main.oper_g){
            case "ON": return "1";
            case "OG": return "2";
            case "VN": return "3";
            case "OO": return "4";
            default:   return "0";
        }
    }

    private String getLgotP14() {
        if (adi == null || adi.surname == null) return null;

        String surname = adi.surname.trim();
        String initials = adi.initials == null ? null : adi.initials.trim();

        return surname + (initials == null ? "" : " " + initials);
    }

    private String getLgotP15() {
        if (adi == null || adi.dependent_surname == null) return null;

        String surname = adi.dependent_surname.trim();
        String initials = adi.dependent_initials == null ? null : adi.dependent_initials.trim();

        return surname + (initials == null ? "" : " " + initials);
    }

    private Integer getLgotP16() {
        if (main.abonement_type.charAt(0) == '0'){
            return isRefund || isRefuse || isCancel
                    ? (short) -main.pass_qty
                    : main.pass_qty;
        }
        return 0;
    }

    private Boolean getLgotP17() {
        return main.flg_stickettype[2] || main.abonement_type.charAt(0) != 0;
    }

    private Integer getLgotP18() {
        if (main.abonement_type.charAt(0) != '0'){
            if (isRefuse || isCancel || isRefund)
                return -1;
            return 1;
        }
        return 0;
    }

    private String getLgotP20() {
        if (main.abonement_type.charAt(0) != '0')
            switch (main.abonement_type.trim()) {
                case "1": return "9";
                case "2": return "7";
                case "4": return "1";
                case "5": return "2";
                case "7": return "4";
                case "8": return "5";
                default: return  "0";
            }
        return " ";
    }

    private Double getLgotP27() {
        double sum = isBasic ? main.department_sum.doubleValue()
                : isRefuse || isCancel ? -main.department_sum.doubleValue()
                : isRefund ? -main.refunddepart_sum.doubleValue()
                : 0;

        return (Math.ceil(sum * 100) / 100);
    }

    private Double getLgotP28() {
        double sum = isBasic ? main.total_sum.doubleValue()
                : isRefuse || isCancel ? -main.total_sum.doubleValue()
                : isRefund ? -main.refund_sum.doubleValue()
                : 0;

        return (Math.ceil(sum * 100) / 100);
    }

    private String getTSite() {
        return HandbookDao.getTSite(
                main.web_id,
                main.sale_station.substring(0, 2),
                main.operation_date);
    }
}