package org.fcrepo.sequencer.archive;

import java.io.FileInputStream;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.sequencer.Sequencer;
import org.modeshape.jcr.api.sequencer.Sequencer.Context;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class ArchiveSequencerTest extends junit.framework.TestCase {
	
    @Test
    public void testSequencerValid()
    {
    	try {
			FileInputStream zipin = new FileInputStream("target/test-classes/test-data/tmp.zip");
			String contents = ArchiveSequencer.listContents(zipin);
			assertEquals("tmp.txt\n", contents);
			zipin.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
    }
}
