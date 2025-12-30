package org.vniizht.suburbtransform.model.routes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder=true)
public class RegionRoute extends Route {
    private String    region;
    private String    okato;

//    public RegionRoute() {
//        super();
//    }
}
