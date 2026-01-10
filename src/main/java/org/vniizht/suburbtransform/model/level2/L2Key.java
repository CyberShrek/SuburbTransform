package org.vniizht.suburbtransform.model.level2;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ToString
@EqualsAndHashCode
abstract public class L2Key implements Serializable {
    public Long       idnum;
    public BigDecimal yyyymm;
    public Date       request_date;

    public L2Key() {}
}
