package xparser;

public interface DAO {

        public static final String DB_URL =
                "jdbc:oracle:thin:@"+" (DESCRIPTION =\n" +
                        "    (ADDRESS_LIST =\n" +
                        "      (ADDRESS = (PROTOCOL = TCP)(Host =  myhost)(Port = 1521))\n" +
                        "    )\n" +
                        "    (CONNECT_DATA =\n" +
                        "      (SERVICE_NAME = sname)\n" +
                        "    )\n" +
                        "  )";
        public static final String DRIVER ="oracle.jdbc.driver.OracleDriver";
        public static final String USER = "user";
        public static final String PASS = "userpw";


}
