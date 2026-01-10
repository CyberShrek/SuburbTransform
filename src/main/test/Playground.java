import org.vniizht.suburbtransform.service.dao.Level2Dao;

import java.sql.Date;

public class Playground {

    public static void main(String[] args) throws Exception {

        Date requestDate = new Date(0);
        String ddMMyyyy = "01012025";
        requestDate.setDate(Integer.parseInt(ddMMyyyy.substring(0, 2)));
        requestDate.setMonth(Integer.parseInt(ddMMyyyy.substring(2, 4)) - 1);
        requestDate.setYear(Integer.parseInt(ddMMyyyy.substring(4, 8)) - 1900);
        System.out.println(Level2Dao.loadPrigRecord(requestDate).next());
    }
}