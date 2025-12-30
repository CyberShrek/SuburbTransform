package org.vniizht.suburbtransform.model.routes;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
public class FollowRoute extends RegionRoute {
    private String road;
}
