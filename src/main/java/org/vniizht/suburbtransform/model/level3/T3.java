package org.vniizht.suburbtransform.model.level3;

import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.RegionRoute;

import java.util.Date;

@SuperBuilder
@ToString
public class T3 extends T2T6 {
    public String          p5;
    public String          p6;
    public Integer           p7;

    public T3(Date requestDate, RegionRoute route) {
        super("tab3", requestDate, route);
        p5 = route.getRegion();
        p6 = route.getOkato();
        p7 = route.getDistance();
    }
}
