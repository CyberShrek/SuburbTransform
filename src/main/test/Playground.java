

import org.vniizht.suburbtransform.model.TransformationOptions;
import org.vniizht.suburbtransform.service.Transformation;

import java.sql.Date;


public class Playground {

    public static void main(String[] args) throws Exception {

        Date requestDate = new Date(0);
        String ddMMyyyy = "10012025";
        requestDate.setDate(Integer.parseInt(ddMMyyyy.substring(0, 2)));
        requestDate.setMonth(Integer.parseInt(ddMMyyyy.substring(2, 4)) - 1);
        requestDate.setYear(Integer.parseInt(ddMMyyyy.substring(4, 8)) - 1900);
//        Level2Dao.PrigCursor record = Level2Dao.loadPrigRecord(requestDate);


        Transformation.run(new TransformationOptions(requestDate, true, false));

//        Transformation.runSpec();

//        Level2Dao.PassCursor passCursor = Level2Dao.loadPass(requestDate);
//        while (passCursor.hasNext()) {
//            passCursor.next();

//            System.out.println(record.getMain().idnum + " f_tick: " + record.getMain().f_tick.toString());
//        }
    }
}