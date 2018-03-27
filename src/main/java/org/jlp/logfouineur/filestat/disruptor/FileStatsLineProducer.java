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
package org.jlp.logfouineur.filestat.disruptor;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
// TODO: Auto-generated Javadoc

/**
 * The Class FileStatsLineProducer.
 */
public class FileStatsLineProducer {
	
	  /** The ring buffer. */
	private final RingBuffer<FileStatEvent> ringBuffer;

    /**
	 * Instantiates a new agg log record event producer.
	 *
	 * @param ringBuffer the ring buffer
	 */
	public FileStatsLineProducer(RingBuffer<FileStatEvent> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
    }
	
	/** The Constant TRANSLATOR_ONE_ARG. */
	private static final EventTranslatorOneArg<FileStatEvent,FileStatEvent> TRANSLATOR_ONE_ARG = new EventTranslatorOneArg<FileStatEvent,FileStatEvent>() {
	       

		@Override
		public void translateTo(FileStatEvent event, long sequence, FileStatEvent eventModel) {
			// TODO Auto-generated method stub
			event.set(eventModel);
		}
    };
    
    /**
     * On data.
     *
     * @param alr the alr
     */
    public final void onData(FileStatEvent alr)
    {
    	 
    	
    	 ringBuffer.publishEvent(TRANSLATOR_ONE_ARG, alr);
    	 
    }
    
}
