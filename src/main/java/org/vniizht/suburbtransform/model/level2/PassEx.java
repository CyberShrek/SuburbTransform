package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

import java.math.BigDecimal;

@ToString(callSuper=true)
public class PassEx extends L2Key {

    public Integer     npp;
    public String      ticket_ser;
    public BigDecimal  ticket_num;
    public String      last_name;
    public String      first_name;
    public String      patronymic;
    public String      snils;

    public PassEx() {}
}
