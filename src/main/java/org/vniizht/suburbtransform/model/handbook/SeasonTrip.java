package org.vniizht.suburbtransform.model.handbook;

import lombok.ToString;

import java.util.Date;

@ToString
public class SeasonTrip {

    public Integer trip_code_id;
    public String gos;
    public Integer season_tick_code;
    public Integer period;
    public Integer kol_trips;
    public Integer kol__round_trips;

    public Date date_ni;

    public Date date_ki;

    public SeasonTrip() {}
}
