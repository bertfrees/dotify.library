package org.daisy.braille.utils.impl.provider.cidat;

import org.daisy.braille.utils.pef.FileCompare;
import org.daisy.braille.utils.pef.PEFConverterFacade;
import org.daisy.braille.utils.pef.PEFHandler;
import org.daisy.braille.utils.pef.UnsupportedWidthException;
import org.daisy.dotify.api.embosser.EmbosserCatalog;
import org.daisy.dotify.api.embosser.EmbosserFeatures;
import org.daisy.dotify.api.embosser.EmbosserWriter;
import org.daisy.dotify.api.paper.PageFormat;
import org.daisy.dotify.api.paper.PaperCatalog;
import org.daisy.dotify.api.paper.SheetPaper;
import org.daisy.dotify.api.paper.SheetPaperFormat;
import org.daisy.dotify.api.table.TableCatalog;
import org.daisy.dotify.common.io.FileIO;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Bert Frees
 */
@SuppressWarnings("javadoc")
public class ImpactoEmbosserTest {

    private static EmbosserCatalog ec = EmbosserCatalog.newInstance();
    private static CidatEmbosser embosser_texto =
            (CidatEmbosser) ec.get("es_once_cidat.CidatEmbosserProvider.EmbosserType.IMPACTO_TEXTO");
    private static CidatEmbosser embosser_600 =
            (CidatEmbosser) ec.get("es_once_cidat.CidatEmbosserProvider.EmbosserType.IMPACTO_600");

    private static PaperCatalog pc = PaperCatalog.newInstance();
    private static PageFormat a4 =
            new SheetPaperFormat(
                (SheetPaper) pc.get("org_daisy.ISO216PaperProvider.PaperSize.A4"),
                SheetPaperFormat.Orientation.DEFAULT
            );

    @Test
    public void testPrintableArea() {

        assertEquals(
            "Assert that max width for a A4 paper is 33 cells",
            embosser_texto.getMaxWidth(a4),
            33
        );
        assertEquals(
            "Assert that max height for a A4 paper is 29 lines",
            embosser_texto.getMaxHeight(a4),
            29
        );
    }

    @Test
    public void testTableFilter() {

        TableCatalog tc = TableCatalog.newInstance();
        assertTrue(
            "Assert that encoding cannot be modified",
            tc.list(embosser_texto.getTableFilter()).size() <= 1
        );
    }

    @Test
    public void testDuplex() {

        assertTrue(
            "Assert that duplex is supported (Impacto texto)",
            embosser_texto.supportsDuplex()
        );
        assertTrue(
            "Assert that duplex is supported (Impacto 600)",
            embosser_600.supportsDuplex()
        );
    }

    @Test
    public void test8dot() {

        assertTrue(
            "Assert that 8-dot is not supported (Impacto texto)",
            !embosser_texto.supports8dot()
        );
        assertTrue(
            "Assert that 8-dot is not supported (Impacto 600)",
            !embosser_600.supports8dot()
        );
    }

    @Test
    public void testAligning() {

        assertTrue(
            "Assert that aligning is supported (Impacto texto)",
            embosser_texto.supportsAligning()
        );
        assertTrue(
            "Assert that aligning is supported (Impacto 600)",
            embosser_600.supportsAligning()
        );
    }

    @Test
    public void testEmbosserWriter() throws IOException,
            ParserConfigurationException,
            SAXException,
            UnsupportedWidthException {

        File prn1 = File.createTempFile("test_impacto_", ".prn");
        File prn2 = File.createTempFile("test_impacto_", ".prn");
        File pef = File.createTempFile("test_impacto_", ".pef");

        FileCompare fc = new FileCompare();
        PEFHandler.Builder builder;
        EmbosserWriter w;

        embosser_texto.setFeature(EmbosserFeatures.PAGE_FORMAT, a4);

        // Single sided

        w = embosser_texto.newEmbosserWriter(new FileOutputStream(prn1));
        builder = new PEFHandler.Builder(w)
                .range(null)
                .align(org.daisy.braille.utils.pef.PEFHandler.Alignment.INNER)
                .offset(0)
                .topOffset(0);

        FileIO.copy(
            this.getClass().getResourceAsStream("resource-files/single_sided.pef"),
            new FileOutputStream(pef)
        );
        FileIO.copy(
            this.getClass().getResourceAsStream("resource-files/impacto_single_sided.prn"),
            new FileOutputStream(prn2)
        );
        new PEFConverterFacade(EmbosserCatalog.newInstance()).parsePefFile(pef, builder.build());
        try (InputStream is2 = new FileInputStream(prn2)) {
            assertTrue("Assert that the contents of the file is as expected.",
                    fc.compareBinary(new FileInputStream(prn1), is2)
            );
        }

        // Double sided

        w = embosser_texto.newEmbosserWriter(new FileOutputStream(prn1));
        builder = new PEFHandler.Builder(w)
                .range(null)
                .align(org.daisy.braille.utils.pef.PEFHandler.Alignment.INNER)
                .offset(0)
                .topOffset(0);

        FileIO.copy(
            this.getClass().getResourceAsStream("resource-files/double_sided.pef"),
            new FileOutputStream(pef)
        );
        FileIO.copy(
            this.getClass().getResourceAsStream("resource-files/impacto_double_sided.prn"),
            new FileOutputStream(prn2)
        );
        new PEFConverterFacade(EmbosserCatalog.newInstance()).parsePefFile(pef, builder.build());
        try (InputStream is2 = new FileInputStream(prn2)) {
            assertTrue(
                "Assert that the contents of the file is as expected.",
                fc.compareBinary(new FileInputStream(prn1), is2)
            );
        }

        prn1.deleteOnExit();
        prn2.deleteOnExit();
        pef.deleteOnExit();
    }
}
