package com.liudonghua.apps.signapk_fx;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Locale locale;
	private ResourceBundle i18nBundle;
	private Stage primaryStage;
	private String resourceBaseName = "ApplicationResources";
	Map<String, String> themes;
	Map<String, Locale> locales;
	Map<String, CertInfo> certs;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		initialize();
		showMainPane();
	}

	void initialize() {
		String rootPath = getRootDirectory();
		setCurrentDirectory(rootPath);
		locale = getLocale();
		i18nBundle = loadResourceBundle(new File(rootPath, "resources/locales").getAbsolutePath(), locale);
		setupLocales(rootPath);
		setupThemes(rootPath);
		setupCerts(rootPath);
	}

	private void setupCerts(String rootPath) {
		certs = new TreeMap<>();
		File certDir = new File(rootPath, "resources/certs/");
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				if(fileName.length() == 0 || fileName.lastIndexOf(".") == -1) {
					return false;
				}
				String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
				String correspondCertFilePath = file.getParent() + File.separator  + getFileNameExcludeExtension(file) + ".x509.pem";
				if(fileName.length() > 0 && fileExtension.toLowerCase().equals("pk8") && new File(correspondCertFilePath).exists()) {
					return true;
				}
				return false;
			}
		};
		for(File resourceFile : certDir.listFiles(fileFilter)) {
			CertInfo certInfo = parseCertInfo(resourceFile);
			certs.put(certInfo.getId(), certInfo);
		}
	}

	private CertInfo parseCertInfo(File resourceFile) {
		String certInfoId = getFileNameExcludeExtension(resourceFile);
		String publicKeyFilePath = resourceFile.getParent() + File.separator  + getFileNameExcludeExtension(resourceFile) + ".x509.pem";
		return new CertInfo(certInfoId, publicKeyFilePath, resourceFile.getPath());
	}

	private void setupLocales(String rootPath) {
		locales = new TreeMap<>();
		File themeDir = new File(rootPath, "resources/locales/");
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				if(fileName.length() == 0 || fileName.lastIndexOf(".") == -1) {
					return false;
				}
				String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
				if(fileName.length() > 0 && fileName.startsWith(resourceBaseName) && fileExtension.toLowerCase().equals("properties")) {
					return true;
				}
				return false;
			}
		};
		for(File resourceFile : themeDir.listFiles(fileFilter)) {
			Locale locale = parseResourceLocale(resourceFile);
			locales.put(locale.toString(), locale);
		}
	}

	private Locale parseResourceLocale(File resourceFile) {
		String fileName = resourceFile.getName();
		// default resource (en_US)
		if (fileName.equals(resourceBaseName + ".properties")) {
			return Locale.US;
		} else {
			int lastDotPosition = fileName.lastIndexOf(".");
			int startLocalePostfix = resourceBaseName.length() + 1;
			String localePostfixString = fileName.substring(startLocalePostfix,
					lastDotPosition);
			String[] localePostfix = localePostfixString.split("_");
			if (localePostfix.length == 1) {
				return new Locale(localePostfix[0]);
			} else if (localePostfix.length == 2) {
				return new Locale(localePostfix[0], localePostfix[1]);
			} else if (localePostfix.length == 3) {
				return new Locale(localePostfix[0], localePostfix[1], localePostfix[2]);
			} else {
				return null;
			}
		}
	}

	private void setupThemes(String rootPath) {
		themes = new TreeMap<>();
		File themeDir = new File(rootPath, "resources/theme/");
		FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String fileName = file.getName();
				if(fileName.length() == 0 || fileName.lastIndexOf(".") == -1) {
					return false;
				}
				String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
				if(fileName.length() > 0 && fileExtension.toLowerCase().equals("css")) {
					return true;
				}
				return false;
			}
		};
	    try {
			for(File themeFile : themeDir.listFiles(fileFilter)) {
				themes.put(getFileNameExcludeExtension(themeFile), themeFile.toURL().toExternalForm());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	void showMainPane() {
		MainPaneController controller = new MainPaneController(this);
		controller.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public ResourceBundle getI18nBundle() {
		return i18nBundle;
	}
	
	public void setI18nBundle() {
		i18nBundle = ResourceBundle.getBundle("com.liudonghua.apps.signapk_fx.ApplicationResources", locale);
	}
	
	public Locale getLocale() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String lang = prefs.get("locale_lang", null);
	    if(lang == null) {
	    	return Locale.getDefault();
	    }
	    String country = prefs.get("locale_country", "");
	    return new Locale(lang, country);
	}
	
	public void setLocale(Locale locale) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    prefs.put("locale_lang", locale.getLanguage());
	    prefs.put("locale_country", locale.getCountry());
	}
	
	public Locale getCurrentInUseLocale() {
		return locale;
	}
	
	public void setCurrentInUseLocale(Locale locale) {
		this.locale = locale;
	}
	
	public String getTheme() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String theme = prefs.get("theme", null);
	    if(theme == null) {
	    	theme = themes.get("default");
	    }
	    return theme;
	}
	
	public void setTheme(String theme) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    prefs.put("theme", theme);
	}
	
	public String getCert() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    return prefs.get("cert", "testkey");
	}
	
	public void setCert(String cert) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    prefs.put("cert", cert);
	}

	public String getRootDirectory() {
		String executingRootPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		String executingDirPath = new File(executingRootPath).getParent();
		return executingDirPath;
	}
	
	public static boolean setCurrentDirectory(String directory_name) {
		boolean result = false;
		File directory;

		directory = new File(directory_name).getAbsoluteFile();
		if (directory.exists() || directory.mkdirs()) {
			result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
		}

		return result;
	}
	
	private ResourceBundle loadResourceBundle(String resourceRootPath, Locale locale) {
		File file = new File(resourceRootPath);
		ClassLoader loader = null;
		try {
			URL[] urls = {file.toURI().toURL()};
			loader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResourceBundle.getBundle(resourceBaseName, locale, loader);
	}
	
	private String getFileNameExtension(File file) {
		String fileName = file.getName();
		if(fileName.length() == 0 || fileName.lastIndexOf(".") == -1) {
			return null;
		}
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
	
	private String getFileNameExcludeExtension(File file) {
		String fileName = file.getName();
		if(fileName.length() == 0) {
			return null;
		}
		int lastDotPosition = fileName.lastIndexOf(".");
		if(lastDotPosition == -1) {
			return fileName;
		}
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
}
