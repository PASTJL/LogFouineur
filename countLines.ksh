 find /opt/workspace47/logfouineur/src -type f -name *.java -exec wc -l {} \; | cut -f 1 -d ' ' | awk ' BEGIN { cumul=0; } { cumul=cumul+$0; } END { print "nbLOC_logfouineur=",cumul;}'
