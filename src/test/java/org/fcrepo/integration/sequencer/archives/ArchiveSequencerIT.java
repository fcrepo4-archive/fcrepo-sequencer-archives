
package org.fcrepo.integration.sequencer.archives;

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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.modeshape.jcr.api.JcrTools;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({"/spring-test/repo.xml", "/spring-test/rest.xml",
        "/spring-test/test-container.xml"})
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

        final HttpGet dsListGet =
                new HttpGet(serverAddress + "/rest/" + pid);

        for (int i = 0; i < 10; i++) {
            if (client.execute(dsListGet).getStatusLine().getStatusCode() == 200) {
                break;
            }
            Thread.sleep(250);
        }

        final String dsList =
                EntityUtils.toString(client.execute(dsListGet).getEntity());
        log.debug("dsList: " + dsList);
        /*
         * assertTrue(
         * dsid + "_archiveContents datastream not created:\n"+dsList,
         * dsList.indexOf(dsid + "_archiveContents") != -1
         * );
         */

        // check _archiveContents datastream content
        final HttpGet extractedContents =
                new HttpGet(serverAddress + "/rest/" + pid +
                                    "/" + dsid + "_archiveContents/fcr:content");
        final String actual =
                EntityUtils.toString(client.execute(extractedContents)
                                             .getEntity());
        log.debug("Contents of " + extractedContents.toString() + " are \n" +
                          actual);
        assertTrue("Sequencer output not saved", zipContent.equals(actual));
        log.debug("Sequencer output was saved.");

    }
}
