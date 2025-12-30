package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;
import java.sql.Date;

@ToString(callSuper=true)
public class PrigMain extends L2Key {
    public Long     id;
    public Integer      request_type;
    public Integer      request_subtype;
    public String       oper;
    public String       oper_g;
    public Date        operation_date;
    public Date        ticket_begdate;
    public Date        ticket_enddate;
    public String      ticket_ser;
    public Integer     ticket_num;
    public String   train_category;
    public String      train_num;
    public Integer       agent_code;
    public Integer       carriage_code;
    public String   paymenttype;
    public String      sale_station;
    public String      region_code;
    public String      payagent_id;
    public String      web_id;
    public String      departure_station;
    public String      arrival_station;
    public Integer       pass_qty;
    public String      abonement_type;
    public String   abonement_subtype;
    public String   carryon_type  ;
    public Integer       carryon_weight;
    public String   flg_2wayticket;
    public String   flg_child;
    public String   flg_bsp;
    public String   flg_carryon;
    public String   flg_fee_onboard;
    public Integer       seatstick_limit;
    public String   carriage_class;
    public String      benefit_region;
    public String      benefit_code;
    public String      benefitgroup_code;
    public Float       fee_sum;
    public Float       refundfee_sum;
    public Float       total_sum;
    public Float       tariff_sum;
    public Float       refund_sum;
    public Float       department_sum;
    public Float       refunddepart_sum;
    public String   no_use;

    public String      server_datetime;
    public Integer     server_reqnum;

}