package clickhouse;

import org.junit.Test;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseConnectionImpl;
import ru.yandex.clickhouse.ClickHouseStatement;
import ru.yandex.clickhouse.domain.ClickHouseFormat;
import ru.yandex.clickhouse.settings.ClickHouseQueryParam;

import java.io.File;
import java.sql.SQLException;

public class ClickHouseTest {
    ClickHouseConnection connection = new ClickHouseConnectionImpl("jdbc:clickhouse://localhost:8123/test");

    /**
     * Importing file into table
     *
     * @throws SQLException
     */
    @Test
    public void test01() throws SQLException {
        ClickHouseStatement sth = connection.createStatement();
        sth.write() // Write API entrypoint
                .table("default.my_table") // where to write data
                .option("format_csv_delimiter", ";") // specific param
                .data(new File("/path/to/file.csv"), ClickHouseFormat.CSV) // specify input
                .send();
    }

    /**
     * Configurable send
     *
     * @throws SQLException
     */
    @Test
    public void test02() throws SQLException {
        ClickHouseStatement sth = connection.createStatement();
        sth.write()
                .sql("INSERT INTO default.my_table (a,b,c)")
                .data(new MyCustomInputStream(), ClickHouseFormat.JSONEachRow)
                .addDbParam(ClickHouseQueryParam.MAX_PARALLEL_REPLICAS, "2")
                .send();
    }

    /**
     * Send data in binary formatd with custom user callback
     *
     * @throws SQLException
     */
    @Test
    public void test03() throws SQLException {
        ClickHouseStatement sth = connection.createStatement();
        sth.write().send("INSERT INTO test.writer", stream -> {
            for (int i = 0; i < 10; i++) {
                stream.writeInt32(i);
                stream.writeString("Name " + i);
            }
        }, ClickHouseFormat.RowBinary); // RowBinary or Native are supported
    }
}
