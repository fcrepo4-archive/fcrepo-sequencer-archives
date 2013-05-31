
package org.fcrepo.integration.sequencer.archives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.modeshape.jcr.api.JcrConstants.JCR_CONTENT;
import static org.modeshape.jcr.api.JcrConstants.JCR_DATA;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.modeshape.jcr.api.JcrTools;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({"/spring-test/repo.xml"})
public class ArchiveSequencerIT extends AbstractResourceIT {

    @Autowired
    Repository repo;

    private static final Logger log = getLogger(ArchiveSequencerIT.class);

    private static final JcrTools jcrTools = new JcrTools();

    @Test
    public void testArchiveSequencer() throws Exception {
        final InputStream zipfile =
                new FileInputStream(new File(
                        "target/test-classes/test-data/tmp.zip"));
        final String zipContent = "tmp.txt\n";
        final String pid = "archiveSequencerTestObject";
        final String dsid = "1.zip";

        final Session session = repo.login();

        log.debug("Uploading zip file...");
        final Node uploadNode =
                jcrTools.findOrCreateNode(session, "/uploads/" + pid).addNode(
                                                                                     dsid);

        uploadNode.addNode(JCR_CONTENT).setProperty(JCR_DATA,
                                                           session.getValueFactory().createBinary(zipfile));
        session.save();
        log.debug("Uploaded zip file to {}.", uploadNode.getPath());

        for (int i = 0; i < 10; i++) {
            if (session.nodeExists("/" + pid)) {
                break;
            }
            Thread.sleep(250);
        }

        assertTrue(session.nodeExists("/" + pid));
        assertTrue(session.nodeExists("/" + pid + "/1.zip_archiveContents"));

        InputStream contentStream = session.getNode("/" + pid + "/1.zip_archiveContents/jcr:content").getProperty("jcr:data").getBinary().getStream();
        assertEquals(zipContent, IOUtils.toString(contentStream));




    }
}
