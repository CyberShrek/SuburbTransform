package org.vniizht.suburbtransform.service;

import org.vniizht.suburbtransform.model.level2.PassCost;
import org.vniizht.suburbtransform.model.level2.PassEx;
import org.vniizht.suburbtransform.model.level2.PassLgot;
import org.vniizht.suburbtransform.model.level2.PassMain;
import org.vniizht.suburbtransform.model.level3.CO22Meta;
import org.vniizht.suburbtransform.model.level3.Lgot;
import org.vniizht.suburbtransform.model.level3.T1;
import org.vniizht.suburbtransform.model.routes.RouteGroup;
import org.vniizht.suburbtransform.service.dao.HandbookDao;
import org.vniizht.suburbtransform.service.dao.Level2Dao;
import org.vniizht.suburbtransform.service.dao.RoutesDao;
import org.vniizht.suburbtransform.util.Util;

import java.util.*;

public final class Level3Pass extends Level3 <Level2Dao.PassCursor> {

    // Переменные для каждой записи
    private Integer        yyyyMM;
    private PassMain       main;
    private List<PassCost> costList;
    private List<PassEx>   exList;
    private PassEx         ex;
    private List<PassLgot> lgotList;
    private PassLgot       lgot;
    private Date           operationDate;
    private String         noUse;

    private boolean lgotGroupIs22;
    
    public Level3Pass(Long initialT1Serial) {
        super(initialT1Serial);
    }

    @Override
    protected void next(Level2Dao.PassCursor cursor) {
        main     = cursor.getMain();
        costList = cursor.getCost();
        exList   = cursor.getEx();
        ex       = exList.isEmpty() ? null : exList.get(0);
        lgotList = cursor.getLgot();
        lgot     = lgotList.isEmpty() ? null : lgotList.get(0);
        operationDate = Objects.equals(main.oper_g, "N") && Objects.equals(main.oper, "V")
                && cursor.getRefund() != null
                && Objects.equals(cursor.getRefund().flg_retpret, "1")
                ? main.request_date
                : main.oper_date;
        yyyyMM   = Integer.parseInt(Util.formatDate(operationDate, "yyyyMM"));
        if (cursor.getUpd() != null && cursor.getUpd().no_use != null)
            noUse = cursor.getUpd().no_use;
        else
            noUse = "0";

        lgotGroupIs22 = lgot != null && lgot.benefit_prigcode != null && lgot.benefit_prigcode.startsWith("22");
    }

    @Override
    protected boolean t1Exists() {
        return !Objects.equals(noUse, "1");
    }

    @Override
    protected boolean lgotExists() {
        return !Objects.equals(noUse, "1")
                && !main.benefit_code.equals("000")
                && !main.benefit_code.equals("021");
    }

    @Override
    protected T1 getT1() {
        String saleRoad = HandbookDao.getRoad3(main.sale_station, operationDate);
        return T1.builder()
                .request_date(main.request_date)
                .yyyymm(yyyyMM)
                .p1("tab1")
                .p3(Util.formatDate(operationDate, "yyyy"))
                .p4(Util.formatDate(operationDate, "MM"))
                .p5("017")
                .p6(saleRoad)
                .p7(saleRoad)
                .p8(main.sale_station)
                .p9(String.format("%09d", main.carrier_code))
                .p10(main.saleregion_code)
                .p11(HandbookDao.getOkatoByStation(main.sale_station, operationDate))
                .p12(Util.formatDate(main.departure_date, "yyMM"))
                .p15(main.departure_station)
                .p17(HandbookDao.getOkatoByStation(main.departure_station, operationDate))
                .p18(HandbookDao.getArea(main.departure_station, operationDate))
                .p19("4")
                .p20("0" + main.carriage_class)
                .p21("1")
                .p22(getT1P22())
                .p23("3")
                .p24(getT1P24())
                .p25(getT1P25())
                .p26(getT1P26()) // !!!!!!!!!!!!!!!!!!!!!
                .p30(HandbookDao.getOkatoByStation(main.arrival_station, main.arrival_date))
                .p31(HandbookDao.getArea(main.arrival_station, main.arrival_date))
                .p32(main.distance)
                .p52("1")
                .p53(String.valueOf(main.agent_code))
                .p54(main.arrival_station)
                .p55("0")
                .p56("000")
                .p57(" ")
                .p58(getT1P58())
                .p59(getT1P59())
                .p60(String.valueOf(main.subagent_code))
                .p61("0")
                .p62(0)
                .p63("0")

                .p33(Objects.equals(noUse, "2") && (Objects.equals(main.oper_g, "N") || Objects.equals(main.oper, "O"))
                        ? Long.valueOf(main.persons_qty)
                        : Long.valueOf(main.seats_qty))
                .p34(0.0)
                .p35(0.0)
                .p36((getLgotP28()))
                .p37(0.0)
                .p38(0.0)
                .p39(getT1P39())
                .p40(getT1P40())
                .p41(0.0)
                .p42(0.0)
                .p43(0.0)
                .p44(getT1P44())
                .p45(0.0)
                .p46(0.0)
                .p47(getT1P47())
                .p48(getT1P48())
                .p49(0.0)
                .p50(0.0)
                .p51(getT1P51())
                .build();
    }

