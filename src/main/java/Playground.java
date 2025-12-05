import org.vniizht.suburbtransform.database.SimpleJdbc;

import java.io.IOException;
import java.sql.SQLException;

public class Playground {

    public static void main(String[] args) throws SQLException, IOException {
        System.out.println(SimpleJdbc.queryForList("demo"));
    }
}