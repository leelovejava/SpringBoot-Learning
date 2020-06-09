package clickhouse;

import java.io.IOException;
import java.io.InputStream;

public class MyCustomInputStream extends InputStream {
    @Override
    public int read() throws IOException {
        return 0;
    }
}
