#!/bin/bash
date=`date '+%Y-%m-%d-%H-%M-%S'` 
export root=/opt/workspace47/logfouineur
/opt/jdk-9.0.4/bin/java -Xms256M -Xmx256M -Droot=${root} -Dworkspace=/opt/workspaceLP -Xlog:gc*:file=/opt/workspace47/logfouineur/logs/gc${date}.log --module-path ${root}/libs:${root}/libExt:${root}/myPlugins -m org.jlp.logfouineur/org.jlp.logfouineur.ui.LogFouineurMain
