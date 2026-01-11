package org.vniizht.suburbtransform.model.level3;

import lombok.experimental.SuperBuilder;
import org.vniizht.suburbtransform.model.routes.Route;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuperBuilder(toBuilder=true)
public abstract class T2T6 {
    public Date request_date;
    public String         p1;
    public String         p2;
    public Long           p3;
    public Integer        p4;

    public T2T6(String tableName, Date requestDate, Route route) {
        request_date = requestDate;
        p1 = tableName;
        p2 = "017";
        p4 = route.getSerial();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        for(Field field : this.getClass().getFields()) {
            try {
                map.put(field.getName(), field.get(this));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }
}
