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

import com.lmax.disruptor.EventFactory;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating AggLogRecordEvent objects.
 */
public class AggLogRecordEventFactory implements EventFactory<AggLogRecordEvent>
{
    
    /**
     * New instance.
     *
     * @return the agg log record event
     */
    /* (non-Javadoc)
     * @see com.lmax.disruptor.EventFactory#newInstance()
     */
    public AggLogRecordEvent newInstance()
    {
        return new AggLogRecordEvent();
    }
}
