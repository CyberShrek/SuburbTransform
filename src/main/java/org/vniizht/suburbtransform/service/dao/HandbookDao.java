package org.vniizht.suburbtransform.service.dao;

import lombok.SneakyThrows;
import org.vniizht.suburbtransform.model.handbook.*;

import java.util.Date;
import java.util.Optional;

public class HandbookDao { private HandbookDao () {}

    public static HandbookCache cache;

    @SneakyThrows
    public static void loadCache() {
        cache = new HandbookCache();
    }

    public static String getRoad2(String stationCode, Date date) {
        Stanv stanv = cache.findStanv(stationCode, date);
        Dork dork = stanv == null ? null : cache.findDork(stanv.dor, stanv.gos, date);
        return dork == null ? null : dork.d_nomd2;
    }

    public static String getRoad3(String stationCode, Date date) {
        Stanv stanv = cache.findStanv(stationCode, date);
        Dork dork = stanv == null ? null : cache.findDork(stanv.dor, stanv.gos, date);
        return dork == null ? null : dork.d_nom3;
    }

    public static String getRegion(String stationCode, Date date) {
        Stanv stanv = cache.findStanv(stationCode, date);
        return stanv == null ? "00"
                : Optional.ofNullable(stanv.sf).orElse("00");
    }

    public static String getDepartment(String stationCode, Date date) {
        Stanv stanv = cache.findStanv(stationCode, date);
        return stanv == null ? "000"
                : Optional.ofNullable(stanv.otd).orElse("000").substring(1);
    }

    public static String getOkatoByRegion(String regionCode, Date date) {
        if(regionCode == null) return "00000";
        Sf sf = cache.findSf(Integer.valueOf(regionCode), date);
        if (sf == null) {
            return "00000";
        }
        return Optional.ofNullable(
                sf.sf_kodokato
        ).orElse("00000")
                .substring(0, 5);
    }

    public static String getOkatoByStation(String stationCode, Date date) {
        if(stationCode == null) return "00000";
        return getOkatoByRegion(getRegion(stationCode, date), date);
    }

    public static String getArea(String stationCode, Date date) {
        Stanv stanv = cache.findStanv(stationCode, date);
        return stanv == null ? null : stanv.nopr; // ??
    }

    public static String getTSite(String siteId, String countryCode, Date date){
        Site site = cache.findSite(siteId, countryCode, date);
        return site == null ? "  " : site.tsite;
    }

    public static String getPlagnVr(String plagnId, String countryCode, Date date){
        Plagn plagn = cache.findPlagn(plagnId, countryCode, date);
        return plagn == null ? "  " : plagn.vr;
    }

    public static String getGvc(String benefitGroupCode, String benefitCode, Date date){
        Sublx sublx = cache.findSublx(benefitGroupCode + benefitCode, date);
        if (sublx == null || sublx.code_lg_gvc == null) {
            return null;
        }
        return String.valueOf(sublx.code_lg_gvc);
    }
}