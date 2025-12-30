package org.vniizht.suburbtransform.model.level2;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@EqualsAndHashCode
abstract public class L2Key implements Serializable {
    public Long        idnum;
    public Integer     yyyymm;
    public Date        request_date;

    public L2Key() {}
}
