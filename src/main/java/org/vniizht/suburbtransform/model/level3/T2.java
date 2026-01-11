package org.vniizht.suburbtransform.model.level3;

import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.DepartmentRoute;
import org.vniizht.suburbtransform.util.Util;

import java.util.Date;

@SuperBuilder
@ToString
public class T2 extends T2T6 {
    public String          p5;
    public String          p6;
    public Integer         p7;

    public T2(Date requestDate, DepartmentRoute route) {
        super("tab2", requestDate, route);
        p5 = Util.addLeadingZeros(route.getRoad(), 3);
        p6 = route.getDepartment();
        p7 = route.getDistance();
    }
}
