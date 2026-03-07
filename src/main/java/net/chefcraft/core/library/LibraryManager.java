package net.chefcraft.core.library;

import net.chefcraft.core.ChefCore;
import net.chefcraft.core.PluginInstance;
import net.chefcraft.core.database.ChefDriver;
import net.chefcraft.core.exception.UnsupporttedVersionException;
import net.chefcraft.core.server.ReflectorVersion;
import net.chefcraft.reflection.AbstractReflections;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class LibraryManager {
	
	public static final String REFLECTOR_MAIN_CLASS_PATH = "net.chefcraft.reflector.MainReflector";
	
	public static void loadSQLDriverFromExternalSources(PluginInstance plugin, ClassLoader parent, URL url, String driverClass, String targetName) {
		try {
    		File file = new File(plugin.getDataFolder().getAbsolutePath(), "libs");
    		if (!file.exists()) {
    			file.mkdirs();
    		}
    		
    		Path jdbcPath = LibraryManager.download(plugin, Path.of(file.getAbsolutePath()), url, targetName);
			
			try {
				Class<?> driverClazz = LibraryManager.loadClass(jdbcPath, driverClass, parent);
				Driver driverInstance = (Driver) driverClazz.getDeclaredConstructor().newInstance();
				DriverManager.registerDriver(new ChefDriver(driverInstance));
				plugin.getLogger().log(Level.INFO, "Driver \"" + driverClass + "\" registered!");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Path download(PluginInstance plugin, Path toFolder, URL url, String targetName) throws IOException {
        Path jarPath = toFolder.resolve(targetName);

        if (!Files.exists(jarPath)) {
            plugin.getLogger().log(Level.INFO, "Downloading resource: " + url.toString());
            try (InputStream in = url.openStream()) {
                Files.copy(in, jarPath);
                plugin.getLogger().log(Level.INFO, "Resource downloaded: " + url.getFile());
            } catch (Exception x) {
            	plugin.getLogger().log(Level.SEVERE, "Failed to download: " + url.toString() + " | Exception: " + x.toString());
            }
        }
        return jarPath;
    }
	
	public static Class<?> loadClass(Path jarPath, String className, ClassLoader parent) throws Exception {
		Class<?> clazz = null;
		try {
			URLClassLoader loader = null;
			try {
				loader = URLClassLoader.newInstance(new URL[] {jarPath.toUri().toURL()}, parent);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			clazz = loader.loadClass(className);
		} catch (ClassNotFoundException x) {
			x.printStackTrace();
		}
		return clazz;
	}
	
	public static Class<?> loadClass(File file, String classPath, ClassLoader parent) {
		try {
			return loadClass(Path.of(file.toURI()), classPath, parent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static AbstractReflections newServerReflector() throws UnsupporttedVersionException {
		AbstractReflections reflector = null;
		try {
			reflector = ReflectorVersion.current().tryDetectAndLoad();
		} catch (Exception x) {
			throw new UnsupporttedVersionException("An error occurred while loading reflections. The version of the plugin does not support this server version!", x);
		}
		
		if (reflector != null) {
			ChefCore.getInstance().sendPlainMessage("<white>The system uses <green>" + ReflectorVersion.current().getReflectorVersion() + "<white> reflector.");
		}
		return reflector;
	}
	
	@SuppressWarnings("unused")
	private static byte[] getBytes(String javaFileName, String jar) throws IOException {
        try (JarFile jarFile = new JarFile(jar)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                if (entry.getName().endsWith(".class") &&
                        entry.getName().contains(javaFileName.substring(0, javaFileName.lastIndexOf(".")))) {
                    try (InputStream inputStream = jarFile.getInputStream(entry)) {
                        return getBytes(inputStream);
                    } catch (IOException ioException) {
                        System.out.println("Could not obtain class entry for " + entry.getName());
                        throw ioException;
                    }
                }
            }
        }
        throw new IOException("File not found");
    }

    private static byte[] getBytes(InputStream is) throws IOException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
        {
            byte[] buffer = new byte[0xFFFF];
            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);
            os.flush();
            return os.toByteArray();
        }
    }
}
