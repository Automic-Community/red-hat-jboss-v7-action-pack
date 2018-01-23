package com.uc4.ara.feature.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//import com.esotericsoftware.wildcard.Paths;
import com.uc4.ara.feature.FeatureUtil;

public class ZipUtil {
	public static final void zipDirectory(File directory, File zip)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(zip);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zipArchive(directory, directory, zos);
		zos.close();
		fos.close();
	}

	private static final void zipArchive(File directory, File base,
			ZipOutputStream zos) throws IOException {
		File[] files = directory.listFiles();
		byte[] buffer = new byte[8192];
		int read = 0;
		for (int i = 0, n = files.length; i < n; i++) {
			if (files[i].isDirectory()) {
				zipArchive(files[i], base, zos);
			} else {
				//FileInputStream in = new FileInputStream(files[i]);
				RandomAccessFile raf = new RandomAccessFile(files[i], "r");
				ZipEntry entry = new ZipEntry(files[i].getPath().substring(
						base.getPath().length() + 1).replace("\\", "/"));
				zos.putNextEntry(entry);
				try {
					while (-1 != (read = raf.read(buffer))) {
						zos.write(buffer, 0, read);
					}
				} finally {
					raf.close();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static final void unzipArchive(File zip, File extractTo)
			throws IOException {
		ZipFile archive = new ZipFile(zip);
		Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) archive.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = e.nextElement();
			File file = new File(extractTo, entry.getName());
			if (entry.isDirectory()) {
				if (!file.exists())
					file.mkdirs();
			} else {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				InputStream in = archive.getInputStream(entry);
				// System.out.println("  file " + file.isDirectory() + ", "
				// + file.isFile());
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));
				byte[] buffer = new byte[8192];
				int read;
				while (-1 != (read = in.read(buffer))) {
					out.write(buffer, 0, read);
				}
				in.close();
				out.close();
			}
			file.setLastModified(entry.getTime());
		}
		archive.close();
	}

	/** Unzip a single file (fileName) in zipDir to destFile.
	 * @param zipDir File to upzip
	 * @param destFile
	 * 			The destination file to unzip into
	 * @param fileName
	 * 			the fileName inside the zipDir
	 * @throws FileNotFoundException
	 */
	public static void unzipFile(File zipDir, File destFile, String fileName) throws Exception{

		ZipInputStream is = null;
		FileOutputStream out = null;
		ZipEntry entry = null;
		try {
			is = new ZipInputStream(new FileInputStream( zipDir));

			while ((entry = is.getNextEntry()) != null) {
				String fileInZip = entry.getName().replace("/", "\\");
				fileInZip = fileInZip.replace("\\", File.separator);
				if (!fileInZip.equals(fileName)) {
					continue;
				}
				break;
			}
			if (entry == null) {
				is.close();
				throw new Exception(fileName + " cannot be found in " + zipDir.getAbsolutePath());
			}
		} catch (IOException e) {
			FeatureUtil.logMsg(zipDir.getAbsolutePath() + " is not a valid compressed file!");
		}

		destFile.getParentFile().mkdirs();
		out = new FileOutputStream(destFile);

		byte b[] = new byte[8192];
		while (is != null && is.available() > 0) {

			int ret = is.read(b);
			if (ret == -1)
				break;
			out.write(b, 0, ret);
			out.flush();
		}
		out.close();
		if(is != null)
			is.close();
	}

	@SuppressWarnings("unchecked")
	public static final void unzipFileInArchive(File zip, String relpathInZip, File extractTo)
			throws ZipException, IOException {
		relpathInZip = relpathInZip.replace("\\", "/");
		Pattern pattern = Pattern.compile(wildcardToRegex(relpathInZip), Pattern.CASE_INSENSITIVE);

		ZipFile archive = new ZipFile(zip);
		Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) archive.entries();
		while (e.hasMoreElements()) {
			ZipEntry entry = e.nextElement();
			File file = new File(extractTo, entry.getName());
			String name = '/' + entry.getName();
			Matcher matcher = pattern.matcher(name);
			if (!entry.isDirectory()) {
				if (matcher.find() || relpathInZip.indexOf(entry.getName()) >= 0) {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					InputStream in = archive.getInputStream(entry);
					BufferedOutputStream out = new BufferedOutputStream(
							new FileOutputStream(file));
					byte[] buffer = new byte[8192];
					int read;
					while (-1 != (read = in.read(buffer))) {
						out.write(buffer, 0, read);
					}
					in.close();
					out.close();
				}
			}
		}
		archive.close();
	}

