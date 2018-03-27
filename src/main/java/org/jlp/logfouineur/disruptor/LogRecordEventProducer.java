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

package org.jlp.logfouineur.disruptor;
import java.util.Date;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorTwoArg;
import com.lmax.disruptor.RingBuffer;

// TODO: Auto-generated Javadoc
/**
 * The Class LogRecordEventProducer.
 */
public class LogRecordEventProducer {
	

	
	    /** The ring buffer. */
    	private final RingBuffer<LogRecordEvent> ringBuffer;

	    /**
    	 * Instantiates a new log record event producer.
    	 *
    	 * @param ringBuffer the ring buffer
    	 */
    	public LogRecordEventProducer(RingBuffer<LogRecordEvent> ringBuffer)
	    {
	        this.ringBuffer = ringBuffer;
	    }
	    
    	/** The Constant TRANSLATOR_TWO_ARG. */
    	private static final EventTranslatorTwoArg<LogRecordEvent, String, Date> TRANSLATOR_TWO_ARG = new EventTranslatorTwoArg<LogRecordEvent, String,Date>() {
	       
    		//System.out.println("EventTranslatorTwoArg try to translate "+event.getContent());
			@Override
			public void translateTo(LogRecordEvent event, long sequence, String content, Date date) {
				
				// TODO Auto-generated method stub
				event.set(content, date);
			}
	    };
	    
    	/**
    	 * On data.
    	 *
    	 * @param lre the lre
    	 */
    	// Got directly a LogRecordEvent from a Stream => no translator needed
	    public void onData(LogRecordEvent lre)
	    {
	    	
	     ringBuffer.publishEvent(TRANSLATOR_TWO_ARG, lre.getContent(),lre.getDate());
//	        long sequence = ringBuffer.next();  // Grab the next sequence
//	        try
//	        {
//	            LogRecordEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
//	                                                        // for the sequence
//	            event.set(lre.getContent(),lre.getDate());  // Fill with data
//	        }
//	        finally
//	        {
//	            ringBuffer.publish(sequence);
//	            
//	        }
	    }
	

}
