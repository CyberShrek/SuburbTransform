package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

import java.math.BigDecimal;

@ToString(callSuper=true)
public class PassCost extends L2Key {
    public BigDecimal  sum_code;
    public String      cnt_code;
    public String      dor_code;
    public String      paymenttype;
    public BigDecimal  sum_te;
    public BigDecimal  sum_nde;

    public PassCost() {}
}
