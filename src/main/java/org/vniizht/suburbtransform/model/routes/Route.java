package org.vniizht.suburbtransform.model.routes;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder=true)
public class Route {
    Integer serial;
    Integer distance;

    Route (Integer distance) {
        this.distance = distance;
    }
}
