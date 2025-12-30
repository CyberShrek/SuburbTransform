package org.vniizht.suburbtransform.model.level3;

import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.FollowRoute;
import org.vniizht.suburbtransform.util.Util;

import java.util.Date;

@SuperBuilder
@ToString
public class T4 extends T2T6Abstract {
    public String          p5;
    public String          p6;
    public Float           p7;
    public Float           p8;
    public Integer         p9;

    public T4(Date requestDate, FollowRoute route,
              double regionIncomePerKm,
              double regionOutcomePerKm) {
        super("tab4", requestDate, route);
        p5 = Util.addLeadingZeros(route.getRoad(), 3);
        p6 = route.getOkato();
        p7 = (float) (route.getDistance() * regionIncomePerKm);
        p8 = (float) (route.getDistance() * regionOutcomePerKm);
        p9 = route.getDistance();
    }
}
