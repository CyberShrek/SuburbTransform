package org.vniizht.suburbtransform.service.handbook;

import lombok.ToString;
import org.vniizht.suburbtransform.database.SimpleJdbc;
import org.vniizht.suburbtransform.model.handbook.*;

import java.util.*;

@ToString
public class HandbookDao {

    public Dork findDork(String kodd, String kodg, Date date) {
        if(date != null && dorMap.containsKey(kodd + kodg)) {
            Date serverDate = new Date();
            for (Dork dork : dorMap.get(kodd + kodg))
                if (dork != null
                        && date.compareTo(dork.d_datan) >= 0
                        && date.compareTo(dork.d_datak) <= 0
                        && serverDate.compareTo(dork.d_datani) >= 0
                        && serverDate.compareTo(dork.d_dataki) <= 0
                ) return dork;
        }
        return null;
    }

    public Stanv findStanv(String stan, Date date) {
        if(date != null && stanvMap.containsKey(stan)) {
            Date serverDate = new Date();
            for (Stanv stanv : stanvMap.get(stan))
                if (stanv != null
                        && date.compareTo(stanv.datand) >= 0
                        && date.compareTo(stanv.datakd) <= 0
                        && serverDate.compareTo(stanv.datani) >= 0
                        && serverDate.compareTo(stanv.dataki) <= 0
                ) return stanv;
        }
        return null;
    }

    public Site findSite(String idsite, String gos, Date date) {
        String key = idsite + gos;
        if (date != null && siteMap.containsKey(key))
            for(Site site : siteMap.get(key))
                if (site != null && date.compareTo(site.datan) >= 0 && date.compareTo(site.datak) <= 0)
                    return site;

        return null;
    }

    public Plagn findPlagn(String idplagn, String gos, Date date) {
        String key = idplagn + gos;
        if(date != null && plagnMap.containsKey(key))
            for(Plagn plagn : plagnMap.get(key))
                if (plagn != null && date.compareTo(plagn.datan) >= 0 && date.compareTo(plagn.datak) <= 0)
                    return plagn;

        return null;
    }

    public Sublx findSublx(String lg, Date date) {
        if(date != null && sublxMap.containsKey(lg))
            for(Sublx sublx : sublxMap.get(lg))
                if (sublx != null && date.compareTo(sublx.datan) >= 0 && date.compareTo(sublx.datak) <= 0)
                    return sublx;

        return null;
    }

    public Sf findSf(Integer vid, Date date) {
        if(date != null && sfMap.containsKey(vid))
            for(Sf sf : sfMap.get(vid))
                if (sf != null && date.compareTo(sf.sf_datan) >= 0 && date.compareTo(sf.sf_datak) <= 0)
                    return sf;

        return null;
    }

    public SeasonTrip findTrip(Short season_tick_code, Short period, Date date) {
        String key = "20" + season_tick_code + period;
        if (date != null && tripsMap.containsKey(key))
            for(SeasonTrip seasonTrip : tripsMap.get(key))
                if (seasonTrip != null && date.compareTo(seasonTrip.date_ni) >= 0 && date.compareTo(seasonTrip.date_ki) <= 0)
                    return seasonTrip;

        return null;
    }
    public SeasonTrip findTrip(Short season_tick_code, Short period) {
        return findTrip(season_tick_code, period, new Date());
    }

    // Key is used for codes. Multiple codes will be concatenated
    private final Map<String, List<Dork>>    dorMap           = new HashMap<>();
    // List is used to hold range of days as indices to provide quick access by date in the range
    private final Map<String,  List<Stanv>> stanvMap = new HashMap<>();
    private final Map<String,  List<Site>>  siteMap  = new HashMap<>();
    private final Map<String,  List<Plagn>> plagnMap = new HashMap<>();
    private final Map<String,  List<Sublx>> sublxMap = new HashMap<>();
    private final Map<Integer, List<Sf>>    sfMap    = new HashMap<>();
    private final Map<String,  List<SeasonTrip>>  tripsMap = new HashMap<>();

    public HandbookDao() throws Exception {
        List<Dork> dorkList         = SimpleJdbc.queryForObjects("handbook/findDorks",       Dork.class);
        List<Stanv> stanvList       = SimpleJdbc.queryForObjects("handbook/findStanvs",      Stanv.class);
        List<Site>  siteList        = SimpleJdbc.queryForObjects("handbook/findSites",       Site.class);
        List<Plagn> plagnList       = SimpleJdbc.queryForObjects("handbook/findPlagns",      Plagn.class);
        List<Sublx> sublxList       = SimpleJdbc.queryForObjects("handbook/findSublxs",      Sublx.class);
        List<Sf>    sfList          = SimpleJdbc.queryForObjects("handbook/findSfs",         Sf.class);
        List<SeasonTrip> tripsList  = SimpleJdbc.queryForObjects("handbook/findSeasonTrips", SeasonTrip.class);

        dorkList.forEach(dork -> {
            String key = dork.d_kod + "20";
            List<Dork> list = Optional.ofNullable(dorMap.get(key)).orElse(new ArrayList<>());
            list.add(dork);
            dorMap.put(key, list);
        });
        stanvList.forEach(stanv -> {
            List<Stanv> list = Optional.ofNullable(stanvMap.get(stanv.stan)).orElse(new ArrayList<>());
            list.add(stanv);
            stanvMap.put(stanv.stan, list);
        });
        siteList.forEach(site  -> { 
            String key = site.tsite + site.gos;
            List<Site> list = Optional.ofNullable(siteMap.get(site.idsite + site.gos)).orElse(new ArrayList<>());
            list.add(site);
            siteMap.put(key, list);
        });
        plagnList.forEach(plagn -> {
            String key = plagn.idplagn + plagn.gos;
            List<Plagn> list = Optional.ofNullable(plagnMap.get(plagn.idplagn + plagn.gos)).orElse(new ArrayList<>());
            list.add(plagn);
            plagnMap.put(key, list);
        });
        sublxList.forEach(sublx -> {
            List<Sublx> list = Optional.ofNullable(sublxMap.get(sublx.lg)).orElse(new ArrayList<>());
            list.add(sublx);
            sublxMap.put(sublx.lg, list);
        });
        sfList   .forEach(sf    -> {
            List<Sf> list = Optional.ofNullable(sfMap.get(sf.sf_vid)).orElse(new ArrayList<>());
            list.add(sf);
            sfMap.put(sf.sf_vid, list);
        });
        tripsList.forEach(seasonTrip -> {
            String key = seasonTrip.gos + seasonTrip.season_tick_code + seasonTrip.period;
            List<SeasonTrip> list = Optional.ofNullable(tripsMap.get(key)).orElse(new ArrayList<>());
            list.add(seasonTrip);
            tripsMap.put(key, list);
        });
    }
}
