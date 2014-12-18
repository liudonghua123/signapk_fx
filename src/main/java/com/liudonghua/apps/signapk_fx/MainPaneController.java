package com.liudonghua.apps.signapk_fx;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.android.signapk.SignApk;


public class MainPaneController extends Stage  implements Initializable{

    @FXML
    private Button selectInputFileButton;
    @FXML
    private Button signApkButton;
    @FXML
    private Button selectOutputDirButton;
    @FXML
    private CheckBox customOutputDirCheckBox;
    @FXML
    private TextField outputDirTextField;
	@FXML
    private ProgressBar statusProgressBar;
    @FXML
    private Label statusLabel;
    @FXML
    private TextField InputFileTextField;
    @FXML
    private Menu languageMenu;
    @FXML
    private ToggleGroup languageRadioMenuItemGroup;
    @FXML
    private RadioMenuItem languageEnglishRadioMenuItem;
    @FXML
    private RadioMenuItem languageChinaRadioMenuItem;
    @FXML
    private Menu themeMenu;
    @FXML
    private ToggleGroup themeRadioMenuItemGroup;
    @FXML
    private RadioMenuItem themeDefaultRadioMenuItem;
    @FXML
    private RadioMenuItem themeBlackRadioMenuItem;
    
    private Scene scene;
    
    private MainApp mainApp;
	private ResourceBundle i18nBundle;
	private String inputFilePath;
	private String outputFilePath;
	private String outputDirPath;
	private String[] STATUS_LABEL_TEXT;
	private String[] SUPPORT_FILE_EXTENSION = {"apk", "jar", "zip"};

    public MainPaneController(MainApp mainApp) {
    	this.mainApp = mainApp;
    	this.i18nBundle = mainApp.getI18nBundle();
		setTitle(i18nBundle.getString("app.title"));
		getIcons().add(new Image("file:resources/images/art-icon-32.png"));
		setWidth(400);
		setHeight(275);
		setResizable(false);
		initModality(Modality.WINDOW_MODAL);
		initOwner(mainApp.getPrimaryStage());
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPane.fxml"), mainApp.getI18nBundle());
		fxmlLoader.setController(this);
		try {
			scene = new Scene((Parent) fxmlLoader.load());
			scene.getStylesheets().add(mainApp.getTheme());
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}

		STATUS_LABEL_TEXT = new String[] {
				i18nBundle.getString("app.status.ready"),
				i18nBundle.getString("app.status.file_prepared"),
				i18nBundle.getString("app.status.dir_prepared"),
				i18nBundle.getString("app.status.signing"),
				i18nBundle.getString("app.status.sign_success"),
				i18nBundle.getString("app.status.sign_failed"),
				i18nBundle.getString("app.status.file_not_supported"),
		};
		updateBottomStatus(STATUS.READY);
	}

	public void initialize(URL location, ResourceBundle resources) {
		languageMenu.getItems().clear();
		Locale localInPref = mainApp.getLocale();
		for(Entry<String, Locale> localeEntry : mainApp.locales.entrySet()) {
			RadioMenuItem languageMenuItem = new RadioMenuItem(localeEntry.getKey());
			if(localeEntry.getValue().equals(localInPref)) {
				languageMenuItem.setSelected(true);
			}
			languageMenuItem.setToggleGroup(languageRadioMenuItemGroup);
			languageMenu.getItems().add(languageMenuItem);
			languageMenuItem.setOnAction(event -> onChangeLanguage(event));
		}
		
		themeMenu.getItems().clear();
		String themeInPref = mainApp.getTheme();
		for(Entry<String, String> themeEntry : mainApp.themes.entrySet()) {
			RadioMenuItem themeMenuItem = new RadioMenuItem(themeEntry.getKey());
			if(themeEntry.getValue().equals(themeInPref)) {
				themeMenuItem.setSelected(true);
			}
			themeMenuItem.setToggleGroup(themeRadioMenuItemGroup);
			themeMenu.getItems().add(themeMenuItem);
			themeMenuItem.setOnAction(event -> onChangeTheme(event));
		}
	}

    @FXML
    void onDragDropped(DragEvent event) {
    	Dragboard dragBoard = event.getDragboard();
		boolean dragSuccess = false;
		if (dragBoard.hasFiles()) {
			dragSuccess = true;
			// get first drag & droped file
			File file = dragBoard.getFiles().get(0);
			if(canProcess(file)) {
				processInputFile(file);
			}
			else {
				updateBottomStatus(STATUS.FILE_NOT_SUPPORTED);
			}
		}
    }

	@FXML
    void onDragEntered(DragEvent event) {
    	
    }

    @FXML
    void onDragExisted(DragEvent event) {
    	
    }

    @FXML
    void onDragOver(DragEvent event) {
		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
		event.consume();
    }

    @FXML
    void onSelectInputFileClicked(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
    	fileChooser.setTitle(i18nBundle.getString("app.dialog.select_input_file.title"));
    	String initialOpenLocation = null;
		try {
			String executingFilePath = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String executingDirPath = new File(executingFilePath).getParent();
			initialOpenLocation = URLDecoder.decode(executingDirPath, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(initialOpenLocation == null) {
			initialOpenLocation = System.getProperty("user.home");
		}
        fileChooser.setInitialDirectory(new File(initialOpenLocation));
        setSupportExtension(fileChooser);
        File file = fileChooser.showOpenDialog(this);
        processInputFile(file);
    }

	private void setSupportExtension(FileChooser fileChooser) {
		for(String extension : SUPPORT_FILE_EXTENSION) {
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extension.toUpperCase(), "*." + extension));
		}
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
	}

