package org.vniizht.suburbtransform.model.handbook;

import lombok.ToString;

import java.sql.Date;

@ToString
public class Sf {

    public Integer      sf_vid;
    public String       sf_kodokato;
    public Date         sf_datan;
    public Date         sf_datak;

    public Sf() {}
}
