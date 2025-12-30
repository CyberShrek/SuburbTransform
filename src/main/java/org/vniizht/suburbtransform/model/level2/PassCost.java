package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

@ToString(callSuper=true)
public class PassCost extends L2Key {
    public Integer     sum_code;
    public String      cnt_code;
    public String      dor_code;
    public String      paymenttype;
    public Float       sum_te;
    public Float       sum_nde;

    public PassCost() {}
}
