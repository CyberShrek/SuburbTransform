package org.vniizht.suburbtransform.service.dao;

import lombok.SneakyThrows;
import org.vniizht.suburbtransform.database.SimpleJdbc;
import org.vniizht.suburbtransform.model.level3.*;
import org.vniizht.suburbtransform.util.Log;
import org.vniizht.suburbtransform.util.Util;

import java.sql.ResultSet;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level3Dao {

    private static final int    BATCH_SIZE      = 1000;

    public static void commit() {
        SimpleJdbc.commit();
    }

    @SneakyThrows
    public static Date getNextRequestDateOrNull(){
        ResultSet rs = SimpleJdbc.query("level3/getNextRequestDate");
        rs.first();
        Date requestDate = rs.getDate("request_date");
        return requestDate == null ? null : new Date(requestDate.getTime() + 24*60*60*1000); // + 1 day
    }

    public static Boolean prigWasTransformedForDate(Date date) {
        return wasTransformedForDate("level3/getPrigCount", date);
    }

    public static Boolean passWasTransformedForDate(Date date) {
        return wasTransformedForDate("level3/getPassCount", date);
    }

    @SneakyThrows
    private static Boolean wasTransformedForDate(String queryId, Date date) {
        List<Map<String, Object>> matrix = SimpleJdbc.queryForMatrix(queryId, new HashMap(){{
            put("requestDate", date);
        }});
        return !((Long) matrix.get(0).get("count") == 0);
    }

    @SneakyThrows
    public static Long getLatestT1P2() {
        Long largest = (Long) SimpleJdbc.queryForMatrix("level3/getLargestT1P2").get(0).get("p2");
        return largest;
    }

    public static void deletePrigForDate(Date date) {
        updateForRequestDate("level3/deletePrig", date);
    }
    public static void deletePassForDate(Date date){
        updateForRequestDate("level3/deletePass", date);
    }
    public static void deleteLgotForDate(Date date){
        updateForRequestDate("level3/deleteLgot", date);
    }

    @SneakyThrows
    private static void updateForRequestDate(String queryId, Date date) {
        SimpleJdbc.update(queryId, new HashMap(){{
            put("requestDate", date);
        }});
    }

    public static void saveT1s(List<T1> t1List, Log log){
        saveList("level3/insertT1", t1List, log);
    }
    public static void saveT2s(List<T2> t2List, Log log){
        saveList("level3/insertT2", t2List, log);
    }
    public static void saveT3s(List<T3> t3List, Log log){
        saveList("level3/insertT3", t3List, log);
    }
    public static void saveT4s(List<T4> t4List, Log log){
        saveList("level3/insertT4", t4List, log);
    }
    public static void saveT6s(List<T6> t6List, Log log){
        saveList("level3/insertT6", t6List, log);
    }
    public static void saveMetas(List<CO22Meta> co22MetaList, Log log){
        saveList("level3/insertMeta", co22MetaList, log);
    }
    public static void saveLgots(List<Lgot> lgotList, Log log){
        saveList("level3/insertLgot", lgotList, log);
    }

    private static void saveList(String queryId, List<?> list, Log log){
        SimpleJdbc.batchUpdate(queryId, list, Util::objectToMap, BATCH_SIZE, log);
    }
}
