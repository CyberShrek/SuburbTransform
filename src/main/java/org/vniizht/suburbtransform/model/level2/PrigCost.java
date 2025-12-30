package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

@ToString(callSuper=true)
public class PrigCost extends L2Key {
    public Integer     doc_reg;
    public Integer     route_num;
    public Integer     route_distance;
    public Long        tariff_sum;
    public Long        department_sum;
    public String      departure_station;
    public String      arrival_station;
    public String      region_code;
    public String      tarif_type;
}
