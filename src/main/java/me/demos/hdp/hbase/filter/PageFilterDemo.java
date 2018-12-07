package me.demos.hdp.hbase.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import me.demos.hdp.common.Context;

public final class PageFilterDemo {
    private static final Log LOG = LogFactory.getLog(PageFilterDemo.class);

    private PageFilterDemo() {
        // Do nothing.
    }

    protected static void exec(String tableName, String familyName, String qualifierName, String qualfierValue, long pageSize, String resultQualifierName) {
        LOG.info("Enter EXEC method, table name is [" + tableName + "], family name is [" + familyName + "].");
        LOG.info("Qualifier name is [" + qualifierName + "], value is [" + qualfierValue + "].");
        ResultScanner scanner = null;
        try (Connection connection = ConnectionFactory.createConnection(Context.getConf()); Table table = connection.getTable(TableName.valueOf(tableName))) {
            byte[] familyBytes = familyName.getBytes();
            Scan scan = new Scan();
            SingleColumnValueFilter scvFilter = new SingleColumnValueFilter(familyBytes, qualifierName.getBytes(), CompareOp.EQUAL, qualfierValue.getBytes());
            scvFilter.setFilterIfMissing(true); // NEED
            scan.setFilter(scvFilter);
            scan.setFilter(new PageFilter(pageSize));
            scanner = table.getScanner(scan);
            AtomicInteger size = new AtomicInteger(0);
            scanner.spliterator().forEachRemaining(result -> LOG.info("Have got [" + size.incrementAndGet() + "] result(s), value of [" + resultQualifierName
                    + "] is [" + new String(CellUtil.cloneValue(result.getColumnLatestCell(familyBytes, resultQualifierName.getBytes()))) + "]."));
            LOG.info("Results' number is [" + size.get() + "].");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (null != scanner) {
                scanner.close();
            }
        }
    }

    public static void main(String[] args) {
        exec(args[0], args[1], args[2], args[3], Long.parseLong(args[4]), args[5]);
    }

}
