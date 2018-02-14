package com.uc4.ara.feature.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Useful class for dynamically changing the classpath, adding classes during
 * runtime.
 * 
 * @author unknown
 */
public class ClasspathHacker {

	/**
	 * Adds a file to the classpath
	 * 
	 * @param f
	 *            the file to be added
	 * @throws IOException
	 */
	public static void addFile(File f) throws IOException {
		URL jarURL = new URL("jar", "", "file:" + f.getCanonicalPath() + "!/");
		addURL(jarURL);
	}

	/**
	 * Adds a files to the classpath
	 * 
	 * @param files
	 *            list of files to be added
	 * @throws IOException
	 */
	public static void addFile(File[] files) throws IOException {
		for (File f : files) {
			addFile(f);
		}
	}

	/**
	 * Adds the content pointed by the URL to the classpath.
	 * 
	 * @param u
	 *            the URL pointing to the content to be added
	 * @throws IOException
	 */
	private static void addURL(URL u) throws IOException {
		URLClassLoader sysLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		try {
			Method method = URLClassLoader.class.getDeclaredMethod("addURL",
					new Class[] { java.net.URL.class });
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
		} catch (Throwable t) {
			throw new IOException("Could not add URL " + u.toString()
					+ " to system classloader");
		}
	}
}
