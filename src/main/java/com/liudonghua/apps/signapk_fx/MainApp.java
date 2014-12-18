package com.liudonghua.apps.signapk_fx;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	private Locale locale;
	private ResourceBundle i18nBundle;
	private Stage primaryStage;
	
    String themeDefault;
    String themeBlack;

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
	    try {
			themeDefault = new File(rootPath, "resources/theme/default.css").toURL().toExternalForm();
		    themeBlack = new File(rootPath, "resources/theme/black.css").toURL().toExternalForm();
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
	    	theme = themeDefault;
	    }
	    return theme;
	}
	
	public void setTheme(String theme) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    prefs.put("theme", theme);
	}

	private String getRootDirectory() {
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
		return ResourceBundle.getBundle("ApplicationResources", locale, loader);
	}
	
}
