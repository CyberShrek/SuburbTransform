package org.vniizht.suburbtransform.model.routes;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder=true)
public class Route {
    Short serial;
    Short distance;

    Route (Short distance) {
        this.distance = distance;
    }
}
