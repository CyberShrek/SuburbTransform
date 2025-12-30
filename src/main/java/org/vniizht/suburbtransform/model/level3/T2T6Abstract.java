package org.vniizht.suburbtransform.model.level3;

import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.Route;

import java.util.Date;

@SuperBuilder(toBuilder=true)
public abstract class T2T6Abstract {
    public Date request_date;
    public String         p1;
    public String         p2;
    public Long           p3;
    public Integer        p4;

    public T2T6Abstract(String tableName, Date requestDate, Route route) {
        request_date = requestDate;
        p1 = tableName;
        p2 = "017";
        p4 = route.getSerial();
    }
}
