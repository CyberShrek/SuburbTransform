package org.vniizht.suburbtransform.model.handbook;

import lombok.ToString;

import java.sql.Date;

@ToString
public class Plagn {
    public String     idplagn;
    public String          vr;
    public String         gos;
    public Date         datan;
    public Date         datak;

    public Plagn(){}
}
