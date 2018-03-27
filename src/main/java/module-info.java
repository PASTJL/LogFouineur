/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
open module org.jlp.logfouineur {
	//exports org.jlp.logfouineur;
	exports org.jlp.logfouineur.disruptor;
	exports org.jlp.logfouineur.ui;
	exports org.jlp.logfouineur.ui.controller;
	//exports plugins;
	//exports org.jlp.logfouineur.tests;
	exports org.jlp.logfouineur.util;
	exports org.jlp.logfouineur.csvviewer.csvutils;
	exports org.jlp.logfouineur.models;
	exports org.jlp.logfouineur.records;

	requires  com.lmax.disruptor;
	requires java.desktop;
	requires transitive javafx.base;
	requires transitive javafx.controls;
	
	requires transitive javafx.graphics;
	requires transitive org.jlp.javafx;
}
	