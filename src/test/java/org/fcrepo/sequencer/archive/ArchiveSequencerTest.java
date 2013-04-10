package org.fcrepo.sequencer.archive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.sequencer.Sequencer;
import org.modeshape.jcr.api.sequencer.Sequencer.Context;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.After;
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

	@Test
	public void testExecute()
	{
		try
		{
			Binary mockBin = mock(Binary.class);
			when(mockBin.getStream()).thenReturn(zipin);
			Property mockProp = mock(Property.class);
			when(mockProp.getName()).thenReturn(JcrConstants.JCR_DATA);
			when(mockProp.getBinary()).thenReturn(mockBin);
	
			Session mockSession = mock(Session.class);
			Node mockNode = mock(Node.class);
			when(mockNode.canAddMixin(any(String.class))).thenReturn(true);
			when(mockNode.getSession()).thenReturn(mockSession);
			when(mockNode.getNode(any(String.class))).thenReturn(mockNode);
			when(mockNode.getParent()).thenReturn(mockNode);
			when(
				mockNode.setProperty( any(String.class), not(eq(zipContents)) )
			).thenThrow( new RuntimeException("Incorrect contents") );
	
			Context mockContext = mock(Context.class);

			ArchiveSequencer arch = new ArchiveSequencer();
			//arch.execute( mockProp, mockNode, mockContext );
// need to handle parent node, etc.
// need to check for content datastream
		}
		catch ( Exception ex )
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
}