package org.vniizht.suburbtransform.service.handbook;

import org.vniizht.suburbtransform.model.handbook.*;

import java.util.Date;
import java.util.Optional;

public class Handbook {

    private final HandbookDao dao;

    public Handbook() throws Exception {
        dao = new HandbookDao();
    }

    public String getRoad2(String stationCode, Date date) {
        Stanv stanv = dao.findStanv(stationCode, date);
        Dork dork = stanv == null ? null : dao.findDork(stanv.dor, stanv.gos, date);
        return dork == null ? null : dork.d_nomd2;
    }

    public String getRoad3(String stationCode, Date date) {
        Stanv stanv = dao.findStanv(stationCode, date);
        Dork dork = stanv == null ? null : dao.findDork(stanv.dor, stanv.gos, date);
        return dork == null ? null : dork.d_nom3;
    }

    public String getRegion(String stationCode, Date date) {
        Stanv stanv = dao.findStanv(stationCode, date);
        return stanv == null ? "00"
                : Optional.ofNullable(stanv.sf).orElse("00");
    }

    public String getDepartment(String stationCode, Date date) {
        Stanv stanv = dao.findStanv(stationCode, date);
        return stanv == null ? "000"
                : Optional.ofNullable(stanv.otd).orElse("000").substring(1);
    }

    public String getOkatoByRegion(String regionCode, Date date) {
        if(regionCode == null) return "00000";
        Sf sf = dao.findSf(Integer.valueOf(regionCode), date);
        if (sf == null) {
            return "00000";
        }
        return Optional.ofNullable(
                sf.sf_kodokato
        ).orElse("00000")
                .substring(0, 5);
    }

    public String getOkatoByStation(String stationCode, Date date) {
        if(stationCode == null) return "00000";
        return getOkatoByRegion(getRegion(stationCode, date), date);
    }

    public String getArea(String stationCode, Date date) {
        Stanv stanv = dao.findStanv(stationCode, date);
        return stanv == null ? null : stanv.nopr; // ??
    }

    public String getTSite(String siteId, String countryCode, Date date){
        Site site = dao.findSite(siteId, countryCode, date);
        return site == null ? "  " : site.tsite;
    }

    public String getPlagnVr(String plagnId, String countryCode, Date date){
        Plagn plagn = dao.findPlagn(plagnId, countryCode, date);
        return plagn == null ? "  " : plagn.vr;
    }

    public String getGvc(String benefitGroupCode, String benefitCode, Date date){
        Sublx sublx = dao.findSublx(benefitGroupCode + benefitCode, date);
        if (sublx == null || sublx.code_lg_gvc == null) {
            return null;
        }
        return String.valueOf(sublx.code_lg_gvc);
    }
}