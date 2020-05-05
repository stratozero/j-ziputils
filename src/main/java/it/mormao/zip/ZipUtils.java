package it.mormao.zip;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtils {
	@SuppressWarnings("unused")
	public static void unzip(Path in, final Path folderOut) throws IOException{
		unzip(in, folderOut, (Predicate<ZipEntry>) null);
	}

	@SuppressWarnings("unused")
	public static void unzip(Path in, Path folderOut, String glob) throws IOException{
		if(glob == null || glob.isEmpty())
			unzip(in, folderOut, (Predicate<ZipEntry>) null);
		else {
			final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
			unzip(in, folderOut, (zipEntry -> matcher.matches(Paths.get(zipEntry.getName()))));
		}
	}

	public static void unzip (Path in, final Path folderOut, Predicate<ZipEntry> filter) throws IOException{
		forEachEntryExec(in, folderOut, filter, (zipFile, zipEntry) -> {
			if(!Files.exists(folderOut)){
				try {
					Files.createDirectory(folderOut);
				} catch (FileAlreadyExistsException faee){
					// Nothing to do: directory already exists
				}
			}
			try(InputStream inputStream = zipFile.getInputStream(zipEntry);
			    OutputStream outputStream = Files.newOutputStream(folderOut.resolve(zipEntry.getName()))){
				IOUtils.copy(inputStream, outputStream);
			}
		});
	}
	public static void forEachEntryExec(Path in, Path folderOut, Predicate<ZipEntry> filter, ThrowingIOBiConsumer<ZipFile, ZipEntry> consumer) throws IOException {
		if(in != null & folderOut != null){
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
}
