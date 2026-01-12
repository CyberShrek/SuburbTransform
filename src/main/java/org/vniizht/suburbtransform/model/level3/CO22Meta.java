package org.vniizht.suburbtransform.model.level3;

import lombok.Builder;

import java.util.Date;

@Builder
public class CO22Meta {
    public Long t1_id;
    public Date request_date;
    public Long l2_prig_idnum;
    public Long l2_pass_idnum;
    public Date operation_date;
    public Date ticket_begdate;
    public Date ticket_enddate;
}
