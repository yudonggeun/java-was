package codesquad.config;

import codesquad.util.scan.Solo;

@Solo
public class DBConfig {

    public final String url = "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1";
    public final String username = "sa";
    public final String password = "";
}
