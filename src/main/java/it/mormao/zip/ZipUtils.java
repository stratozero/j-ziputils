package it.mormao.zip;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@SuppressWarnings("unused")
public class ZipUtils {

	public static void unzip(final Path in, final Path folderOut) throws IOException{
		unzip(in, folderOut, (Predicate<ZipEntry>) null);
	}

	public static void unzip(final Path in, final Path folderOut, String glob) throws IOException{
		if(glob == null || glob.isEmpty())
			unzip(in, folderOut, (Predicate<ZipEntry>) null);
		else {
			final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
			unzip(in, folderOut, (zipEntry -> matcher.matches(Paths.get(zipEntry.getName()))));
		}
	}

	public static void unzip (final Path in, final Path folderOut, final Predicate<ZipEntry> filter) throws IOException{
		final AtomicBoolean folderExists = new AtomicBoolean(); // Flag for creating output folder (done only if needed)
		forEachEntryExec(in, filter, (zipFile, zipEntry) -> {
			if(!folderExists.get()) {
				// Creating folder if does not exists
				if (!Files.exists(folderOut)) {
					try {
						Files.createDirectory(folderOut);
					} catch (FileAlreadyExistsException faee) {
						// Nothing to do: directory already exists
					}
				}
				folderExists.set(true);
			}
			try(InputStream inputStream = zipFile.getInputStream(zipEntry);
			    OutputStream outputStream = Files.newOutputStream(folderOut.resolve(zipEntry.getName()))){
				IOUtils.copy(inputStream, outputStream);
			}
		});
	}
	public static void forEachEntryExec(final Path in, final Predicate<ZipEntry> filter, final ThrowingIOBiConsumer<ZipFile, ZipEntry> consumer) throws IOException {
		try(ZipFile zipFile = new ZipFile(in.toFile())){
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			ZipEntry entry;
			while(entries.hasMoreElements()){
				entry = entries.nextElement();
				if(filter == null || filter.test(entry)){
					consumer.accept(zipFile, entry);
				}
			}
		}
	}
}