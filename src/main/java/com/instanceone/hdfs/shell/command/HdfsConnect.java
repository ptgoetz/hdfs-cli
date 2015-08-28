// Copyright (c) 2012 P. Taylor Goetz (ptgoetz@gmail.com)

package com.instanceone.hdfs.shell.command;

import java.io.IOException;
import java.net.URI;

import com.instanceone.hdfs.shell.completers.FileSystemNameCompleter;
import com.instanceone.stemshell.command.AbstractCommand;
import jline.console.ConsoleReader;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;

import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.instanceone.stemshell.Environment;

public class HdfsConnect extends AbstractCommand {

    public HdfsConnect(String name) {
        super(name);
        Completer completer = new StringsCompleter("hdfs://localhost:8020/", "hdfs://hdfshost:8020/");
        this.completer = completer;
    }

    public void execute(Environment env, CommandLine cmd, ConsoleReader reader) {
        try {
            if(cmd.getArgs().length > 0){
                Configuration config = new Configuration();
                FileSystem hdfs = FileSystem.get(URI.create(cmd.getArgs()[0]),
                                config);
                env.setValue(HdfsCommand.CFG,config);
                env.setValue(HdfsCommand.HDFS, hdfs);
                // set working dir to root
                hdfs.setWorkingDirectory(hdfs.makeQualified(new Path("/")));
                
                FileSystem local = FileSystem.getLocal(new Configuration());
                env.setValue(HdfsCommand.LOCAL_FS, local);
                env.setProperty(HdfsCommand.HDFS_URL, hdfs.getUri().toString());

                FSUtil.prompt(env);

                log(cmd, "Connected: " + hdfs.getUri());
                logv(cmd, "HDFS CWD: " + hdfs.getWorkingDirectory());
                logv(cmd, "Local CWD: " + local.getWorkingDirectory());

            }     
        }
        catch (IOException e) {
            log(cmd, e.getMessage());
        }
    }

    @Override
    public Completer getCompleter() {
        return this.completer;
    }


}
