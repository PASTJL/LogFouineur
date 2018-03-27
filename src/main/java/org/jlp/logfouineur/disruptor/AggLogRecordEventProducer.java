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

import org.jlp.logfouineur.models.AggLogRecordEvent;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorThreeArg;
import com.lmax.disruptor.RingBuffer;

// TODO: Auto-generated Javadoc
/**
 * The Class AggLogRecordEventProducer.
 */
public class AggLogRecordEventProducer {
	
			
	
	    /** The ring buffer. */
    	private final RingBuffer<AggLogRecordEvent> ringBuffer;

	    /**
    	 * Instantiates a new agg log record event producer.
    	 *
    	 * @param ringBuffer the ring buffer
    	 */
    	public AggLogRecordEventProducer(RingBuffer<AggLogRecordEvent> ringBuffer)
	    {
	        this.ringBuffer = ringBuffer;
	    }
	    
    	/** The Constant TRANSLATOR_THREE_ARG. */
    	private static final EventTranslatorThreeArg<AggLogRecordEvent,String, Long, Integer> TRANSLATOR_THREE_ARG = new EventTranslatorThreeArg<AggLogRecordEvent,String, Long, Integer>() {
	       

			@Override
			public  final void translateTo(AggLogRecordEvent event, long sequence, String name, Long period, Integer nbVals) {
				// TODO Auto-generated method stub
				event.set(name,period.longValue(), nbVals.intValue());
			}
	    };
	    
    	/** The Constant TRANSLATOR_ONE_ARG. */
    	private static final EventTranslatorOneArg<AggLogRecordEvent,AggLogRecordEvent> TRANSLATOR_ONE_ARG = new EventTranslatorOneArg<AggLogRecordEvent,AggLogRecordEvent>() {
		       

			@Override
			public void translateTo(AggLogRecordEvent event, long sequence, AggLogRecordEvent eventModel) {
				// TODO Auto-generated method stub
				event.set(eventModel);
			}
	    };
	    
	    
	    /**
    	 * On data.
    	 *
    	 * @param alr the alr
    	 */
    	// Got directly a LogRecordEvent from a Stream => no translator needed
	    public final void onData(AggLogRecordEvent alr)
	    {
	    	
	    	 // ringBuffer.publishEvent(TRANSLATOR_THREE_ARG, alr.namePivot,alr.period,alr.nbVals);
	    	 ringBuffer.publishEvent(TRANSLATOR_ONE_ARG, alr);
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
