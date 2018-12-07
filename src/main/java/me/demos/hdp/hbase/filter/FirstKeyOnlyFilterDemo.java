package me.demos.hdp.hbase.filter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;

import me.demos.hdp.common.Context;

public final class FirstKeyOnlyFilterDemo {
    private static final Log LOG = LogFactory.getLog(FirstKeyOnlyFilterDemo.class);
    private static final int MILLI = 1000;

    private FirstKeyOnlyFilterDemo() {
        // Do nothing.
    }

    private static void exec(String tableName, String rowKeyPrefix) {
        ResultScanner scanner = null;
        try (Connection connection = ConnectionFactory.createConnection(Context.getConf()); Table table = connection.getTable(TableName.valueOf(tableName))) {
            Scan scan = new Scan();
            scan.setFilter(new FirstKeyOnlyFilter());
            scan.setFilter(new PrefixFilter(rowKeyPrefix.getBytes()));
            scanner = table.getScanner(scan);
            AtomicInteger size = new AtomicInteger(0);
            scanner.spliterator().forEachRemaining(result -> size.incrementAndGet());
            LOG.info("Results' number is [" + size.get() + "].");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (null != scanner) {
                scanner.close();
            }
        }
    }

    private static void without(String tableName, String rowKeyPrefix) {
        ResultScanner scanner = null;
        try (Connection connection = ConnectionFactory.createConnection(Context.getConf()); Table table = connection.getTable(TableName.valueOf(tableName))) {
            Scan scan = new Scan();
            scan.setFilter(new PrefixFilter(rowKeyPrefix.getBytes()));
            scanner = table.getScanner(scan);
            AtomicInteger size = new AtomicInteger(0);
            scanner.spliterator().forEachRemaining(result -> size.incrementAndGet());
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
        String prefix = DigestUtils.md5Hex(args[1]);

        long time0 = System.currentTimeMillis();
        exec(args[0], prefix);
        long time1 = System.currentTimeMillis();
        long duration1 = (time1 - time0) / MILLI;
        LOG.info("Operation with FKO filter cost [" + duration1 + "s].");

        long time2 = System.currentTimeMillis();
        without(args[0], prefix);
        long time3 = System.currentTimeMillis();
        long duration2 = (time3 - time2) / MILLI;
        LOG.info("Operation without FKO filter cost [" + duration2 + "s].");

        LOG.info("Duration distance is [" + (duration2 - duration1) + "s].");
    }

}
