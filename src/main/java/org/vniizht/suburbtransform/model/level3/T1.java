package org.vniizht.suburbtransform.model.level3;


import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.McdRoute;
import org.vniizht.suburbtransform.model.routes.RouteGroup;

@SuperBuilder
@ToString
public class T1 extends L3Key {

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
    public Float p34;
    public Float p35;
    public Float p36;
    public Float p37;
    public Float p38;
    public Float p39;
    public Float p40;
    public Float p41;
    public Float p42;
    public Float p43;
    public Float p44;
    public Float p45;
    public Float p46;
    public Float p47;
    public Float p48;
    public Float p49;
    public Float p50;
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

    public T1() {
    }

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
}
