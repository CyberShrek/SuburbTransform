package org.vniizht.suburbtransform.service;

import lombok.Getter;
import org.vniizht.suburbtransform.model.level3.*;
import org.vniizht.suburbtransform.model.routes.RouteGroup;
import org.vniizht.suburbtransform.service.dao.Level2Dao;
import org.vniizht.suburbtransform.util.Log;
import org.vniizht.suburbtransform.util.Util;


import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

abstract public class Level3 <L2_CURSOR extends Level2Dao.Cursor> {

    // Конечные результаты - трансформированные записи второго уровня, готовые к записи в базу данных
    @Getter private final Map<String, CO22> co22Result = new HashMap<>();
    @Getter private final Set<Lgot>         lgotResult = new HashSet<>();

    // Подсчитанное время на сопутствующие операции (с)
    @Getter private float transformationTime   = 0;
    @Getter private float routesSearchTime     = 0;

    // Функция обработки каждой записи второго уровня
    abstract protected void next(L2_CURSOR cursor);

    // Проверка существования t1
    abstract protected boolean   t1Exists();

    // Проверка существования льгот
    abstract protected boolean lgotExists();

    abstract protected T1 getT1();
    abstract protected Lgot      getLgot();
    abstract protected CO22Meta getMeta();

    // Мультипликатор
    abstract protected Set<T1> multiplyT1(T1 t1);

    // Маршруты
    abstract protected RouteGroup getRouteGroup();

    // Подсчёт средних стоимостей на километр по регионам
    abstract protected double getRegionIncomePerKm(String region);
    abstract protected double getRegionOutcomePerKm(String region);

    private Long t1Serial;

    protected Level3(Long initialT1Serial) {
        this.t1Serial = initialT1Serial;
    }

    public void runTransformation(L2_CURSOR cursor, Log log) {
        int progress = 1;
        while (cursor.hasNext()) {
            log.reline("Трансформирую... " + progress++ + " из " + cursor.size());
            transform((L2_CURSOR) cursor.next());
        }
        log.nextLine();
        arrangeResult();
        roundTimes();
    }

    private void transform(L2_CURSOR cursor) {
        next(cursor);
        if(t1Exists()) {
            AtomicReference<RouteGroup> routeGroup = new AtomicReference<>();
            routesSearchTime   += Util.measureTime(() -> routeGroup.set(getRouteGroup()));
            transformationTime += Util.measureTime(() -> {
                T1 t1           = getT1();
                t1.setRoutes(routeGroup.get());
                multiplyT1(t1).forEach(t1Copy -> {
                    String key = t1Copy.getKey();
                    if(co22Result.containsKey(key))
                        co22Result.get(key).merge(t1Copy);
                    else
                        co22Result.put(key, new CO22(t1Copy, routeGroup.get()));
                });
            });
        }
        if(lgotExists()) {
            transformationTime += Util.measureTime(() -> lgotResult.add(getLgot()));
        }
    }

    private void arrangeResult() {
        co22Result.values().forEach(co22 -> {
            co22.assignSerials();
            co22.arrangeCosts();
        });
    }

    private void roundTimes() {
        transformationTime = (float) Math.round(transformationTime * 100) / 100;
        routesSearchTime   = (float) Math.round(routesSearchTime   * 100) / 100;
    }

    // ЦО22 включая все дочерние записи
    @Getter public class CO22 {
        private final T1 t1;
        private final List<T2>       t2 = new ArrayList<>();
        private final List<T3>       t3 = new ArrayList<>();
        private final List<T4>       t4 = new ArrayList<>();
        private final List<T6>       t6 = new ArrayList<>();
        private final Set<CO22Meta> metas = new HashSet<>();

        CO22(T1 t1, RouteGroup routeGroup) {
            Date requestDate = t1.request_date;
            this.t1 = t1;
            routeGroup.getDepartmentRoutes().forEach(route  -> t2.add(new T2(requestDate, route)));
            routeGroup.getRegionRoutes().forEach(route      -> t3.add(new T3(requestDate, route)));
            routeGroup.getFollowRoutes().forEach(route      -> {
                T4 t4 = new T4(requestDate, route,
                        (long) getRegionIncomePerKm(route.getRegion()),
                        (long) getRegionOutcomePerKm(route.getRegion()));
                if (t4.p7 != 0 || t4.p8 != 0)
                    this.t4.add(t4);
            });
            routeGroup.getDcsRoutes().forEach(route         -> t6.add(new T6(requestDate, route)));
            metas.add(getMeta());
        }

        void merge(T1 t1) {
            this.t1.merge(t1);
            metas.add(getMeta());
        }

        void assignSerials() {
            t1.p2 = t1Serial;
            t2.forEach(t -> t.p3 = t1Serial);
            t3.forEach(t -> t.p3 = t1Serial);
            t4.forEach(t -> t.p3 = t1Serial);
            t6.forEach(t -> t.p3 = t1Serial);
            metas.forEach(t -> t.id = t1Serial);
            t1Serial++;
        }

        void arrangeCosts() {arrangeCosts(false);}
        void arrangeCosts(boolean edgeOnly) {
            if (t4.isEmpty()) return;
            double incomeDelta  = t1.p36 - t4.stream().mapToDouble(t4 -> t4.p7).sum();
            double outcomeDelta = t1.p44 - t4.stream().mapToDouble(t4 -> t4.p8).sum();

            if(edgeOnly || t1.p21.equals("6")) {
                // Добавление разницы в первую запись
                T4 firstT4 = t4.get(0);
                firstT4.p4 = (int)   (firstT4.p7 + incomeDelta);
                firstT4.p8 = (float) (firstT4.p8 + outcomeDelta);
            } else {
                // Распределение разницы по всем записям в зависимости от километража
                int totalDistance = t4.stream().mapToInt(t4 -> t4.p9).sum();
                for (T4 t4Item : t4) {
                    t4Item.p7 = ((float) Math.round((t4Item.p7 + incomeDelta * t4Item.p9 / totalDistance) * 100) / 100);
                    t4Item.p8 = ((float) Math.round((t4Item.p8 + outcomeDelta * t4Item.p9 / totalDistance) * 100) / 100);
                }

                // Возможный остаток при распределении добавляется в первую запись
                arrangeCosts(true);
            }
        }
    }
}