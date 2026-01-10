package org.vniizht.suburbtransform.service.dao;

import lombok.*;
import org.vniizht.suburbtransform.database.SimpleJdbc;
import org.vniizht.suburbtransform.model.level2.PrigCost;
import org.vniizht.suburbtransform.model.level2.PrigMain;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;


public class Level2Dao { private Level2Dao() {}

    public static PrigRecord loadPrigRecord(Date requestDate) {
        return new PrigRecord(requestDate);
    }

//    public static Set<Record> findPassRecords(Date requestDate) {
//        Map<Long, PassRecord> collector = new LinkedHashMap<>();
//        List<PassMain> mainList = passMainRepo.findAllByRequestDateAndIdnumIn(requestDate, ids);
//        mainList.forEach(main -> collector.put(main.idnum, new PassRecord(main)));
//        return new LinkedHashSet<>(collector.values());
//    }


    @ToString
    static public class PrigRecord extends Record<PrigRecord> {

        @Getter @Setter private PrigMain main;
        @Getter @Setter private List<PrigCost> cost = new ArrayList<>();

        private ResultSet costRS;

        PrigRecord(Date requestDate) {
            super("level2/findPrigMain", requestDate);
        }

        @Override
        public PrigRecord next() {
            return this;
        }
    }

//    @Getter
//    @Setter
//    @ToString
//    static public class PassRecord extends Record {
//        private PassMain main;
//
//        PassRecord(PassMain main) {
//            super(main.idnum);
//            this.main = main;
//        }
//    }

    static public abstract class Record<T extends Record<T>> implements Iterator<T> {

        protected final ResultSet mainRS;
        protected final List<Long> idnums = new ArrayList<>();

        @SneakyThrows
        Record(String queryId, Date requestDate) {
            this.mainRS = SimpleJdbc.query(queryId, new HashMap(){{
                put("requestDate", requestDate);
            }});
            mainRS.beforeFirst();
            while (mainRS.next())
                idnums.add(mainRS.getLong("idnum"));
        }

        @Override
        @SneakyThrows
        public boolean hasNext() {
            boolean hasNext = mainRS.next();
            if (hasNext) mainRS.previous();
            return hasNext;
        }

//        @Override
//        public T next() {
//            return this;
//        }
    }


    private static List<Long> findIdnumsByRequestDate(String queryId, Date requestDate) throws Exception {
        return (List<Long>) SimpleJdbc.queryForMatrix(queryId, new HashMap(){{
            put("requestDate", requestDate);
        }}).stream().map( entry -> ((Map<String, Object>) entry).get("idnum")).collect(Collectors.toList());
    }
}
