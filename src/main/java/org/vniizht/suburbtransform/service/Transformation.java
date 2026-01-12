package org.vniizht.suburbtransform.service;

import org.vniizht.suburbtransform.model.TransformationOptions;
import org.vniizht.suburbtransform.model.level3.*;
import org.vniizht.suburbtransform.service.dao.HandbookDao;
import org.vniizht.suburbtransform.service.dao.Level2Dao;
import org.vniizht.suburbtransform.service.dao.Level3Dao;
import org.vniizht.suburbtransform.util.Log;
import org.vniizht.suburbtransform.util.Util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Transformation { private Transformation() {}

    private static int PORTION_SIZE = 10000;

    private static final Log log = new Log();

    public static synchronized void run(TransformationOptions options) {

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
            Level3Dao.commit();
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
                () -> Level2Dao.findPrigIdnums(requestDate),
                Level2Dao::loadPrig,
                new Level3Prig(Level3Dao.getLatestT1P2() + 1)
        );
    }

    private static Level3Pass transformPassOrNull(Date requestDate) {
        return (Level3Pass) transformOrNull(
                "l2_prig",
                () -> Level2Dao.findPassIdnums(requestDate),
                Level2Dao::loadPass,
                new Level3Pass(Level3Dao.getLatestT1P2() + 1)
        );
    }

    private static Level3 transformOrNull(String name,
                                          Supplier<List<Long>> idnumsLoader,
                                          Function<List<Long>, Level2Dao.Cursor> cursorLoader,
                                          Level3 level3) {
        log.nextTimeLine("Ищу записи " + name + "...");
        List<Long> idnums = idnumsLoader.get();
        log.nextTimeLine("Найдено записей main: " + idnums.size());
        if (idnums.size() == 0) return null;
        List<List<Long>> pagedIdnums = Util.splitList(idnums, PORTION_SIZE);
        for (int i = 0; i < pagedIdnums.size(); i++) {
            List<Long> currentIdnums = pagedIdnums.get(i);
            log.nextTimeLine("Трансформирую порцию " + (i * PORTION_SIZE) + " - " + (i * PORTION_SIZE + currentIdnums.size()));
            level3.runTransformation(cursorLoader.apply(currentIdnums), log);;
        }
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
        List<Level3.CO22> co22List = new ArrayList<>(nullableLevel3.getCo22Result().values());

        log.sumUp("Сформировано записей ЦО-22 Т1:      " + co22List.size(),
                "Сформировано записей Льготников: " + nullableLevel3.getLgotResult().size());

        update(co22List, nullableLevel3.getLgotResult());
    }

    private static void update(List<Level3.CO22> co22List, List<Lgot> lgotList) {
        log.sumUp("\tЗатрачено времени на запись: " + Util.measureTime(() -> {

            List<T1> t1List = co22List.stream().map(Level3.CO22::getT1).collect(Collectors.toList());
            if (!t1List.isEmpty()) {
                log.nextTimeLine("Записываю T1 (" + t1List.size() + ")...");
                Level3Dao.saveT1s(t1List, log);
            }
            List<T2> t2List = new ArrayList<>();
            co22List.forEach(co22 -> t2List.addAll(co22.getT2()));
            if (!t2List.isEmpty()) {
                log.nextTimeLine("Записываю T2 (" + t2List.size() + ")...");
                Level3Dao.saveT2s(t2List, log);
            }
            List<T3> t3List = new ArrayList<>();
            co22List.forEach(co22 -> t3List.addAll(co22.getT3()));
            if (!t3List.isEmpty()) {
                log.nextTimeLine("Записываю T3 (" + t3List.size() + ")...");
                Level3Dao.saveT3s(t3List, log);
            }
            List<T4> t4List = new ArrayList<>();
            co22List.forEach(co22 -> t4List.addAll(co22.getT4()));
            if (!t4List.isEmpty()) {
                log.nextTimeLine("Записываю T4 (" + t4List.size() + ")...");
                Level3Dao.saveT4s(t4List, log);
            }
            List<T6> t6List = new ArrayList<>();
            co22List.forEach(co22 -> t6List.addAll(co22.getT6()));
            if (!t6List.isEmpty()) {
                log.nextTimeLine("Записываю T6 (" + t6List.size() + ")...");
                Level3Dao.saveT6s(t6List, log);
            }
            List<CO22Meta> co22MetaList = new ArrayList<>();
            co22List.forEach(co22 -> co22MetaList.addAll(co22.getMetas()));
            if (!co22MetaList.isEmpty()) {
                log.nextTimeLine("Записываю метаданные ЦО-22 (" + co22MetaList.size() + ")...");
                Level3Dao.saveMetas(co22MetaList, log);
            }
            if (!lgotList.isEmpty()) {
                log.nextTimeLine("Записываю льготников (" + lgotList.size() + ")...");
                Level3Dao.saveLgots(lgotList, log);
            }
        }) + "c");
    }

}