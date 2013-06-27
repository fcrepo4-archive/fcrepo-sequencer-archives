/**
 * Copyright 2013 DuraSpace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fcrepo.sequencer.archive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ArchiveSequencerTest extends junit.framework.TestCase {

    FileInputStream zipin = null;

    String zipContents = null;

    @Before
    public void setUp() throws FileNotFoundException {
        zipin = new FileInputStream("target/test-classes/test-data/tmp.zip");
        zipContents = "tmp.txt\n";
    }

    @Test
    public void testListContents() {
        try {
            String contents = ArchiveSequencer.listContents(zipin);
            assertEquals(zipContents, contents);
            zipin.close();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
}
