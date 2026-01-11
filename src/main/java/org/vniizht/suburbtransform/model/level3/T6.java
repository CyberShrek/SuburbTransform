package org.vniizht.suburbtransform.model.level3;


import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.DcsRoute;
import org.vniizht.suburbtransform.util.Util;

import java.util.Date;

@SuperBuilder(toBuilder=true)
@ToString
public class T6 extends T2T6 {
    public String          p5;
    public Integer         p6;
    public Integer         p7;

    public T6(Date requestDate, DcsRoute route) {
        super("tab6", requestDate, route);
        p5 = Util.addLeadingZeros(route.getRoad(), 3);
        p6 = Integer.valueOf(route.getDcs());
        p7 = route.getDistance();
    }
}
