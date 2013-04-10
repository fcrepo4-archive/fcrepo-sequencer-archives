package org.fcrepo.sequencer.archive;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.jcr.Binary;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.ConstraintViolationException;

import org.modeshape.jcr.api.JcrConstants;
import org.modeshape.jcr.api.nodetype.NodeTypeManager;
import org.modeshape.jcr.api.sequencer.Sequencer;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.ArchiveException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fcrepo.Datastream;
import org.fcrepo.services.DatastreamService;
import org.fcrepo.utils.DatastreamIterator;

public class ArchiveSequencer extends Sequencer {
	
	private static Logger log = LoggerFactory.getLogger(ArchiveSequencer.class);
	
    public ArchiveSequencer() {
    }

    @Override
    public void initialize( NamespaceRegistry registry, NodeTypeManager nodeTypeManager ) throws RepositoryException, IOException {
    	super.initialize(registry, nodeTypeManager);
		log.debug("ArchiveSequencer.initialize()");
    }
    
	@Override
	public boolean execute( Property inputProperty, Node outputNode,
		Context context) throws Exception {
    	log.debug(
			"Sequencing property change: \"{}\", expecting \"{}\"",
			inputProperty.getName(), JcrConstants.JCR_DATA
		);
        if (JcrConstants.JCR_DATA.equals(inputProperty.getName()))
		{
        	Binary inputBinary = inputProperty.getBinary();
        	InputStream in = inputBinary.getStream();
			String contents = listContents(in);
			log.debug("contents: '" + contents + "'");

			if ( contents != null && !contents.trim().equals(""))
			{
				// saving contents to a new datastream
				String outPath = outputNode.getPath() + "_archiveContents";
				log.debug("outPath: " + outPath);
				Session session = outputNode.getSession();
				Datastream ds = new Datastream( session, outPath );
				ds.setContent( new ByteArrayInputStream(contents.getBytes()) );
				session.save();
        		log.debug( "Sequenced output node at path: {}", outPath );
        		return true;
			}
        }
        return false;
	}
	public static String listContents( InputStream in )
		throws IOException, ArchiveException
	{
		ArchiveStreamFactory factory = new ArchiveStreamFactory();
		ArchiveInputStream arc = null;
		try {
			arc = factory.createArchiveInputStream(
				new BufferedInputStream(in)
			);
		}
		catch ( Exception ex )
		{
			log.warn( "Error parsing archive input", ex );
			return null;
		}
		StringBuffer contents = new StringBuffer();
		for ( ArchiveEntry entry = null; (entry=arc.getNextEntry()) != null; )
		{
			contents.append( entry.getName() + "\n" );
		}
		return contents.toString();
	}
}
