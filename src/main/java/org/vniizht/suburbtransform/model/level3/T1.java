package org.vniizht.suburbtransform.model.level3;


import lombok.Builder;
import lombok.ToString;
import org.vniizht.suburbtransform.model.routes.McdRoute;
import org.vniizht.suburbtransform.model.routes.RouteGroup;

import java.util.Date;

@Builder(toBuilder = true)
@ToString
public class T1 {

    public Date request_date;
    public Date operation_date;
    public Date ticket_begdate;
    public Integer yyyymm;
    public String p1;
    public Long p2;
    public String p3;
    public String p4;
    public String p5;
    public String p6;
    public String p7;
    public String p8;
    public String p9;
    public String p10;
    public String p11;
    public String p12;
    public String p13;
    public String p14;
    public String p15;
    public String p16;
    public String p17;
    public String p18;
    public String p19;
    public String p20;
    public String p21;
    public String p22;
    public String p23;
    public String p24;
    public String p25;
    public String p26;
    public String p27;
    public String p28;
    public String p29;
    public String p30;
    public String p31;
    public Integer p32;

    // VALUES
    public Long p33;
    public Double p34;
    public Double p35;
    public Double p36;
    public Double p37;
    public Double p38;
    public Double p39;
    public Double p40;
    public Double p41;
    public Double p42;
    public Double p43;
    public Double p44;
    public Double p45;
    public Double p46;
    public Double p47;
    public Double p48;
    public Double p49;
    public Double p50;
    public Long p51;
    // VALUES

    public String p52;
    public String p53;
    public String p54;
    public String p55;
    public String p56;
    public String p57;
    public String p58;
    public String p59;
    public String p60;
    public String p61;
    public Integer p62;
    public String p63;


    public void setRoutes(RouteGroup routeGroup) {

        p13 = routeGroup.getFirstRoadRoute() != null ? routeGroup.getFirstRoadRoute().getRoad() : null;
        p14 = routeGroup.getFirstDepartmentRoute() != null ? routeGroup.getFirstDepartmentRoute().getDepartment() : null;
        p16 = routeGroup.getFirstRegionRoute() != null ? routeGroup.getFirstRegionRoute().getRegion() : null;
        p27 = routeGroup.getLastRoadRoute() != null ? routeGroup.getLastRoadRoute().getRoad() : null;
        p28 = routeGroup.getLastDepartmentRoute() != null ? routeGroup.getLastDepartmentRoute().getDepartment() : null;
        p29 = routeGroup.getLastRegionRoute() != null ? routeGroup.getLastRegionRoute().getRegion() : null;
        p62 = routeGroup.getMcdRoutes().stream().mapToInt(McdRoute::getDistance).sum();

        if (routeGroup.getMcdRoutes().stream().anyMatch(mcdRoute -> mcdRoute.getCode().equals("1"))) {
            switch (routeGroup.getMcdRoutes().size()) {
                case 1:
                    p63 = "1";
                    break;
                case 2:
                    p63 = routeGroup.getMcdRoutes().get(0).getCode().equals("0") ? "2" : "3";
                    break;
                default:
                    p63 = "4";
                    break;
            }
        } else {
            p63 = "0";
        }
    }

    public void merge(T1 t1) {
        p33 += t1.p33;
        p34 += t1.p34;
        p35 += t1.p35;
        p36 += t1.p36;
        p37 += t1.p37;
        p38 += t1.p38;
        p39 += t1.p39;
        p40 += t1.p40;
        p41 += t1.p41;
        p42 += t1.p42;
        p43 += t1.p43;
        p44 += t1.p44;
        p45 += t1.p45;
        p46 += t1.p46;
        p47 += t1.p47;
        p48 += t1.p48;
        p49 += t1.p49;
        p50 += t1.p50;
        p51 += t1.p51;
    }

    public String getKey() {
        return new StringBuilder()
                .append(request_date)
                .append(operation_date)
                .append(ticket_begdate)
                .append(yyyymm)
                .append(p1)
                .append(p2)
                .append(p3)
                .append(p4)
                .append(p5)
                .append(p6)
                .append(p7)
                .append(p8)
                .append(p9)
                .append(p10)
                .append(p11)
                .append(p12)
                .append(p13)
                .append(p14)
                .append(p15)
                .append(p16)
                .append(p17)
                .append(p18)
                .append(p19)
                .append(p20)
                .append(p21)
                .append(p22)
                .append(p23)
                .append(p24)
                .append(p25)
                .append(p26)
                .append(p27)
                .append(p28)
                .append(p29)
                .append(p30)
                .append(p31)
                .append(p32)
                .append(p52)
                .append(p53)
                .append(p54)
                .append(p55)
                .append(p56)
                .append(p57)
                .append(p58)
                .append(p59)
                .append(p60)
                .append(p61)
                .append(p62)
                .append(p63)
                .toString();
    }
}
