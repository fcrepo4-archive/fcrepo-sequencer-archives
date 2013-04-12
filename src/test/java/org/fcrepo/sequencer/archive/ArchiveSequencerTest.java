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
    public void testListContents()
    {
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
