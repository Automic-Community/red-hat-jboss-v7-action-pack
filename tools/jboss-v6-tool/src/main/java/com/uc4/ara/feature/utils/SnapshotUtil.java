package com.uc4.ara.feature.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.uc4.ara.feature.FeatureUtil;
import com.uc4.ara.feature.weblogic.FileType;

public class SnapshotUtil {

	public static enum FileCopyOption {

		PreserveDate, CopySymbolicLink

	}
	private static final String slash = File.separator;
	private static final String DEFAULT_HASH = "MD5";
	private static final DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL);
	private static final int defaultBlockSize = 8 * 1024;
	private static final String osName = System.getProperty("os.name").toLowerCase();
	public static boolean isWindows() {
		return osName.startsWith("windows");
	}

	public static boolean isSymbolic(File f) throws IOException {
		return f.getCanonicalPath().endsWith(".lnk");
	}

	/**
	 * Copies a file to a new location without locking source file This function
	 * doesn't work with big file :)
	 * 
	 * @param srcFile
	 *            the existing file to copy
	 * @param destFile
	 *            the new file
	 * @param options
	 *            PreserveDate true if the file date of the copy should be the
	 *            same as the original. CopySymbolicLink true if the symbolic
	 *            link is copied rather than the real file it points to.
	 * @throws Exception
	 */
	public static void copyFile(File srcFile, File destFile,
			FileCopyOption... options) throws Exception {

		RandomAccessFile raf = new RandomAccessFile(srcFile, "r");
		FileChannel fc = raf.getChannel();
		int len;
		byte[] buf = new byte[defaultBlockSize];

		MappedByteBuffer mb = fc.map(FileChannel.MapMode.READ_ONLY, 0L,
				fc.size());

		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}

		OutputStream out = new FileOutputStream(destFile);

		while (mb.hasRemaining()) {
			len = Math.min(mb.remaining(), defaultBlockSize);
			mb.get(buf, 0, len);
			out.write(buf, 0, len);
		}

		mb.clear();

		out.close();
		fc.close();
		raf.close();

		for (FileCopyOption option : options) {

			if (option == FileCopyOption.PreserveDate) {
				destFile.setLastModified(srcFile.lastModified());
				break;
			}
		}

	}

	/**
	 * Copies a whole directory to a new location
	 * 
	 * @param srcDir
	 *            the existing directory to copy
	 * @param destDir
	 *            the new directory
	 * @param filter
	 *            the folders should be excluded from the destDir
	 * @param options
	 *            PreserveDate true if the file date of the copy should be the
	 *            same as the original. CopySymbolicLink true if the symbolic
	 *            link is copied rather than the real file it points to.
	 * @throws Exception
	 */
	public static void copyDirectory(File srcDir, File destDir, String filter,
			FileCopyOption... options) throws Exception {

		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String[] names = srcDir.list();
		boolean copySymbolic = false;
		for (int i = 0; i < options.length; i++) {
			if (options[i] == FileCopyOption.CopySymbolicLink) {
				copySymbolic = true;
				break;
			}
		}
		for (String name : names) {
			boolean isExclude = true;
			if ((filter == null) || (filter.isEmpty())) {
				isExclude = false;
			} else {
				isExclude = name.matches(filter);
			}

			if (!isExclude) {
				File srcFile = new File(srcDir, name);
				if (srcFile.isDirectory()) {
					copyDirectory(srcFile, new File(destDir, name), null,
							options);
				} else {
					if (copySymbolic && isWindows() && isSymbolic(srcFile)) {
						try {

							String realFilename = new WindowsShortcut(srcFile)
							.getRealFilename();
							File realFile = new File(realFilename);
							if (!realFile.exists()) {
								throw new FileNotFoundException("Real file "
										+ realFile.getCanonicalPath() + " not exist");
							}
							name = realFile.getName();

							if (realFile.isFile()) {

								copyFile(realFile, new File(destDir, name), options);
							} else {
								copyDirectory(realFile, new File(destDir, name), null,
										options);
							}

						} catch (Exception e) {
							copyFile(srcFile, new File(destDir, name), options);
						}
					} else {
						copyFile(srcFile, new File(destDir, name), options);
					}
				}
			}
		}

	}

	/**
	 * Cleans up all names of files/directories, that match patterns
	 * 
	 * @param srcDir
	 *            the source directory will be clean
	 * @param filter
	 *            the regular expression that contains list of files/directories
	 *            will be excluded from source directory
	 */
	public static void cleanDirectory(File srcDir, String filter) {

		if ((filter != null) && (!filter.isEmpty())) {
			String[] names = srcDir.list();
			for (String name : names) {
				if (name.matches(filter))
					FileUtil.deleteDirectory(new File(srcDir, name));
			}
		}
	}

	/**
	 * Finds all files in the list that end with 'extension' suffix
	 * 
	 * @param l
	 *            the list contains files
	 * @param extension
	 *            the suffix.
	 * 
	 * @return A list of filetype that ends with extension and the files end
	 *         with 'extension' suffix will be removed from original list
	 */
	public static List<FileType> filter(List<FileType> l, String extension) {
		List<FileType> filterList = new ArrayList<FileType>();
		for (FileType fileType : l) {
			if (fileType.getName().endsWith(extension)) {
				filterList.add(fileType);
			}
		}

		l.removeAll(filterList);

		return filterList;
	}

	public static List<File> filterFiles(List<File> files, String topDir, String excludefilters) throws IOException {
		// form the include exclude lists

		List<Pattern> excludes = getFilters(excludefilters);
		for (Iterator<File> iter = files.iterator(); iter.hasNext();)
		{
			File f = iter.next();
			for (Pattern exclude : excludes)
				if (exclude.matcher(getRelativePath(topDir, f)).matches())
				{
					iter.remove();
					f = null;
					break;
				}
		}
		return files;
	}

	public static List<Pattern> getFilters(String excludefilters) throws IOException {

		String[] excludefilter = excludefilters.split(",");

		//form the include exclude lists
		List<Pattern> excludes = new ArrayList<Pattern>();

		if(!excludefilters.trim().equals("NULL") && !excludefilters.trim().equals("") ) {
			for (int i = 0; i < excludefilter.length; i++) {

				String regex = convertRegex(excludefilter[i]);

				Pattern p = Pattern.compile(regex);
				excludes.add(p);
			}
		}

		return excludes;
	}

	/** For Include Filter and Exclude Filter of snapshot
	 * @param regex
	 * @return
	 */
	public static String convertRegex(String regex)
	{
		String slashRegex = "[/\\\\]+";
		regex = regex.trim();

		/*
		// Remove first / or ./
		if (regex.matches("^" + slashRegex + ".*"))
			regex = regex.replaceFirst(slashRegex, "");
		else if (regex.matches("^\\." + slashRegex + ".*"))
			regex = regex.replaceFirst("\\.[\\\\/]+", "");

		// Remove last /
		if (regex.matches(".*" + slashRegex + "$"))
			regex = regex.replaceAll("^(.*)" + slashRegex + "$", "$1");
		 */

		// Replace / or \ -> [\\/]
		regex = regex.replaceAll(slashRegex, "[/\\\\\\\\]");

		for(String regexChar : new String[]{".","+","^","$","{","}","(",")"})
			regex = regex.replace(regexChar, "\\" + regexChar);

		regex = regex.replace("*", ".*");
		regex = regex.replace("?", ".");

		return regex;
	}

	/**Get list of files inside a directory recursively
	 * @param dir
	 * @param isGetSymbolicLink
	 * @return
	 * @throws IOException
	 */
	public static List<File> getFileList(File dir, boolean isGetSymbolicLink) throws IOException {
		List<File> files = new ArrayList<File>();
		// keep track of directories so the list stays well ordered
		List<File> dirs = new ArrayList<File>();

		File[] dirFiles = dir.listFiles();
		if(dirFiles != null) {
			for (File file : dirFiles) {

				if (file.isDirectory()) {
					dirs.add(file);

				}else{
					if (isGetSymbolicLink && isWindows() && isSymbolic(file)) {
						String realFileName = null;
						try {
							realFileName = new WindowsShortcut(file).getRealFilename();
						} catch (ParseException e) {
							FeatureUtil.logMsg("Can't parse symbolic link " + file.getAbsolutePath() + " . Skip this file!");
							continue;
						}

						File realFile = new File(realFileName);
						if(realFile.isDirectory()){
							files.addAll(getFileList(realFile, true));
						} else if(realFile.isFile()){
							files.add(realFile);
						} else {
							throw new FileNotFoundException("Real file " + realFile.getCanonicalPath() + " not exist");
						}

					} else
						files.add(file);
				}
			}
			for (File fdir : dirs) {
				//files.add(fdir.getAbsoluteFile());
				files.addAll(getFileList(fdir, isGetSymbolicLink));
			}
		}

		return files;
	}

	/**
	 * @param dir
	 * @param topDir
	 * @param excludeFilters
	 * @param isGetSymbolicLink
	 * @return
	 * @throws IOException
	 */
	public static List<File> getFilterFileList(File dir, String topDir, List<Pattern> excludeFilters, boolean isGetSymbolicLink) throws IOException {
		List<File> files = new ArrayList<File>();
		// keep track of directories so the list stays well ordered
		List<File> dirs = new ArrayList<File>();

		for (Pattern exclude : excludeFilters)
			if ( exclude.matcher(getRelativePath(topDir, dir) ).matches())
			{
				return files;
			}

		File[] dirFiles = dir.listFiles();
		if(dirFiles != null) {
			for (File file : dirFiles) {

				if (file.isDirectory()) {
					dirs.add(file);

				}else{
					if (isGetSymbolicLink && isWindows() && isSymbolic(file)) {
						String realFileName = null;
						try {
							realFileName = new WindowsShortcut(file).getRealFilename();
						} catch (ParseException e) {
							FeatureUtil.logMsg("Can't parse symbolic link " + file.getAbsolutePath() + " . Skip this file!");
							continue;
						}

						File realFile = new File(realFileName);
						if(realFile.isDirectory()){
							files.addAll(getFileList(realFile, true));
						} else if(realFile.isFile()){
							files.add(realFile);
						} else {
							throw new FileNotFoundException("Real file " + realFile.getCanonicalPath() + " not exist");
						}

					} else
						files.add(file);
				}
			}
			for (File fdir : dirs) {
				//files.add(fdir.getAbsoluteFile());
				files.addAll(getFilterFileList(fdir, topDir, excludeFilters, isGetSymbolicLink));
			}
		}

		return files;
	}

	/**Add files and directories the symbolicFile links to to the snap.xml report
	 * @param parent
	 * @param fileName
	 * 			relative path of the file
	 * @param symbolicFile
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws DOMException
	 */
	public static void addSymbolicFileToReport(Element parent, String fileName, File symbolicFile) throws IOException, DOMException, NoSuchAlgorithmException{
		String location = null, realFileName = null;
		Element file = parent.getOwnerDocument().createElement("file");
		try {
			realFileName = new WindowsShortcut(symbolicFile).getRealFilename();

		} catch (ParseException e) {
			FeatureUtil.logMsg("Can't parse symbolic link " + symbolicFile.getAbsolutePath() + " . Skip this file!");
			file.setAttribute("name", fileName);
			Date date = new Date(symbolicFile.lastModified());
			file.setAttribute("lastModified", dateFormat.format(date));
			file.setAttribute("hash", FileUtil.calcHash(symbolicFile, DEFAULT_HASH));
			file.setAttribute("size", String.valueOf(symbolicFile.length()));
			return;
		}

		File f = new File(realFileName);
		if(f.exists()){
			location = f.getAbsolutePath();
		}else return;

		if(f.isDirectory())
		{
			for(File rf : getFileList(f,false)){
				Element fileSym =  parent.getOwnerDocument().createElement("file");
				String symPath = fileName.concat(slash + getRelativePath(f.getAbsolutePath(), rf));

				if(rf.getAbsolutePath().endsWith(".lnk")){
					addSymbolicFileToReport(parent, symPath, rf);
				}else {
					fileSym.setAttribute("name", symPath);
					Date date = new Date(rf.lastModified());
					fileSym.setAttribute("location", rf.getAbsolutePath());
					fileSym.setAttribute("lastModified", dateFormat.format(date));
					fileSym.setAttribute("hash", FileUtil.calcHash(rf, DEFAULT_HASH));
					fileSym.setAttribute("size", String.valueOf(rf.length()));
					parent.appendChild(fileSym);
				}
			}
			return;
		}
		else
		{
			file.setAttribute("location", location);
			file.setAttribute("name", fileName);
			Date date = new Date(f.lastModified());
			file.setAttribute("lastModified", dateFormat.format(date));
			file.setAttribute("hash", FileUtil.calcHash(f, DEFAULT_HASH));
			file.setAttribute("size", String.valueOf(f.length()));
		}
	}

	/**All files and directories the symbolicFile links to will be added to snap.zip
	 * @param zip
	 * @param fileName
	 * 		name of the file in zip module. Different from symbolicFile name.
	 * @param symbolicFile
	 * 		Symbolic file to parse
	 * @throws IOException
	 */
	public static void addSymbolicFileToZip(ZipOutputStream zip, String fileName, File symbolicFile) throws IOException{
		String realFileName = "";
		try {
			realFileName = new WindowsShortcut(symbolicFile).getRealFilename();
		} catch (ParseException e) {
			FeatureUtil.logMsg("Can't parse symbolic link " + symbolicFile.getAbsolutePath() + " . Skip this file!");
			addFileToZip(zip,fileName.toString(),symbolicFile);
		}

		File f = new File(realFileName);
		if(!f.exists())  return;

		if(f.isDirectory()){
			String rfName = fileName.toString();
			for(File rf : getFileList(f, false)){

				String name = rfName.concat(rf.getAbsolutePath().substring(f.getAbsolutePath().length()));

				if(rf.getAbsolutePath().endsWith(".lnk")){
					addSymbolicFileToZip(zip, name, rf);
				} else
					addFileToZip(zip, name, rf);
			}
		} else {
			addFileToZip(zip, fileName, f);
		}
	}

	public static void addFileToZip(ZipOutputStream zip, String fileName, File f) throws IOException {
		byte buf[] = new byte[8192];
		BufferedInputStream fin = null;
		int ret = 0;
		try {
			ZipEntry entry = new ZipEntry(fileName);
			// for empty directory
			if (!f.isFile()) {
				zip.putNextEntry(entry);
			} else {
				fin = new BufferedInputStream(new FileInputStream(f));
				zip.putNextEntry(entry);
				while ((ret = fin.read(buf)) != -1) {
					zip.write(buf, 0, ret);
				}
			}

		} catch(IOException e) {
			FeatureUtil.logMsg("Can't read file " + f.getAbsolutePath() + ", this file may not exist or you don't have enough permission to access this file. Skip it!");
		} finally {
			if (fin != null) fin.close();
			zip.closeEntry();
		}
	}

	public static String getRelativePath(String top, File f) throws IOException {
		String path = f.getAbsolutePath().substring(top.length());
		if (path.length() > 0)
			path = path.substring(1); // remove leading / or \
		return path;
	}

	/**
	 * Parse the content of the given file as an XML document and return a new
	 * DOM object. Support namespace handling
	 * 
	 * The parser in this case turns on some options, only use this function
	 * when creating snapshot, do not use in common cases.
	 * 
	 * @param xmlFile
	 *            The file containing the XML to parse.
	 * @return A new DOM Document object.
	 * @throws ParserConfigurationException
	 */
	public static Document createDocumentFromFile(File xmlFile) throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setIgnoringElementContentWhitespace(true);

		DocumentBuilder builder = dbf.newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicID, String systemID) {

				return new InputSource(new StringReader(""));
			}
		});

		Document document = null;
		try {
			document = builder.parse(xmlFile);
		} catch (SAXException e) {
			FeatureUtil.logMsg(e.getMessage() + " .Can't parse " + xmlFile.getAbsolutePath());
		} catch (IOException e) {
			FeatureUtil.logMsg(e.getMessage() + "." + xmlFile.getAbsolutePath() + " is not a valid file!");
		}

		return document;
	}

	/**Parse the content of the given file as an XML document and return a new
	 * DOM object. Do not Support namespace handling
	 * @param fileName
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createDocument(String fileName) throws ParserConfigurationException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;

		builder = factory.newDocumentBuilder();

		Document doc = null;
		try {
			doc = builder.parse(new File(fileName));
		} catch (IOException e) {
			FeatureUtil.logMsg(fileName + " is not a valid file");
		} catch (SAXException e) {
			FeatureUtil.logMsg(fileName + " does not have a valid xml format");
		}

		if(doc != null && doc.getDocumentElement() != null)
			doc.getDocumentElement().normalize();

		return doc;
	}

	public static List<Node> getChildElements(Node node) {

		List<Node> l = new ArrayList<Node>();

		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				l.add(n);
			}
		}

		return l;
	}

	public static HashMap<String, String> getNodeAttributes(Node node) {
		HashMap<String, String> h = new HashMap<String, String>();
		if (node instanceof Element) {
			NamedNodeMap attrs = node.getAttributes();
			for (int i = 0; i < attrs.getLength(); i++) {
				Attr attr = (Attr) attrs.item(i);
				h.put(attr.getName(), attr.getValue());
			}
		}
		return h;

	}

	public static boolean hasChildNodes(Node node) {

		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.ELEMENT_NODE)
				return true;
		}

		return false;

	}

	public static void updateParentState(Node node) {
		// TODO Auto-generated method stub
		Node parent = node.getParentNode();
		while ((parent != null)
				&& !((Element) parent).getNodeName().equals("snapshot")) {
			((Element) parent).setAttribute("state", "different");
			parent = parent.getParentNode();
		}

	}

	public static String getElementText(Element e) {
		StringBuffer buf = new StringBuffer();
		for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() == Node.TEXT_NODE
					|| n.getNodeType() == Node.CDATA_SECTION_NODE) {
				buf.append(n.getNodeValue());
			}
		}
		return buf.toString();
	}
}
