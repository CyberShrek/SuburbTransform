package org.vniizht.suburbtransform.model.level2;

import lombok.ToString;

import java.sql.Date;
import java.sql.Time;

@ToString(callSuper=true)
public class PassMain extends L2Key {
    public Long        id;
    public Integer     request_subtype;
    public Time        request_time;
    public String      oper;
    public String      oper_g;
    public Date        oper_date;
    public Date        departure_date;
    public Date        arrival_date;
    public String      train_num;
    public String      train_thread;
    public Integer     agent_code;
    public Integer     subagent_code;
    public Integer     carrier_code;
    public String      saleregion_code;
    public String      paymenttype;
    public String      sale_station;
    public String      departure_station;
    public String      arrival_station;
    public String      f_tick;
    public String      carriage_class;
    public String      benefit_code;
    public String      benefitcnt_code;
    public Integer     military_code;
    public String      trip_direction;
    public Integer     distance;
    public Integer     persons_qty;
    public Integer     seats_qty;
}