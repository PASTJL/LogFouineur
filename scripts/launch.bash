#!/bin/bash
export root=/opt/workspace47/logfouineur
/opt/jdk-9.0.4/bin/java -Xms1024M -Xmx1024M -Droot=${root} -Dworkspace=/opt/workspaceLP  --module-path ${root}/libs:${root}/libExt -m org.jlp.logfouineur/org.jlp.logfouineur.ui.LogFouineurMain
