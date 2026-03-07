package net.chefcraft.core.util;

/** @since 1.0*/

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.List;

public class FileUtils {
	
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	
	public static void deleteFile(File file) throws IOException {
		if (!file.exists()) {
			throw new NullPointerException("File to be deleted cannot be null!");
		} else {
			deleteFile0(file);
		}
	}
	
	private static void deleteFile0(File file) {
		File[] arr = file.listFiles();
		if (arr != null) {
			for (File in : arr) {
				if (in.isDirectory()) {
					deleteFile0(in);
				} else {
					in.delete();
				}
			}
		}
		file.delete();
	}
	
	public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        if (file == null) {
        	throw new NullPointerException("File is null!");
        } else if (file.isDirectory()) {
        	throw new IOException("File is directory!");
        } else {
        	createDirectory(file);
	        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
	            int read;
	            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
	            while ((read = inputStream.read(bytes)) != -1) {
	                outputStream.write(bytes, 0, read);
	            }
	            
	            inputStream.close();
	            outputStream.close();
	        }
        }
	}
	
	public static void copyFile(@Nonnull File source, @Nonnull File target) {
		copyFile(source, target, DEFAULT_BUFFER_SIZE, null);
	}
	
	public static void copyFile(@Nonnull File source, @Nonnull File target, @Nullable List<String> ignoredFiles) {
		copyFile(source, target, DEFAULT_BUFFER_SIZE, ignoredFiles);
	}
	
	public static void copyFile(@Nonnull File source, @Nonnull File target, @Nonnull final int bufferSize, @Nullable List<String> ignoredFiles) {
		
		try {
            if (ignoredFiles == null || !ignoredFiles.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists())
                        if (!target.mkdirs())
                            throw new IOException("Couldn't create directory!");
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyFile(srcFile, destFile, bufferSize, ignoredFiles);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buff = new byte[bufferSize];
                    int length;
                    while ((length = in.read(buff)) > 0)
                        out.write(buff, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static File createDirectory(String path) throws IOException {
		File file = null;
		if (path == null || path.isEmpty()) {
			throw new NullPointerException("Directory is null!");
		} else {
			file = createDirectory(new File(path));
		}
		return file;
	}
	
	public static File createDirectory(File file) throws IOException {
		if (!file.exists()) {
			String[] parts = file.getAbsolutePath().split("\\\\");
			
			StringBuilder builder = new StringBuilder();
			int j = parts.length - 2;
			
			for (int i = 0; i < parts.length - 1; i++) {
				builder.append(parts[i]);
				
				if (i != j) {
					builder.append(File.separator);
				}
			}
			
			File dir = new File(builder.toString());
			dir.mkdirs();
			file.createNewFile();
    	}
		return file;
	}
}
