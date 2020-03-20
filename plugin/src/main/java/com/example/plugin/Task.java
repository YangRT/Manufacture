package com.example.plugin;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecSpec;

import javax.inject.Inject;

import groovy.lang.DelegatesTo;
//gradle 插件任务
public class Task extends DefaultTask {

    @Inject
    public Task(){

    }

    @TaskAction
    public void run(){
        getProject().exec(new Action<ExecSpec>() {
            @Override
            public void execute(ExecSpec execSpec) {
                execSpec.commandLine("java");
            }
        });
    }
}
