<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
	<title></title>
	<meta name="generator" content="LibreOffice 5.4.5.1 (Linux)"/>
	<meta name="created" content="2018-03-27T09:10:02.509863952"/>
	<meta name="changed" content="2018-03-27T09:27:46.408115055"/>
</head>
<body lang="en-US" dir="ltr">
<ol>
	<li/>
<p style="margin-top: 0.42cm; page-break-after: avoid"><font face="DejaVu Math TeX Gyre"><font size="4" style="font-size: 14pt"><b>LogFouineur</b></font></font></p>
</ol>
<p style="font-weight: normal"><font face="DejaVu Math TeX Gyre">This
application parse different types of logs that are explicitly or
implicitly dated.</font></p>
<p style="font-weight: normal"><font face="DejaVu Math TeX Gyre">It
runs in two phases :</font></p>
<p style="font-weight: normal"><font face="DejaVu Math TeX Gyre">-
First parse logs and generate a CSV files. Each line is dated with a
recognized java format date or timestamp in milliseconds.</font></p>
<p><font face="DejaVu Math TeX Gyre"><span lang="en-US"><span style="font-weight: normal">-
A csv viewer that can graph the series.</span></span></font></p>
<ol start="2">
	<li/>
<p style="margin-top: 0.42cm; page-break-after: avoid"><font face="DejaVu Math TeX Gyre"><font size="4" style="font-size: 14pt"><b>Requirements</b></font></font></p>
</ol>
<p>Oracle Java SE 9+/Java FX 9+. Not tested with Open JDK</p>
<p>License <b>LogFouineur </b>: Apache 2</p>
<ol start="3">
	<li/>
<p style="margin-top: 0.42cm; page-break-after: avoid"><font face="DejaVu Math TeX Gyre"><font size="4" style="font-size: 14pt"><b>Launch</b></font></font></p>
</ol>
<p style="font-weight: normal"><font face="DejaVu Math TeX Gyre">The
scripts are under folder scripts:</font></p>
<p style="font-weight: normal"><font face="DejaVu Math TeX Gyre">For
a Linux desktop :</font></p>
<p style="margin-bottom: 0cm; font-weight: normal; background: #729fcf">
<font face="DejaVu Math TeX Gyre"><font face="Monospace"><font size="2" style="font-size: 10pt">#!/bin/bash</font></font></font></p>
<p align="left" style="margin-bottom: 0cm; background: #729fcf"><font face="Monospace"><font size="2" style="font-size: 10pt">export
root=/opt/workspace47/</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>logfouineur</u></font></font></font></p>
<p align="left" style="margin-bottom: 0cm; background: #729fcf"><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>export
workspace=/opt/workspaceLP</u></font></font></font></p>
<p align="left" style="margin-bottom: 0cm; background: #729fcf"><font face="Monospace"><font size="2" style="font-size: 10pt">/opt/</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>jdk</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">-9.0.4/bin/java
-Xms1024M -Xmx1024M -</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Droot</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=${root}
-</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Dworkspace</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=</font></font><font face="Monospace"><font size="2" style="font-size: 10pt">${workspace}</font></font><font face="Monospace"><font size="2" style="font-size: 10pt">
 --module-path ${root}/</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>libs</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">:${root}/libExt
-m org.jlp.logfouineur/org.jlp.logfouineur.ui.LogFouineurMain</font></font></p>
<p style="font-weight: normal; background: #729fcf"><br/>
<br/>

</p>
<p style="font-weight: normal"><font face="DejaVu Math TeX Gyre">For
a Windows desktop :</font></p>
<p style="margin-bottom: 0cm; font-weight: normal; background: #729fcf">
<font face="DejaVu Math TeX Gyre"><font face="Monospace"><font size="2" style="font-size: 10pt">set
JAVA_HOME=C:\Program Files\Java\</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>jdk</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">-9.0.4</font></font></font></p>
<p align="left" style="margin-bottom: 0cm; background: #729fcf"><font face="Monospace"><font size="2" style="font-size: 10pt">set
root=C:\opt\workspace47\</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>logfouineur</u></font></font></font></p>
<p align="left" style="margin-bottom: 0cm; background: #729fcf"><font face="Monospace"><font size="2" style="font-size: 10pt">set
workspace=C:\opt\workspaceLP</font></font></p>
<p align="left" style="margin-bottom: 0cm; background: #729fcf"><font face="Monospace"><font size="2" style="font-size: 10pt">&quot;%JAVA_HOME%\bin\java&quot;
-Xms1024M -Xmx1024M -</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Droot</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=%root%
-</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>Dworkspace</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">=%workspace%
 --module-path %root%\</font></font><font color="#31363b"><font face="Monospace"><font size="2" style="font-size: 10pt"><u>libs</u></font></font></font><font face="Monospace"><font size="2" style="font-size: 10pt">;%root%\libExt;%root%\myPlugins
-m org.jlp.logfouineur/org.jlp.logfouineur.ui.LogFouineurMain</font></font></p>
<p style="margin-bottom: 0cm; font-weight: normal; background: #729fcf">
<br/>

</p>
<ol start="4">
	<li/>
<p style="margin-top: 0.42cm; page-break-after: avoid"><font face="DejaVu Math TeX Gyre"><font size="4" style="font-size: 14pt"><b>Synoptics</b></font></font></p>
</ol>
<p><img src="manual/synoptic.png" name="Image1" align="left" width="786" height="830" border="0"/>
<br/>
<br/>

</p>
</body>
</html>