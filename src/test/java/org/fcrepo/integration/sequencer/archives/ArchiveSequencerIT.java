
package org.fcrepo.integration.sequencer.archives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({"/spring-test/repo.xml", "/spring-test/rest.xml",
        "/spring-test/eventing.xml"})
public class ArchiveSequencerIT extends AbstractResourceIT {

    final private Logger log = LoggerFactory
            .getLogger(ArchiveSequencerIT.class);

    @Test
    public void testArchiveSequencer() throws Exception {
        File zipfile = new File("target/test-classes/test-data/tmp.zip");
        String zipContent = "tmp.txt\n";
        String pid = "archiveSequencerTestObject";
        String dsid = "1.zip";

        // create object
        final HttpPost objMethod = postObjMethod(pid);
        assertEquals(201, getStatus(objMethod));

        // upload zip file
        final HttpPost method = postDSMethod(pid, dsid, zipfile);
        final HttpResponse response = client.execute(method);
        final String location = response.getFirstHeader("Location").getValue();
        assertEquals(201, response.getStatusLine().getStatusCode());
        assertEquals(
                "Got wrong URI in Location header for datastream creation!",
                serverAddress + "/rest/objects/" + pid + "/datastreams/" + dsid,
                location);

        final HttpGet dsListGet =
                new HttpGet(serverAddress + "/rest/objects/" + pid +
                        "/datastreams/");
        String dsList =
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
                new HttpGet(serverAddress + "/rest/objects/" + pid +
                        "/datastreams/" + dsid + "_archiveContents/content");
        String actual =
                EntityUtils.toString(client.execute(extractedContents)
                        .getEntity());
        assertTrue("Sequencer output not saved", zipContent.equals(actual));
    }
}
