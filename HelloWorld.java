package JustHelp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class UnRar {

	public static final String separator = File.separator; // 分隔符

	public static void main(String[] args) throws Exception {

		traverseFolder2("\\\\192.168.14.80\\zet-2018-05-23\\File");

		// UnRar unRar = new UnRar();
		// unRar.unrar(new File("F:\\MX\\s6p4910a0\\s6p4910a0.rar"), new File(
		// "F:\\MX\\s6p4910a0"));
	}

	public static String unrar(File sourceRar, File destDir) throws Exception {

		Archive archive = null;
		FileOutputStream fos = null;
		System.out.println("Starting...");

		try {
			archive = new Archive(sourceRar);
			FileHeader fh = archive.nextFileHeader();

			int count = 0;
			File destFileName = null;
			while (fh != null) {
				String compressFileName = "";
				System.out.println(fh.isUnicode());

				// 判断文件路径是否有中文
				if (existZH(fh.getFileNameW())) {

					System.out.println((++count) + ") " + fh.getFileNameW());
					compressFileName = fh.getFileNameW().trim();
				} else {
					System.out.println((++count) + ") "
							+ fh.getFileNameString());
					compressFileName = fh.getFileNameString().trim();
				}

				destFileName = new File(destDir.getAbsolutePath() + "/"
						+ compressFileName);

				if (fh.isDirectory()) {

					if (!destFileName.exists()) {
						destFileName.mkdirs();
					}

					fh = archive.nextFileHeader();
					continue;

				}

				if (!destFileName.getParentFile().exists()) {

					destFileName.getParentFile().mkdirs();
				}

				fos = new FileOutputStream(destFileName);
				archive.extractFile(fh, fos);
				fos.close();
				fos = null;
				fh = archive.nextFileHeader();

			}

			archive.close();
			archive = null;
			System.out.println("Finished !");
			return destFileName.getAbsolutePath();

		} catch (Exception e) {
			throw e;
		} finally {

			if (fos != null) {

				try {
					fos.close();
					fos = null;
				} catch (Exception e) {

				}
			}

			if (archive != null) {

				try {

					archive.close();
					archive = null;

				} catch (Exception e) {

				}
			}

		}
	}

	// 判断路径是否带中文

	public static boolean existZH(String str) {

		String regEx = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		while (m.find()) {

			return true;
		}
		return false;
	}

	// 解压文件
	public static void traverseFolder2(String path) throws Exception {

		try {

			String wordString = "\\..*rar"; // 替换汉字和空格
			Pattern pattern = Pattern.compile(wordString);

			File file = new File(path);
			if (file.exists()) {
				File[] files = file.listFiles();
				if (files.length == 0) {
					// System.out.println("文件夹是空的!");
					// return;
				} else {
					for (File file2 : files) {

						Matcher matcher = pattern.matcher(file2.getName());
						if (matcher.find()) {
							String resultString = unrar(
									new File(file2.getAbsolutePath()),
									new File(file2.getParent()));
							System.out.println("解压成功！解压前的文件目录为:"
									+ file2.getAbsolutePath() + "解压后的为: "
									+ resultString);
						}

						if (file2.isDirectory()) {
							// System.out.println("文件夹:" +
							// file2.getAbsolutePath());
							traverseFolder2(file2.getAbsolutePath());
						} else {
							// System.out.println("文件:" +
							// file2.getAbsolutePath());
						}
					}
				}
			} else {
				System.out.println("文件不存在!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
