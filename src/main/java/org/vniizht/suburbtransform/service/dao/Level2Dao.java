package org.vniizht.suburbtransform.service.dao;

import lombok.*;
import org.vniizht.suburbtransform.database.SimpleJdbc;
import org.vniizht.suburbtransform.model.level2.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;

public class Level2Dao { private Level2Dao() {}

    public static List<Long> findPrigIdnums(Date requestDate) {
        return findIdnums("level2/findPrigIdnums", requestDate);
    }

    public static List<Long> findPrigSpecIdnums() {
        return findIdnums("level2/findPrigSpecIdnums", null);
    }

    public static List<Long> findPassIdnums(Date requestDate) {
        return findIdnums("level2/findPassIdnums", requestDate);
    }

    public static PrigCursor loadPrig(List<Long> idnums) {
        return new PrigCursor(idnums);
    }

    public static PassCursor loadPass(List<Long> idnums) {
        return new PassCursor(idnums);
    }


    @ToString(callSuper = true)
    static public class PrigCursor extends Cursor<PrigCursor> {

        @Getter @Setter private PrigMain       main;
        @Getter @Setter private PrigAdi         adi;
        @Getter @Setter private List<PrigCost> cost;

        @SneakyThrows
        PrigCursor(List<Long> idnums) {
            super("level2/findPrigMain", idnums);
            loadChildRS("adi", "level2/findPrigAdi");
            loadChildRS("cost", "level2/findPrigCost");
        }

        @SneakyThrows
        @Override
        public PrigCursor next() {
            super.next();
            main = collectMain(PrigMain.class);
            adi  = collectOne("adi", PrigAdi.class);
            cost = collectList("cost", PrigCost.class);
            return this;
        }
    }

    @ToString(callSuper = true)
    static public class PassCursor extends Cursor<PassCursor> {

        @Getter @Setter private PassMain       main;
        @Getter @Setter private PassMainUpd     upd;
        @Getter @Setter private List<PassEx>     ex;
        @Getter @Setter private List<PassLgot> lgot;
        @Getter @Setter private PassRefund   refund;
        @Getter @Setter private List<PassCost> cost;

        @SneakyThrows
        PassCursor(List<Long> idnums) {
            super("level2/findPassMain", idnums);
            loadChildRS("upd", "level2/findPassMainUpd");
            loadChildRS("ex", "level2/findPassEx");
            loadChildRS("lgot", "level2/findPassLgot");
            loadChildRS("refund", "level2/findPassRefund");
            loadChildRS("cost", "level2/findPassCost");
        }

        @SneakyThrows
        @Override
        public PassCursor next() {
            super.next();
            main    = collectMain(PassMain.class);
            upd     = collectOne("upd", PassMainUpd.class);
            ex      = collectList("ex", PassEx.class);
            lgot    = collectList("lgot", PassLgot.class);
            refund  = collectOne("refund", PassRefund.class);
            cost    = collectList("cost", PassCost.class);
            return this;
        }
    }

    @ToString
    static public abstract class Cursor<T extends Cursor<T>> implements Iterator<T> {

        private final ResultSet mainRS;
        private final Map<String, ResultSet> childrenRS = new HashMap<>();
        private final List<Long> idnums;
        private Long currentIdnum;

        @SneakyThrows
        Cursor(String queryId, List<Long> idnums) {
            this.idnums = idnums;
            mainRS = SimpleJdbc.query(queryId, new HashMap(){{
                put("idnums", idnums);
            }});
        }

        @Override
        @SneakyThrows
        public boolean hasNext() {
            boolean hasNext = mainRS.next();
            if (hasNext) mainRS.previous();
            return hasNext;
        }

        @Override
        @SneakyThrows
        public T next() {
            mainRS.next();
            currentIdnum = mainRS.getLong("idnum");
            return (T) this;
        }

        public int size() {
            return idnums.size();
        }

        @SneakyThrows
        public void close() {
            mainRS.close();
            for (ResultSet rs : childrenRS.values())
                rs.close();
        }

        @SneakyThrows
        protected void loadChildRS(String key, String queryId) {
            childrenRS.put(key, SimpleJdbc.query(queryId, new HashMap(){{
                put("idnums", idnums);
            }}));
        }

        @SneakyThrows
        protected <V> V collectMain(Class<V> clasS) {
            return SimpleJdbc.rsToObject(mainRS, clasS);
        }

        protected <V> V collectOne(String rsKey, Class<V> clasS) {
            List<V> list = collectList(rsKey, clasS);
            return list.isEmpty() ? null : list.get(0);
        }

        @SneakyThrows
        protected <V> List<V> collectList(String rsKey, Class<V> clasS) {
            ResultSet rs = childrenRS.get(rsKey);
            List<V> result = new ArrayList<>();
            while (rs.next()) {
                Long idnum = rs.getLong("idnum");
                if (idnum.equals(currentIdnum))
                    result.add(SimpleJdbc.rsToObject(rs, clasS));
                else if (idnum > currentIdnum) {
                    rs.previous();
                    break;
                }
            }
            return result;
        }
    }


    @SneakyThrows
    private static List<Long> findIdnums(String queryId, Date requestDate) {
        ResultSet rs = SimpleJdbc.query(queryId, new HashMap(){{
            put("requestDate", requestDate);
        }});
        List<Long> result = new ArrayList<>();
        while (rs.next()) result.add(rs.getLong("idnum"));
        return result;
    }
}
