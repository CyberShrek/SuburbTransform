package org.vniizht.suburbtransform.service.dao;


import lombok.SneakyThrows;
import org.vniizht.suburbtransform.database.SimpleJdbc;
import org.vniizht.suburbtransform.model.routes.*;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RoutesDao { private RoutesDao() {}

    private final static Map<String, RouteGroup> routesCache = new HashMap<>();

    @SneakyThrows
    public static RouteGroup getRouteGroup(int routeNum,
                                    String depStation,
                                    String arrStation,
                                    Date date) {
        String key = depStation + arrStation + date;
        if (!routesCache.containsKey(key)) {
            RouteGroup group = new RouteGroup();

            ResultSet prigRS = SimpleJdbc.query("functions/estimate_km_suburb", new HashMap(){{
                put("routeNum", routeNum);
                put("date", date);
                put("depStation", depStation);
                put("arrStation", arrStation);
            }});
            
            while (prigRS.next()){
                int    narr     = prigRS.getInt("narr");
                String st1      = prigRS.getString("sto");
                String st2      = prigRS.getString("stn");
                String obj_chr  = prigRS.getString("obj_chr");
                int    obj_int  = prigRS.getInt("obj_int");
                int    pr_mcd   = prigRS.getInt("pr_mcd");
                int  rst        = prigRS.getInt("rst_p");

                switch (narr) {
                    case 1: group.addRegionRoute(RegionRoute.builder()
                            .region(obj_chr)
                            .okato(HandbookDao.getOkatoByRegion(obj_chr, date))
                            .distance(rst)
                            .build());
                    break;
                    case 2: group.addRoadRoute(RoadRoute.builder()
                            .road(obj_chr)
                            .distance(rst)
                            .build());
                    break;
                    case 3: group.addDepartmentRoute(DepartmentRoute.builder()
                            .road(String.valueOf(obj_int))
                            .department(obj_chr)
                            .distance(rst)
                            .build());
                    break;
                    case 4: group.addDcsRoute(DcsRoute.builder()
                            .road(String.valueOf(obj_int))
                            .dcs(obj_chr)
                            .distance(rst)
                            .build());
                    break;
                    case 5:
                        boolean isMcd = obj_chr != null && obj_chr.trim().equals("1");
                        group.addMcdRoute(McdRoute.builder()
                            .code(isMcd ? "1" : "0")
                            .distance(isMcd ? rst : 0)
                            .build());
                    break;
                    case 6: group.addFollowRoute(FollowRoute.builder()
                            .road(String.valueOf(obj_int))
                            .region(obj_chr)
                            .okato(HandbookDao.getOkatoByRegion(obj_chr, date))
                            .distance(rst)
                            .build());
                }
            }
            routesCache.put(key, group);
        }
        return routesCache.get(key);
    }

    @SneakyThrows
    public static RouteGroup getRouteGroup(String trainId, String trainThread, Date trainDepartureDate,
                                    String depStation, String arrStation) {
        String key = trainId + trainThread + trainDepartureDate + depStation + arrStation;
        if (!routesCache.containsKey(key)) {
            RouteGroup group = new RouteGroup();

            Map<String, Object> params = new HashMap(){{
                put("trainId", trainId);
                put("trainThread", trainThread);
                put("trainDepartureDate", trainDepartureDate);
                put("depStation", depStation);
                put("arrStation", arrStation);
            }};

            ResultSet roadsRS       = SimpleJdbc.query("functions/passkm_estimate_for_gos_and_dor", params);
            ResultSet departmentsRS = SimpleJdbc.query("functions/passkm_estimate_for_otd",         params);
            params.put("mode", 5);
            ResultSet regionsRS     = SimpleJdbc.query("functions/passkm_estimate_for_stan_dcs_sf", params);
            params.put("mode", 7);
            ResultSet followsRS     = SimpleJdbc.query("functions/passkm_estimate_for_stan_dcs_sf", params);
            params.put("mode", 4);
            ResultSet dcsRS         = SimpleJdbc.query("functions/passkm_estimate_for_stan_dcs_sf", params);

            while (roadsRS.next()) {
                group.addRoadRoute(RoadRoute.builder()
                        .road(roadsRS.getString("dor3"))
                        .build());
            }
            while (departmentsRS.next()) {
                group.addDepartmentRoute(DepartmentRoute.builder()
                        .road(departmentsRS.getString("dor3"))
                        .department(departmentsRS.getString("otd"))
                        .distance(departmentsRS.getInt("km"))
                        .build());
            }
            while (regionsRS.next()) {
                String sf = regionsRS.getString("sf");
                group.addRegionRoute(RegionRoute.builder()
                        .region(sf)
                        .okato(HandbookDao.getOkatoByRegion(sf, trainDepartureDate))
                        .distance(regionsRS.getInt("km"))
                        .build());
            }
            while (followsRS.next()) {
                group.addFollowRoute(FollowRoute.builder()
                        .road(followsRS.getString("dor3"))
                        .region(followsRS.getString("sf"))
                        .okato(HandbookDao.getOkatoByRegion(followsRS.getString("sf"), trainDepartureDate))
                        .distance(followsRS.getInt("km"))
                        .build());
            }
            while (dcsRS.next()) {
                group.addDcsRoute(DcsRoute.builder()
                        .road(dcsRS.getString("dor3"))
                        .dcs(dcsRS.getString("dcs"))
                        .distance(dcsRS.getInt("km"))
                        .build());
            }

            routesCache.put(key, group);
        }
        return routesCache.get(key);
    }
}
