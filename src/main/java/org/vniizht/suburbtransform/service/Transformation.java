package org.vniizht.suburbtransform.service;

import org.vniizht.suburbtransform.model.TransformationOptions;
import org.vniizht.suburbtransform.model.level3.*;
import org.vniizht.suburbtransform.service.dao.HandbookDao;
import org.vniizht.suburbtransform.service.dao.Level2Dao;
import org.vniizht.suburbtransform.service.dao.Level3Dao;
import org.vniizht.suburbtransform.util.Log;
import org.vniizht.suburbtransform.util.Util;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Transformation { private Transformation() {}

    private static final Log log = new Log();

    public static synchronized void transformOrNull(TransformationOptions options) throws Exception {

        Date startTime = new Date();
        try {
            boolean prigWasTransformed = Level3Dao.prigWasTransformedForDate(options.date),
                    passWasTransformed = Level3Dao.passWasTransformedForDate(options.date);

            if(!options.pass && !options.prig) { // auto
                options.prig = !prigWasTransformed;
                options.pass = !passWasTransformed;
                if (!options.prig && !options.pass) {
                    options.prig = options.pass = true;
                };
            }
            if(options.date == null) {
                options.date = getRequestDate();
                if(options.date == null) return;
            }

            log.nextTimeLine("Начинаю трансформацию записей за "
                    + Util.formatDate(options.date, "dd.MM.yyyy"));
            log.sumUp((options.prig ? " l2_prig" : "") + (options.pass ? " l2_pass" : ""));

            log.nextTimeLine("Получаю справочники...");
            HandbookDao.loadCache();

            log.nextTimeLine("Удаляю старые записи третьего уровня за " + Util.formatDate(options.date, "dd.MM.yyyy") + "...");

            if (options.prig) {
                log.nextLine("prig:");

                if (prigWasTransformed) {
                    Level3Dao.deletePrigForDate(options.date);
                }
                else
                    log.nextLine("Нечего удалять");
            }
            if (options.pass) {
                log.nextLine("pass:");
                if (passWasTransformed) {
                    Level3Dao.deletePassForDate(options.date);
                }
                else
                    log.nextLine("Нечего удалять");
            }
            if (passWasTransformed && options.pass || prigWasTransformed && options.prig) {
                log.nextLine("lgot:");
                Level3Dao.deleteLgotForDate(options.date);
            }
            log.sumUp();

            if (options.prig) {
                log.nextTimeLine("Выполняю трансформацию l2_prig...");
                complete(transformPrigOrNull(options.date));
            }
            if (options.pass) {
                log.nextTimeLine("Выполняю трансформацию l2_pass...");
                complete(transformPassOrNull(options.date));
            }
            log.nextTimeLine("Конец выполнения.");
        } catch (Exception e) {
            log.error(e.getLocalizedMessage() == null ? "" : e.getLocalizedMessage());
            throw e;
        } finally {
            log.finish("Итоговое время выполнения: " + (new Date().getTime() - startTime.getTime()) / 1000 + "с");
        }
    }

    private static Level3Prig transformPrigOrNull(Date requestDate) {
        return (Level3Prig) transformOrNull(
                "l2_prig",
                () -> Level2Dao.loadPrig(requestDate),
                new Level3Prig(Level3Dao.getLatestT1P2() + 1)
        );
    }

    private static Level3Pass transformPassOrNull(Date requestDate) {
        return (Level3Pass) transformOrNull(
                "l2_prig",
                () -> Level2Dao.loadPass(requestDate),
                new Level3Pass(Level3Dao.getLatestT1P2() + 1)
        );
    }

    private static Level3 transformOrNull(String name,
                                   Supplier<Level2Dao.Cursor> cursorLoader,
                                   Level3 level3
                                   ) {
        log.nextTimeLine("Загружаю записи " + name + "...");
        Level2Dao.Cursor cursor = cursorLoader.get();
        log.nextTimeLine("Загружено записей main: " + cursor.size());

        if (cursor.size() == 0) return null;

        level3.runTransformation(cursor, log);

        log.nextTimeLine("Записи " + name + " успешно трансформированы.");

        return level3;
    }

    private static Date getRequestDate() {
        log.nextTimeLine("Определяю дату запроса...");
        Date requestDate = Level3Dao.getNextRequestDateOrNull();
        if(requestDate == null) {
            log.nextTimeLine("На третьем уровне ещё нет данных. Повторите запрос с указанием даты.");
        }
        return requestDate;
    }

    private static void complete(Level3 nullableLevel3) {

        if (nullableLevel3 == null) {
            log.sumUp("Нет данных для формирования ЦО-22.");
            return;
        }
        Set<Level3.CO22> co22Set = new HashSet<>(nullableLevel3.getCo22Result().values());

        log.sumUp("Сформировано записей ЦО-22:      " + co22Set.size(),
                "Сформировано записей Льготников: " + nullableLevel3.getLgotResult().size());

        update(co22Set, nullableLevel3.getLgotResult());
    }

    private static void update(Set<Level3.CO22> co22Set, Set<Lgot> lgotSet) {
        log.sumUp("\tЗатрачено времени на запись: " + Util.measureTime(() -> {

            Set<T1> t1Set = co22Set.stream().map(Level3.CO22::getT1).collect(Collectors.toSet());
            if (!t1Set.isEmpty()) {
                log.nextTimeLine("Записываю T1 (" + t1Set.size() + ")...");
                Level3Dao.saveT1s(t1Set);
            }
            Set<T2> t2Set = new HashSet<>();
            co22Set.forEach(co22 -> t2Set.addAll(co22.getT2()));
            if (!t2Set.isEmpty()) {
                log.nextTimeLine("Записываю T2 (" + t2Set.size() + ")...");
                Level3Dao.saveT2s(t2Set);
            }
            Set<T3> t3Set = new HashSet<>();
            co22Set.forEach(co22 -> t3Set.addAll(co22.getT3()));
            if (!t3Set.isEmpty()) {
                log.nextTimeLine("Записываю T3 (" + t3Set.size() + ")...");
                Level3Dao.saveT3s(t3Set);
            }
            Set<T4> t4Set = new HashSet<>();
            co22Set.forEach(co22 -> t4Set.addAll(co22.getT4()));
            if (!t4Set.isEmpty()) {
                log.nextTimeLine("Записываю T4 (" + t4Set.size() + ")...");
                Level3Dao.saveT4s(t4Set);
            }
            Set<T6> t6Set = new HashSet<>();
            co22Set.forEach(co22 -> t6Set.addAll(co22.getT6()));
            if (!t6Set.isEmpty()) {
                log.nextTimeLine("Записываю T6 (" + t6Set.size() + ")...");
                Level3Dao.saveT6s(t6Set);
            }
            Set<CO22Meta> co22MetaSet = new HashSet<>();
            co22Set.forEach(co22 -> co22MetaSet.addAll(co22.getMetas()));
            if (!co22MetaSet.isEmpty()) {
                log.nextTimeLine("Записываю метаданные ЦО-22 (" + co22MetaSet.size() + ")...");
                Level3Dao.saveCO22Metas(co22MetaSet);
            }
            if (!lgotSet.isEmpty()) {
                log.nextTimeLine("Записываю льготников (" + lgotSet.size() + ")...");
                Level3Dao.saveLgots(lgotSet);
            }
        }) + "c");
    }

}