/*	public static final void zipFileIntoArchive(File directory, String relpathInZip, File zip)
			throws IOException {
		Paths paths = new Paths();
		paths.glob(directory.getPath(), relpathInZip);
		paths.filesOnly();
		File[] files = paths.getFiles();

		byte[] buffer = new byte[8192];
		int read = 0;

		// Create temp Zip file
		File temp = File.createTempFile(zip.getName(), null);
		temp.delete();
		// Rename zip file to temp
		if (!zip.renameTo(temp)) {
			throw new RuntimeException("Can not rename file "
					+ zip.getAbsolutePath() + " to " + temp.getAbsolutePath());
		}

		// Write entries in temp file to zip
		ZipInputStream zis = new ZipInputStream(
				new FileInputStream(temp));
		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(zip));
		ZipEntry entry = zis.getNextEntry();
		while (entry != null) {
			if (!entry.isDirectory()) {
				String name = entry.getName();
				boolean found = false;
				for (File file : files) {
					if (file.isDirectory())
						continue;
					String relpath = file.getPath().substring(
							directory.getAbsolutePath().length() + 1 )
							.replace("\\", "/");
					if (relpath.equalsIgnoreCase(name)) {
						found = true;
						break;
					}
				}
				if (!found) {
					zos.putNextEntry(new ZipEntry(name));
					while (-1 != (read = zis.read(buffer))) {
						zos.write(buffer, 0, read);
					}
				}
			}
			entry = zis.getNextEntry();
		}
		// Close input temp file
		zis.close();

		// Write update file to zip
		for (File file : files) {
			if (file.isDirectory())
				continue;
			String relpath = file.getPath().substring(
					directory.getAbsolutePath().length() + 1 )
					.replace("\\", "/");
			RandomAccessFile raf = new RandomAccessFile(file, "r");
			entry = new ZipEntry(relpath);
			zos.putNextEntry(entry);
			try {
				while (-1 != (read = raf.read(buffer))) {
					zos.write(buffer, 0, read);
				}
			} finally {
				raf.close();
			}
		}
		// Complete the zip file
		zos.close();
		temp.delete();
	}
*/
	public static String wildcardToRegex(String wildcard){
		StringBuffer s = new StringBuffer(wildcard.length());
		s.append('^');
		for (int i = 0, is = wildcard.length(); i < is; i++) {
			char c = wildcard.charAt(i);
			switch(c) {
			case '*':
				s.append(".*");
				break;
			case '?':
				s.append(".");
				break;
				// escape special regexp-characters
			case '(': case ')': case '[': case ']': case '$':
			case '^': case '.': case '{': case '}': case '|':
			case '\\':
				s.append("\\");
				s.append(c);
				break;
			default:
				s.append(c);
				break;
			}
		}
		s.append('$');
		return(s.toString());
	}

	public static void zipFile(File file, File zipFile) throws Exception {
		byte[] buffer = new byte[8192];
		int read = 0;

		ZipOutputStream zos = new ZipOutputStream(
				new FileOutputStream(zipFile));

		FileInputStream fis = new FileInputStream(file);

		zos.putNextEntry(new ZipEntry(file.getName()));

		while ((read = fis.read(buffer)) > 0) {
			zos.write(buffer, 0, read);
		}

		fis.close();
		zos.closeEntry();
		zos.close();
	}

	public static void unzipFileInArchive(FileChannel channel,
			String relpathInZip, File extractTo) throws FileNotFoundException, IOException {
		relpathInZip = relpathInZip.replace("\\", "/");
		Pattern pattern = Pattern.compile(wildcardToRegex(relpathInZip), Pattern.CASE_INSENSITIVE);

		// ZipFile archive = new ZipFile(zip);
		ZipInputStream stream = new ZipInputStream(Channels.newInputStream(channel));
		ZipEntry entry;

		// Enumeration<ZipEntry> e = (Enumeration<ZipEntry>) archive.entries();
		// while (e.hasMoreElements()) {
		while ((entry = stream.getNextEntry()) != null) {
			// ZipEntry entry = e.nextElement();
			File file = new File(extractTo, entry.getName());
			String name = '/' + entry.getName();
			Matcher matcher = pattern.matcher(name);
			if (!entry.isDirectory()) {
				if (matcher.find() || relpathInZip.indexOf(entry.getName()) >= 0) {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					// InputStream in = archive.getInputStream(entry);
					BufferedOutputStream out = new BufferedOutputStream(
							new FileOutputStream(file));
					byte[] buffer = new byte[8192];
					int read;
					while (-1 != (read = stream.read(buffer))) {
						out.write(buffer, 0, read);
					}
					// in.close();
					out.close();
				}
			}
		}
		stream.close();
	}
}