    @Override
    protected Lgot getLgot() {
        return Lgot.builder()
                .request_date(main.request_date)
                .yyyymm(yyyyMM)
                .list("R800" + (Objects.equals(main.paymenttype, "Ж")
                        && lgotGroupIs22 ? 'Z' : 'G'))
                .p2(HandbookDao.getRoad3(main.sale_station, operationDate))
                .p3(HandbookDao.getDepartment(main.sale_station, operationDate))
                .p4("0")
                .p5(getLgotP5())
                .p6("1")
                .p7(getT1P24())
                .p8(Util.addLeadingZeros(String.valueOf(main.carrier_code), 4))
                .p9(HandbookDao.getOkatoByRegion(main.benefitcnt_code, operationDate))
                .p10(lgot == null ? null : lgot.document_num)
                .p11(getLgotP11())
                .p12(getLgotP12())
                .p13(getLgotP13())
                .p14(getLgotP14())
                .p16(getLgotP16())
                .p17(Objects.equals(main.trip_direction, "3"))
                .p18(0)
                .p20(" ")
                .p21(0)
                .p22(operationDate)
                .p23(main.departure_date)
                .p24(ex == null ? null : ex.ticket_ser.substring(0, 2) + ex.ticket_num)
                .p25(main.departure_station)
                .p26(main.arrival_station)
                .p30(Util.formatDate(new Date(main.request_date.getTime() + main.request_time.getTime()), "ddMMyyHHmm"))
                .p32(ex == null ? null : ex.snils)
                .p34(main.sale_station)
                .p35(main.agent_code == null ? null : String.valueOf(main.agent_code))

                .p19(0)
                .p27(getLgotP27())
                .p28(getLgotP28())
                .p33(0)
                .build();
    }

    @Override
    protected RouteGroup getRouteGroup() {
        return RoutesDao.getRouteGroup(
                main.train_num,
                main.train_thread,
                main.departure_date,
                main.departure_station,
                main.arrival_station
        );
    }

    @Override
    protected Set<T1> multiplyT1(T1 t1) {
        Set<T1> result = new LinkedHashSet<>();
        result.add(t1);
        if (!operationDate.equals(main.departure_date)) {
            result.add(t1.toBuilder()
                    .yyyymm(Integer.parseInt(Util.formatDate(main.departure_date, "yyyyMM")))
                    .build()
            );
        }
        return result;
    }

    @Override
    protected CO22Meta getMeta() {
        return CO22Meta.builder()
                .request_date(main.request_date)
                .l2_pass_idnum(main.idnum)
                .operation_date(operationDate)
                .build();
    }

    @Override
    protected double getRegionIncomePerKm(String region) {
        // TODO
        return costList.stream().mapToDouble(cost -> cost.sum_nde.doubleValue()).sum() / main.distance;
    }

    @Override
    protected double getRegionOutcomePerKm(String region) {
        // TODO
        return costList.stream().mapToDouble(cost -> cost.sum_te.doubleValue()).sum() / main.distance;
    }

    private String getT1P22() {
        return main.f_tick.length > 2 && main.f_tick[2] ? "2"                                        // Детский
                : !main.benefit_code.equals("000") || main.f_tick.length > 4 && main.f_tick[4] ? "3"  // Льготный
                :  main.f_tick.length > 1 && main.f_tick[1] ? "'1' "                                    // Полный
                : "0" ;
    }

    private String getT1P24() {
        return Objects.equals(main.paymenttype, "В")
                ? "21" + String.format("%02d", main.military_code)
                : lgot != null && lgot.benefit_prigcode != null && !main.benefit_code.equals("000") && !main.benefit_code.equals("013")
                ? lgot.benefit_prigcode
                : "0000";
    }

    private String getT1P25() {
        switch (main.paymenttype) {
            case "8":                     return "3"; // Банковские карты
            case "9": case "В": case "Б": return "1"; // Льготные
            case "1": case "3":           return "2"; // Наличные
            case "6":                     return "5"; // Безнал для юр. лиц
            default:                      return "4"; // Электронный кошелёк
        }
    }

    private String getT1P26() {
        return lgot == null || lgot.benefit_prigcode == null || lgot.benefit_prigcode.length() < 2 ? null
                :
                HandbookDao.getGvc(
                        lgot.benefit_prigcode.substring(0, 2),
                        main.benefit_code, operationDate);
    }

    private Double getT1P39() {
        return costList.stream().mapToDouble(costListItem -> {
                    switch (costListItem.sum_code.intValue()) {
                        case 104: case 105: case 106:
                            return costListItem.sum_nde.doubleValue();
                        default: return 0;
                    }}
                ).sum();
    }

