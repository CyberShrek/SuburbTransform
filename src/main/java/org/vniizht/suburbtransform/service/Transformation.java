package org.vniizht.suburbtransform.service;

import org.vniizht.suburbtransform.model.TransformationOptions;
import org.vniizht.suburbtransform.service.handbook.Handbook;
import org.vniizht.suburbtransform.util.Log;
import org.vniizht.suburbtransform.util.Util;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Transformation {

//    private static int PORTION_SIZE = 25000;
//
//    @Autowired private Level2Dao level2;
//    @Autowired private Level3Dao level3;
//    private Handbook handbook;
//    @Autowired private RoutesDao routes;
//    @Autowired private TripsDao  trips;
//
//    private Log log;
//    private int pageSize;
//
//    public synchronized void transform(TransformationOptions options) throws Exception {
//
//        Date startTime = new Date();
//        log = new Log();
//        try {
//            boolean prigWasTransformed = level3.prigWasTransformedForDate(options.date),
//                    passWasTransformed = level3.passWasTransformedForDate(options.date);
//
//            if(!options.pass && !options.prig) { // auto
//                options.prig = !prigWasTransformed;
//                options.pass = !passWasTransformed;
//                if (!options.prig && !options.pass) {
//                    options.prig = options.pass = true;
//                };
//            }
//            if(options.date == null) {
//                options.date = getRequestDate();
//                if(options.date == null) return;
//            }
//
//            log.addTimeLine("Выполняю трансформацию записей за "
//                    + Util.formatDate(options.date, "dd.MM.yyyy"));
//            log.sumUp((options.prig ? " l2_prig" : "") + (options.pass ? " l2_pass" : ""));
//
//            log.addTimeLine("Получаю справочники...");
//            handbook.loadCache();
//
//            log.addTimeLine("Удаляю старые записи третьего уровня за " + Util.formatDate(options.date, "dd.MM.yyyy") + "...");
//
//            if (options.prig) {
//                log.addLine("l2_prig:");
//                if (prigWasTransformed)
//                    level3.deletePrigForDate(options.date, log);
//                else
//                    log.addLine("Нечего удалять");
//            }
//            if (options.pass) {
//                log.addLine("l2_pass:");
//                if (passWasTransformed)
//                    level3.deletePassForDate(options.date, log);
//                else
//                    log.addLine("Нечего удалять");
//            }
//            if (passWasTransformed && options.pass || prigWasTransformed && options.prig) {
//                log.addLine("lgot:");
//                level3.deleteLgotForDate(options.date, log);
//            }
//            log.sumUp();
//
//            pageSize = calculatePageSize();
//            if (options.prig) {
//                log.addTimeLine("Выполняю трансформацию l2_prig...");
//                complete(transformPrigOrNull(options.date));
//            }
//            if (options.pass) {
//                log.addTimeLine("Выполняю трансформацию l2_pass...");
//                complete(transformPassOrNull(options.date));
//            }
//            log.addTimeLine("Конец выполнения.");
//        } catch (Exception e) {
//            log.error(e.getLocalizedMessage());
//            throw e;
//        } finally {
//            handbook.clearCache();
//            routes.clearCache();
//            log.finish("Итоговое время выполнения: " + (new Date().getTime() - startTime.getTime()) / 1000 + "с");
//        }
//    }
//
//    private int calculatePageSize() {
////        long availableMemoryInMB = Runtime.getRuntime().maxMemory() / 1024 / 1024;
////        log.addTimeLine("Доступный объём памяти: " + availableMemoryInMB + " Мб");
////        int portionSize = (int) (availableMemoryInMB * 1024 / ESTIMATED_RECORD_SIZE_IN_B);
//        log.addTimeLine("Расчётный размер порции: " + PORTION_SIZE);
//        return PORTION_SIZE;
//    }
//
//    private Level3Prig transformPrigOrNull(Date requestDate) {
//
//        List<Long> idnums = level2.findPrigIdnumsByRequestDate(requestDate);
//
//        return idnums.isEmpty() ? null : (Level3Prig) transform(
//                new Level3Prig(handbook, routes, trips, level3.getLatestT1P2() + 1),
//                idnums,
//                (List<Long> currentIdnums) -> level2.findPrigRecordsByIdnums(requestDate, currentIdnums),
//                "l2_prig"
//        );
//    }
//
//    private Level3Pass transformPassOrNull(Date requestDate) {
//        List<Long> idnums = level2.findPassIdnumsByRequestDate(requestDate);
//
//        return idnums.isEmpty() ? null : (Level3Pass) transform(
//                new Level3Pass(handbook, routes, level3.getLatestT1P2() + 1),
//                idnums,
//                 (List<Long> currentIdnums) -> level2.findPassRecordsByIdnums(requestDate, currentIdnums),
//                 "l2_pass"
//        );
//    }
//
//    private Level3 transform(Level3 level3,
//                             List<Long> idnums,
//                             Function<List<Long>, Set<Level2Dao.Record>> recordsLoader,
//                             String name) {
//        log.addTimeLine("Найдено записей " + name + ": " + idnums.size());
//
//        List<List<Long>> pagedIdnums = Util.splitList(idnums, pageSize);
//        log.addTimeLine("Порций: " + pagedIdnums.size());
//
//        for (int i = 0; i < pagedIdnums.size(); i++) {
//            List<Long> currentIdnums = pagedIdnums.get(i);
//            log.addTimeLine("Порция №" + (i + 1) + ": загружаю записи (" + currentIdnums.size() + ")...");
//            Set<Level2Dao.Record> records = recordsLoader.apply(currentIdnums);
//
//            log.addTimeLine("Порция №" + (i + 1) + ": трансформирую...");
//            level3.transform(records);
//        }
//        level3.finish();
//
//        log.addTimeLine("Записи " + name + " успешно трансформированы.");
//
//        return level3;
//    }
//
//    private Date getRequestDate() {
//        log.addTimeLine("Определяю дату запроса...");
//        Date requestDate = level3.getNextRequestDate();
//        if(requestDate == null) {
//            log.addTimeLine("На третьем уровне ещё нет данных. Повторите запрос с указанием даты.");
//        }
//        return requestDate;
//    }
//
//    private void complete(Level3 nullableLevel3) {
//
//        if (nullableLevel3 == null) {
//            log.sumUp("Нет данных для формирования ЦО-22.");
//            return;
//        }
//        Set<Level3.CO22> co22Set = new HashSet<>(nullableLevel3.getCo22Result().values());
//
//        log.sumUp("Сформировано записей ЦО-22:      " + co22Set.size(),
//                "Сформировано записей Льготников: " + nullableLevel3.getLgotResult().size());
//
//        update(co22Set, nullableLevel3.getLgotResult());
//    }
//
//    private void update(Set<Level3.CO22> co22Set, Set<Lgot> lgotSet) {
//        log.sumUp("\tЗатрачено времени на перезапись: " + Util.measureTime(() -> {
//
//            Set<T1> t1Set = co22Set.stream().map(Level3.CO22::getT1).collect(Collectors.toSet());
//            if (!t1Set.isEmpty()) {
//                log.addTimeLine("Записываю T1 (" + t1Set.size() + ")...");
//                level3.saveT1s(t1Set);
//            }
//            Set<T2> t2Set = new HashSet<>();
//            co22Set.forEach(co22 -> t2Set.addAll(co22.getT2()));
//            if (!t2Set.isEmpty()) {
//                log.addTimeLine("Записываю T2 (" + t2Set.size() + ")...");
//                level3.saveT2s(t2Set);
//            }
//            Set<T3> t3Set = new HashSet<>();
//            co22Set.forEach(co22 -> t3Set.addAll(co22.getT3()));
//            if (!t3Set.isEmpty()) {
//                log.addTimeLine("Записываю T3 (" + t3Set.size() + ")...");
//                level3.saveT3s(t3Set);
//            }
//            Set<T4> t4Set = new HashSet<>();
//            co22Set.forEach(co22 -> t4Set.addAll(co22.getT4()));
//            if (!t4Set.isEmpty()) {
//                log.addTimeLine("Записываю T4 (" + t4Set.size() + ")...");
//                level3.saveT4s(t4Set);
//            }
//            Set<T6> t6Set = new HashSet<>();
//            co22Set.forEach(co22 -> t6Set.addAll(co22.getT6()));
//            if (!t6Set.isEmpty()) {
//                log.addTimeLine("Записываю T6 (" + t6Set.size() + ")...");
//                level3.saveT6s(t6Set);
//            }
//            Set<CO22Meta> co22MetaSet = new HashSet<>();
//            co22Set.forEach(co22 -> co22MetaSet.addAll(co22.getMetas()));
//            if (!co22MetaSet.isEmpty()) {
//                log.addTimeLine("Записываю метаданные ЦО-22 (" + co22MetaSet.size() + ")...");
//                level3.saveCO22Metas(co22MetaSet);
//            }
//            if (!lgotSet.isEmpty()) {
//                log.addTimeLine("Записываю льготников (" + lgotSet.size() + ")...");
//                level3.saveLgots(lgotSet);
//            }
//        }) + "c");
//    }
//
////    @PostConstruct
//    public void test() throws Exception {
//        int yyyy = 2024, mm = 2, dd = 9;
//        transform(new TransformationOptions(
//                new Date(yyyy - 1900, mm - 1, dd),
//                false,
//                true
//        ));
//    }
}