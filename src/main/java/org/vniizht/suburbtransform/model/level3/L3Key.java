package org.vniizht.suburbtransform.model.level3;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.util.Date;


@ToString
@SuperBuilder(toBuilder=true)
@EqualsAndHashCode
public abstract class L3Key {
    public Date request_date;
    public Integer yyyymm;

    public L3Key() {}
}
