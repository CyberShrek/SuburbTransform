package org.vniizht.suburbtransform.model.handbook;

import lombok.ToString;

import java.sql.Date;

@ToString
public class Site {
    public String      idsite;
    public String       tsite;
    public String         gos;
    public Date         datan;
    public Date         datak;

    public Site() {}
}
