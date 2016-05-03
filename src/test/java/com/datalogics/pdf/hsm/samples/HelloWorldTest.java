/*
 * Copyright 2015 Datalogics, Inc.
 */

package com.datalogics.pdf.hsm.samples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.adobe.pdfjt.pdf.document.PDFDocument;

import com.datalogics.pdf.hsm.samples.util.DocumentUtils;

import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Tests the HelloWorld sample.
 */
public class HelloWorldTest extends SampleTest {
    static final String FILE_NAME = "HelloWorld.pdf";

    @Test
    public void testHelloWorld() throws Exception {
        final File file = newOutputFile(FILE_NAME);
        if (file.exists()) {
            Files.delete(file.toPath());
        }
        HelloWorld.helloWorld(file.toURI().toURL());
        assertTrue(file.getPath() + " must exist after run", file.exists());

        final PDFDocument doc = DocumentUtils.openPdfDocument(file.toURI().toURL());
        final String resourceName = "HelloWorld.pdf.page1.txt";

        try (final InputStream is = HelloWorldTest.class.getResourceAsStream(resourceName)) {
            final String contentsAsString = pageContentsAsString(doc, 0);

            assertEquals(contentsOfInputStream(is), contentsAsString);

        } finally {
            doc.close();
        }
    }
}
