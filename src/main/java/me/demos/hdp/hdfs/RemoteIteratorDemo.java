package me.demos.hdp.hdfs;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

import me.demos.hdp.common.Context;

public final class RemoteIteratorDemo {
    private static final Log LOG = LogFactory.getLog(RemoteIteratorDemo.class);

    private RemoteIteratorDemo() {
        // Do nothing.
    }

    private static void exec(String pathStr) {
        Path path = new Path(pathStr);
        try (FileSystem fs = path.getFileSystem(Context.getConf())) {
            RemoteIterator<LocatedFileStatus> files = fs.listFiles(path, true);
            while (files.hasNext()) {
                LocatedFileStatus file = files.next();
                LOG.info("File path is [" + file.getPath().toString() + "], owner is [" + file.getOwner() + "].");
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) {
        exec(args[0]);
    }

}
