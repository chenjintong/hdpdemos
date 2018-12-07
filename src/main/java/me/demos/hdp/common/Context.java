package me.demos.hdp.common;

import org.apache.hadoop.conf.Configuration;

public final class Context {
    private static final Configuration CONF = new Configuration();

    static {
        CONF.addResource("core-site.xml");
        CONF.addResource("hdfs-site.xml");
        CONF.addResource("hbase-site.xml");
        CONF.addResource("dfs-site.xml");
    }

    private Context() {
        // Do nothing.
    }

    public static Configuration getConf() {
        return CONF;
    }

}
