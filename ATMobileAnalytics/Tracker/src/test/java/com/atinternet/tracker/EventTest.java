/*
This SDK is licensed under the MIT license (MIT)
Copyright (c) 2015- Applied Technologies Internet SAS (registration number B 403 261 258 - Trade and Companies Register of Bordeaux – France)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.atinternet.tracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@Config(sdk = 21)
@RunWith(RobolectricTestRunner.class)
public class EventTest extends AbstractTestClass {

    private Event event;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        event = new Event(tracker);
    }

    @Test
    public void setOneTest() {
        tracker = event.set("cat", "action", "page");

        assertEquals(4, buffer.getVolatileParams().size());
        assertEquals(0, buffer.getPersistentParams().size());

        assertEquals(1, buffer.getVolatileParams().get("type").getValues().size());
        assertEquals("cat", buffer.getVolatileParams().get("type").getValues().get(0).execute());

        assertEquals(1, buffer.getVolatileParams().get("action").getValues().size());
        assertEquals("action", buffer.getVolatileParams().get("action").getValues().get(0).execute());

        assertEquals(1, buffer.getVolatileParams().get("p").getValues().size());
        assertEquals("page", buffer.getVolatileParams().get("p").getValues().get(0).execute());

        assertEquals(1, buffer.getVolatileParams().get("stc").getValues().size());
        assertEquals("{}", buffer.getVolatileParams().get("stc").getValues().get(0).execute());
    }

    @Test
    public void setTwoTest() {
        event.set("cat", "action", "page", "obj");
        assertEquals(4, buffer.getVolatileParams().size());
        assertEquals(0, buffer.getPersistentParams().size());

        assertEquals(1, buffer.getVolatileParams().get("type").getValues().size());
        assertEquals("cat", buffer.getVolatileParams().get("type").getValues().get(0).execute());

        assertEquals(1, buffer.getVolatileParams().get("action").getValues().size());
        assertEquals("action", buffer.getVolatileParams().get("action").getValues().get(0).execute());

        assertEquals(1, buffer.getVolatileParams().get("p").getValues().size());
        assertEquals("page", buffer.getVolatileParams().get("p").getValues().get(0).execute());

        assertEquals(1, buffer.getVolatileParams().get("stc").getValues().size());
        assertEquals("obj", buffer.getVolatileParams().get("stc").getValues().get(0).execute());
    }
}