	private void processInputFile(File file) {
		if (file != null) {
        	inputFilePath = file.getPath();
        	InputFileTextField.setText(inputFilePath);
        	if(!customOutputDirCheckBox.isSelected()) {
            	outputDirPath = file.getParent();
            	outputDirTextField.setText(outputDirPath);
        	}
        	signApkButton.setDisable(false);
    		updateBottomStatus(STATUS.FILE_PREPARED);
        }
	}

	@FXML
    void onSelectOutputDirClicked(ActionEvent event) {
    	DirectoryChooser directoryChooser = new DirectoryChooser();
    	directoryChooser.setTitle(i18nBundle.getString("app.dialog.select_output_dir.title"));
    	File initialDirectory = new File(System.getProperty("user.home"));
    	if(inputFilePath != null) {
        	File inputFile = new File(inputFilePath);
        	if(inputFile.exists()) {
        		initialDirectory = inputFile.getParentFile();
        	}
    	}
    	directoryChooser.setInitialDirectory(initialDirectory);
    	File dir = directoryChooser.showDialog(this);
    	if(dir != null) {
    		outputDirPath = dir.getPath();
    		outputDirTextField.setText(outputDirPath);
    		updateBottomStatus(STATUS.DIR_PREPARED);
    	}
    }

    @FXML
	void onCustomOutputDirChecked(ActionEvent event) {
		CheckBox checkBox = (CheckBox) event.getSource();
		outputDirTextField.setDisable(!checkBox.isSelected());
		selectOutputDirButton.setDisable(!checkBox.isSelected());
	}

    @FXML
    void onSignFileClicked(ActionEvent event) {
    	// check if the file is signed already
    	outputFilePath = outputDirPath + "/" + getSignedFileName(inputFilePath);
		if(new File(outputFilePath).exists()) {
			boolean isContinue = showFileMayProcessedConfirmDialog();
			if(!isContinue) {
				return;
			}
		}
		updateBottomStatus(STATUS.SIGNING);
    	new Thread(() -> {
    		boolean isSuccess = SignApk.signApk(false, "resources/testkey.x509.pem", "resources/testkey.pk8", inputFilePath, outputFilePath);
    		Platform.runLater(() -> {
    			if(isSuccess) {
    	    		updateBottomStatus(STATUS.SIGN_SUCCESS);
    			}
    			else {
    	    		updateBottomStatus(STATUS.SIGN_FAILED);
    			}
    		});
    	}).start();
    }

	private boolean showFileMayProcessedConfirmDialog() {
		boolean isProceed = true;
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(i18nBundle.getString("app.dialog.file_exist_confirm.title"));
		alert.setHeaderText(i18nBundle.getString("app.dialog.file_exist_confirm.header_text"));
		alert.setContentText(i18nBundle.getString("app.dialog.file_exist_confirm.content_text"));

		Optional<ButtonType> result = alert.showAndWait();
		isProceed = (result.get() == ButtonType.OK);
		return isProceed;
	}

    @FXML
    void onExit(ActionEvent event) {
    	Platform.exit();
    }

    @FXML
    void onChangeTheme(ActionEvent event) {
    	RadioMenuItem item = (RadioMenuItem) event.getSource();
        String themeSelected = mainApp.themes.get(item.getText());
        scene.getStylesheets().clear();
        scene.getStylesheets().add(themeSelected);
        mainApp.setTheme(themeSelected);
    }

    @FXML
    void onChangeLanguage(ActionEvent event) {
    	RadioMenuItem item = (RadioMenuItem) event.getSource();
    	Locale localeSelected = mainApp.locales.get(item.getText());
    	if(!mainApp.getCurrentInUseLocale().equals(localeSelected)) {
    		updateLanguage(localeSelected);
    	}
    	
    }

    private void updateLanguage(Locale locale) {
		mainApp.setLocale(locale);
		close();
		mainApp.initialize();
		mainApp.showMainPane();
	}

	@FXML
    void onAbout(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(i18nBundle.getString("app.dialog.about.title"));
		alert.setHeaderText(null);
		alert.setContentText(i18nBundle.getString("app.dialog.about.content_text"));

		alert.showAndWait();
    }
    
    enum STATUS {
    	READY, FILE_PREPARED, DIR_PREPARED, SIGNING, SIGN_SUCCESS, SIGN_FAILED, FILE_NOT_SUPPORTED;
    }
    
    private void updateBottomStatus(STATUS status) {
    	int ordinal = status.ordinal();
		statusProgressBar.setProgress(0);
    	switch(status) {
    	case READY:
    		break;
    	case FILE_PREPARED:
    		break;
    	case DIR_PREPARED:
    		break;
    	case SIGNING:
    		statusProgressBar.setProgress(-1);
    		break;
    	case SIGN_SUCCESS:
    		statusProgressBar.setProgress(1);
    		break;
    	case SIGN_FAILED:
    		break;
    	case FILE_NOT_SUPPORTED:
    		break;
    	}
    	statusLabel.setText(STATUS_LABEL_TEXT[ordinal]);
    }
    

    private String getSignedFileName(String inputFilePath) {
    	File file = new File(inputFilePath);
		String dir = file.getParent();
		String fileName = file.getName();
		int lastDotPosition = fileName.lastIndexOf(".");
		return fileName.substring(0, lastDotPosition) + "_signed" + fileName.substring(lastDotPosition);
	}

    private boolean canProcess(File file) {
    	String fileName = file.getName();
    	String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    	for(String extension : SUPPORT_FILE_EXTENSION) {
    		if(extension.equals(fileExtension)) {
    			return true;
    		}
    	}
    	return false;
	}

}
