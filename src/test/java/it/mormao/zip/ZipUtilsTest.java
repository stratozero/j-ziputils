package it.mormao.zip;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

public class ZipUtilsTest {
	@Test
	public void testUnzipSimple() throws URISyntaxException, IOException {
		Path zipPath = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource("lorem-ipsum.zip")).toURI());
		Path folderOut = zipPath.resolveSibling(zipPath.getFileName() + "_full");
		ZipUtils.unzip(zipPath, folderOut);
		assertEquals("Not all file have been extracted", Objects.requireNonNull(folderOut.toFile().list()).length, 3);
	}

	@Test
	public void testUnzipGlob() throws URISyntaxException, IOException {
		Path zipPath = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource("lorem-ipsum.zip")).toURI());
		Path folderOut = zipPath.resolveSibling(zipPath.getFileName() + "_glob");

		ZipUtils.unzip(zipPath, folderOut, "*.xml");
		assertEquals("Not all xml file have been extracted", Objects.requireNonNull(folderOut.toFile().list()).length, 1);
	}

	@Test
	public void testUnzipPredicate() throws URISyntaxException, IOException {
		Path zipPath = Paths.get(Objects.requireNonNull(this.getClass().getClassLoader().getResource("lorem-ipsum.zip")).toURI());
		Path folderOut = zipPath.resolveSibling(zipPath.getFileName() + "_regexp");

		final Pattern testPattern = Pattern.compile("^lorem-ipsum\\.pdf$");
		ZipUtils.unzip(zipPath, folderOut, (zipEntry -> testPattern.matcher(zipEntry.getName()).matches() ));
		assertEquals("Not all matchig file have been extracted", Objects.requireNonNull(folderOut.toFile().list()).length, 1);
	}
}
