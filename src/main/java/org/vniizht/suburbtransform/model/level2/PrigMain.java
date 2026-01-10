package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Date;

@ToString(callSuper = true)
public class PrigMain extends L2Key {
    public Long id;
    public Integer request_type;
    public Integer request_subtype;
    public String oper;
    public String oper_g;
    public Date operation_date;
    public Date ticket_begdate;
    public Date ticket_enddate;
    public String ticket_ser;
    public BigDecimal ticket_num;
    public String train_category;
    public String train_num;
    public Integer agent_code;
    public Integer carriage_code;
    public String paymenttype;
    public String sale_station;
    public String region_code;
    public String payagent_id;
    public String web_id;
    public String departure_station;
    public String arrival_station;
    public Integer pass_qty;
    public String abonement_type;
    public String abonement_subtype;
    public String carryon_type;
    public Integer carryon_weight;
    public Integer seatstick_limit;
    public String carriage_class;
    public String benefit_region;
    public String benefit_code;
    public String benefitgroup_code;
    public BigDecimal fee_sum;
    public BigDecimal refundfee_sum;
    public BigDecimal total_sum;
    public BigDecimal tariff_sum;
    public BigDecimal refund_sum;
    public BigDecimal department_sum;
    public BigDecimal refunddepart_sum;
    public String no_use;

    public String server_datetime;
    public BigDecimal server_reqnum;

}