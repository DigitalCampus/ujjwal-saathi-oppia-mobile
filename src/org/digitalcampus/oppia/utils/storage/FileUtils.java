/* 
 * This file is part of OppiaMobile - https://digital-campus.org/
 * 
 * OppiaMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OppiaMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OppiaMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package org.digitalcampus.oppia.utils.storage;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;
import android.webkit.MimeTypeMap;




import org.digitalcampus.oppia.application.MobileLearning;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

	public static final String TAG = FileUtils.class.getSimpleName();
	public static final int BUFFER_SIZE = 1024;
	
	public static final String APP_ROOT_DIR_NAME = "digitalcampus";
	
	public static final String APP_COURSES_DIR_NAME = "modules";
	public static final String APP_DOWNLOAD_DIR_NAME = "download";
	public static final String APP_MEDIA_DIR_NAME = "media";

    public static int BUFFER_SIZE_CONFIG = 1024;

    private static StorageAccessStrategy storageStrategy;
    public static void setStorageStrategy(StorageAccessStrategy strategy){
        storageStrategy = strategy;
    }
    public static StorageAccessStrategy getStorageStrategy() {
        return storageStrategy;
    }

	public static boolean createDirs(Context ctx) {

        if (!storageStrategy.isStorageAvailable(ctx)){
            Log.d(TAG, "Storage not available");
            return false;
        }

        //BUFFER_SIZE_CONFIG = 21;

		String[] dirs = { FileUtils.getCoursesPath(ctx), FileUtils.getMediaPath(ctx), FileUtils.getDownloadPath(ctx) };

		for (String dirName : dirs) {
			File dir = new File(dirName);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.d(TAG, dirName);
					Log.d(TAG, "can't mkdirs");
					return false;
				}
			} else {
				if (!dir.isDirectory()) {
					Log.d(TAG, "not a directory");
					return false;
				}
			}
		}
		//After creating the necessary folders, we create the .nomedia file
		createNoMediaFile(ctx);
		
		return true;
	}
	
	public static String getStorageLocationRoot(Context ctx){
		return storageStrategy.getStorageLocation(ctx);
	}
	
	public static String getCoursesPath(Context ctx){
		return getStorageLocationRoot(ctx) + File.separator + "modules" + File.separator;
	}
	
	public static String getDownloadPath(Context ctx){
		return getStorageLocationRoot(ctx) + File.separator + "download" + File.separator;
	}
	
	public static String getMediaPath(Context ctx){
		return getStorageLocationRoot(ctx) + File.separator + "media" + File.separator;
	}
	
	// This function converts the zip file into uncompressed files which are
	// placed in the destination directory
	// destination directory should be created first
	public static boolean unzipFiles(String srcDirectory, String srcFile, String destDirectory) {
		try {
			// first make sure that all the arguments are valid and not null
			if (srcDirectory == null) {
				return false;
			}
			if (srcFile == null) {
				return false;
			}
			if (destDirectory == null) {
				return false;
			}
			if (srcDirectory.equals("")) {
				return false;
			}
			if (srcFile.equals("")) {
				return false;
			}
			if (destDirectory.equals("")) {
				return false;
			}
			// now make sure that these directories exist
			File sourceDirectory = new File(srcDirectory);
			File sourceFile = new File(srcDirectory + File.separator + srcFile);
			File destinationDirectory = new File(destDirectory);

			if (!sourceDirectory.exists()) {
				return false;
			}
			if (!sourceFile.exists()) {
				return false;
			}
			if (!destinationDirectory.exists()) {
				return false;
			}

			// now start with unzip process
			BufferedOutputStream dest;
			FileInputStream fis = new FileInputStream(sourceFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				String outputFilename = destDirectory + File.separator + entry.getName();

				createDirIfNeeded(destDirectory, entry);

				int count;

				byte data[] = new byte[BUFFER_SIZE];

				File f = new File(outputFilename);

				// write the file to the disk
				if (!f.isDirectory()) {
					FileOutputStream fos = new FileOutputStream(f);
					dest = new BufferedOutputStream(fos, BUFFER_SIZE);

					// this counter is a hack to prevent getting stuck when
					// installing corrupted or not fully downloaded course
					// packages
					int counter = 0;
					while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
						dest.write(data, 0, count);
						counter++;
						if (counter > 11000) {
							dest.flush();
							dest.close();
							return false;
						}
					}
					// close the output streams
					dest.flush();
					dest.close();
				}
			}

			// we are done with all the files
			// close the zip file
			zis.close();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static void createDirIfNeeded(String destDirectory, ZipEntry entry) {
		String name = entry.getName();

		if (name.contains(File.separator)) {
			int index = name.lastIndexOf(File.separator);
			String dirSequence = name.substring(0, index);
			File newDirs = new File(destDirectory + File.separator + dirSequence);

			// create the directory
			newDirs.mkdirs();
		}
	}

    public static boolean cleanDir(File dir){
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String dirFiles : children) {
                File fileToDelete = new File(dir, dirFiles);
                boolean success = deleteDir(fileToDelete);
                if (!success) {
                    return false;
                }
            }
        }
        return true;
    }

	// Deletes all files and subdirectories under dir.
	// Returns true if all deletions were successful.
	// If a deletion fails, the method stops attempting to delete and returns
	// false.
	public static boolean deleteDir(File dir) {
        if (cleanDir(dir)){
            // The directory is now empty so delete it
            return dir.delete();
        }
        else {
            return false;
        }
	}

	public static boolean mediaFileExists(Context ctx, String filename) {
		File media = new File(FileUtils.getMediaPath(ctx) + filename);
		Log.d(TAG,"Looking for: " + FileUtils.getMediaPath(ctx) + filename);
        return media.exists();
	}

    private static long dirSize(File dir){
        if (dir.exists() && dir.isDirectory()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (File file : fileList) {
                if (file.isDirectory()) {
                    result += dirSize(file);
                } else {
                    result += file.length();
                }
            }
            return result;
        }
        return 0;
    }

	public static void createNoMediaFile(Context ctx){
		String storagePath = storageStrategy.getStorageLocation(ctx);
		File dir = new File(storagePath);
		File nomedia = new File(dir, ".nomedia");
		if (!nomedia.exists()){
			boolean fileCreated = false;
			try {
				fileCreated = nomedia.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Log.d(TAG, (fileCreated ? "File .nomedia created in " : "Failed creating .nomedia file in ") + dir.getAbsolutePath());
		}
	}

    @SuppressWarnings("deprecation")
	public static long getAvailableStorageSize(Context ctx){
        String path = getStorageLocationRoot(ctx);
        StatFs stat = new StatFs(path);
        long bytesAvailable;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        }
        else{
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        }
        return bytesAvailable;
    }

    public static long getTotalStorageUsed(Context ctx){
        File dir = new File(getStorageLocationRoot(ctx));
        return dirSize(dir);
    }

	public static void cleanUp(File tempDir, String path) {
		FileUtils.deleteDir(tempDir);

		// delete zip file from download dir
		File zip = new File(path);
		zip.delete();
	}

	public static String readFile(String file) throws IOException {
		FileInputStream fstream = new FileInputStream(file);
        return readFile(fstream);
	}

	public static String readFile(InputStream fileStream) throws IOException {
		DataInputStream in = new DataInputStream(fileStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		StringBuilder stringBuilder = new StringBuilder();
		while ((strLine = br.readLine()) != null) {
			stringBuilder.append(strLine);
		}
		in.close();
		return stringBuilder.toString();
	}

	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	public static boolean supportedMediafileType(String mimeType) {
		if (mimeType == null) {
			return false;
		} else if (mimeType.equals("video/m4v")) {
			return true;
		} else if (mimeType.equals("video/mp4")) {
			return true;
		} else {
			return false;
		}
	}

	public static String getLocalizedFilePath(Activity act, String currentLang, String fileName) {
		String filePath = "www" + File.separator + currentLang + File.separator + fileName;
		try {
			InputStream stream = act.getAssets().open(filePath);
			stream.close();
			return "file:///android_asset/" + filePath;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		String localeFilePath = "www" + File.separator + Locale.getDefault().getLanguage() + File.separator + fileName;
		try {
			InputStream stream = act.getAssets().open(localeFilePath);
			stream.close();
			return "file:///android_asset/" + localeFilePath;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		String defaultFilePath = "www" + File.separator + MobileLearning.DEFAULT_LANG + File.separator + fileName;
		try {
			InputStream stream = act.getAssets().open(defaultFilePath);
			stream.close();
			return "file:///android_asset/" + defaultFilePath;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return "";

	}
}
