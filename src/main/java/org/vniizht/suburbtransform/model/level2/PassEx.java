package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

@ToString(callSuper=true)
public class PassEx extends L2Key {

    public Integer     npp;
    public String      ticket_ser;
    public Integer     ticket_num;
    public String      lgot_info;
    public String      nomlgud;
    public String      last_name;
    public String      first_name;
    public String      patronymic;
    public String      snils;

    public PassEx() {}
}