    private Double getT1P40() {
        return costList.stream().mapToDouble(
                        costListItem -> costListItem.sum_code.intValue() == 101
                                ? costListItem.sum_nde.doubleValue()
                                : 0
                ).sum();
    }

    private Double getT1P44() {
        return costList.stream().mapToDouble(costListItem -> {
                    switch (costListItem.sum_code.intValue()) {
                        case 101: case 116: {
                            switch (main.paymenttype) {
                                case "Б": case "В": case "Ж": case "9":
                                    return costListItem.sum_nde.doubleValue();
                            }
                        }
                    }
                    return 0F;
                }).sum();
    }

    private Double getT1P47() {
        return costList.stream().mapToDouble(costListItem -> {
                    switch (costListItem.sum_code.intValue()) {
                        case 104: case 105: case 106:
                            switch (main.paymenttype) {
                                case "Б": case "В": case "Ж": case "9":
                                    return costListItem.sum_nde.doubleValue();
                            }
                    }
                    return 0;
                }).sum();
    }

    private Double getT1P48() {
        return costList.stream().mapToDouble(costListItem -> {
                    if (costListItem.sum_code.intValue() == 101) switch (main.paymenttype) {
                        case "Б": case "В": case "Ж": case "9":
                            return costListItem.sum_nde.doubleValue();
                    }
                    return 0;
                }).sum();
    }

    private Long getT1P51() {
        if (Objects.equals(main.oper_g, "N")) {
            switch (main.oper) {
                case "O": return 1L;
                case "V": return -1L;
            }
            if (Objects.equals(noUse, "2"))
                return -1L;
        }
        else if (Objects.equals(main.oper_g, "O") && Objects.equals(noUse, "2")) {
            return 1L;
        }
        return 0L;
    }

    private String getT1P58() {
        if (lgot != null && lgot.bilgroup_code != null && lgot.bilgroup_code.length() >= 3) switch (lgot.bilgroup_code.charAt(2)) {
            case '0': case '1': case '2': case '3': case '4': return "0";
            case '5': case '6': case '7': case '8': case '9': return "1";
        }
        return null;
    }

    private String getT1P59() {
        if (Objects.equals(main.paymenttype, "Ж") && lgot != null
                && lgotGroupIs22
                && lgot.employee_cat != null && !lgot.employee_cat.isEmpty())
            switch (lgot.employee_cat){
            case "Ф": case "Д": return "1";
        }
        return "0";
    }

    private String getLgotP5() {
        switch (main.oper + main.oper_g) {
            case "ON": return "1";
            case "OG": return "2";
            case "VN": return "3";
            case "OO": return "4";
            case "VO": return "5";
        }
        return "0";
    }

    private String getLgotP11() {
        return lgotGroupIs22
                ? lgot.bilgroup_code
                : main.saleregion_code;
    }

    private String getLgotP12() {
        return lgotGroupIs22
                ? lgot.employee_unit
                : null;
    }

    private String getLgotP13() {
        return lgotGroupIs22
                ? lgot.employee_unit
                : "0";
    }

    private String getLgotP14() {
        if(ex == null || ex.last_name == null) return null;

        String lastName = ex.last_name.trim();
        String firstName = ex.first_name == null ? "" : ex.first_name.trim();
        String patronymic = ex.patronymic == null ? "" : ex.patronymic.trim();

        if (lgotGroupIs22)
            return lastName + ' '
                    + (firstName.isEmpty() ? "" : firstName.charAt(0))
                    + (patronymic.isEmpty() ? "" : patronymic.charAt(0));
        else
            return lastName + ' '
                    + (firstName.isEmpty() ? "" : firstName.substring(0, firstName.length() < 2 ? 1 : 2));
    }

    private int getLgotP16() {
        if (Objects.equals(noUse, "2")) {
            if (Objects.equals(main.oper, "O") && (Objects.equals(main.oper_g, "G") || Objects.equals(main.oper_g, "O")))
                return -1;
            if (Objects.equals(main.oper, "V") && Objects.equals(main.oper_g, "N"))
                return -1;
        }
        return 1;
    }

    private Double getLgotP27() {
        return costList == null ? 0 :
                (costList.stream().mapToDouble(
                        cost -> {
                            switch (cost.sum_code.intValue()) {
                                case 101:
                                case 102:
                                case 116:
                                    switch (main.paymenttype) {
                                        case "9":
                                        case "В":
                                        case "B":
                                        case "Б":
                                            return cost.sum_nde.doubleValue();
                                    }
                            }
                            return 0;
                        }).sum());
    }

    private Double getLgotP28() {
        return costList == null ? 0 :
                (costList.stream().mapToDouble(
                        cost -> {
                            switch (cost.sum_code.intValue()) {
                                case 101:
                                case 102:
                                case 116:
                                    switch (main.paymenttype) {
                                        case "1":
                                        case "6":
                                        case "8":
                                            return cost.sum_nde.doubleValue();
                                    }
                            }
                            return 0.0;
                        }).sum());
    }
}