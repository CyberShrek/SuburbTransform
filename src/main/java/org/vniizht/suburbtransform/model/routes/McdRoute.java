package org.vniizht.suburbtransform.model.routes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder(toBuilder=true)
public class McdRoute extends Route {
    private String code;
}
