package org.vniizht.suburbtransform.model.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RouteGroup {

    private List<RoadRoute>       roadRoutes;
    private List<DepartmentRoute> departmentRoutes;
    private List<RegionRoute>     regionRoutes;
    private List<DcsRoute>        dcsRoutes;
    private List<McdRoute>        mcdRoutes;
    private List<FollowRoute>     followRoutes;

    public List<RoadRoute> getRoadRoutes() {
        return getRoutesOrEmptyList(roadRoutes);
    }

    public List<DepartmentRoute> getDepartmentRoutes() {
        return getRoutesOrEmptyList(departmentRoutes);
    }

    public List<RegionRoute> getRegionRoutes() {
        return getRoutesOrEmptyList(regionRoutes);
    }

    public List<DcsRoute> getDcsRoutes() {
        return getRoutesOrEmptyList(dcsRoutes);
    }

    public List<McdRoute> getMcdRoutes() {
        return getRoutesOrEmptyList(mcdRoutes);
    }

    public List<FollowRoute> getFollowRoutes() {
        return getRoutesOrEmptyList(followRoutes);
    }
    
    public void addRoadRoute(RoadRoute route) {
        roadRoutes = collectRoute(route, roadRoutes);
    }

    public void addDepartmentRoute(DepartmentRoute route) {
        departmentRoutes = collectRoute(route, departmentRoutes);
    }

    public void addRegionRoute(RegionRoute route) {
        regionRoutes = collectRoute(route, regionRoutes);
    }

    public void addDcsRoute(DcsRoute route) {
        dcsRoutes = collectRoute(route, dcsRoutes);
    }

    public void addMcdRoute(McdRoute route) {
        mcdRoutes = collectRoute(route, mcdRoutes);
    }

    public void addFollowRoute(FollowRoute route) {
        followRoutes = collectRoute(route, followRoutes);
    }

    public RoadRoute getFirstRoadRoute() {
        return getFirstRouteOrNull(roadRoutes);
    }
    public RoadRoute getLastRoadRoute() {
        return getLastRouteOrNull(roadRoutes);
    }

    public DepartmentRoute getFirstDepartmentRoute() {
        return getFirstRouteOrNull(departmentRoutes);
    }
    public DepartmentRoute getLastDepartmentRoute() {
        return getLastRouteOrNull(departmentRoutes);
    }

    public RegionRoute getFirstRegionRoute() {
        return getFirstRouteOrNull(regionRoutes);
    }
    public RegionRoute getLastRegionRoute() {
        return getLastRouteOrNull(regionRoutes);
    }

    public DcsRoute getFirstDcsRoute() {
        return getFirstRouteOrNull(dcsRoutes);
    }
    public DcsRoute getLastDcsRoute() {
        return getLastRouteOrNull(dcsRoutes);
    }

    public McdRoute getFirstMcdRoute() {
        return getFirstRouteOrNull(mcdRoutes);
    }
    public McdRoute getLastMcdRoute() {
        return getLastRouteOrNull(mcdRoutes);
    }

    public FollowRoute getFirstFollowRoute() {
        return getFirstRouteOrNull(followRoutes);
    }
    public FollowRoute getLastFollowRoute() {
        return getLastRouteOrNull(followRoutes);
    }
    
    public void merge(RouteGroup group) {
        Optional.ofNullable(group.getRegionRoutes()).orElse(new ArrayList<>()).forEach(this::addRegionRoute);
        Optional.ofNullable(group.getRoadRoutes()).orElse(new ArrayList<>()).forEach(this::addRoadRoute);
        Optional.ofNullable(group.getDepartmentRoutes()).orElse(new ArrayList<>()).forEach(this::addDepartmentRoute);
        Optional.ofNullable(group.getDcsRoutes()).orElse(new ArrayList<>()).forEach(this::addDcsRoute);
        Optional.ofNullable(group.getMcdRoutes()).orElse(new ArrayList<>()).forEach(this::addMcdRoute);
        Optional.ofNullable(group.getFollowRoutes()).orElse(new ArrayList<>()).forEach(this::addFollowRoute);
    }

    private <T extends Route> List<T> getRoutesOrEmptyList(List<T> routes) {
        return routes == null ? new ArrayList<>() : routes;
    }

    private <T extends Route> List<T> collectRoute(T route, List<T> collector) {
        if(collector == null)
            collector = new ArrayList<>();

        collector.add(route);
        route.serial = collector.size();
        return collector;
    }

    private <T extends Route> T getFirstRouteOrNull(List<T> routes) {
        return routes == null || routes.isEmpty() ? null : routes.get(0);
    }
    private <T extends Route> T getLastRouteOrNull(List<T> routes) {
        return routes == null || routes.isEmpty() ? null : routes.get(routes.size() - 1);
    }
}