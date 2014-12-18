package com.liudonghua.apps.signapk_fx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomDialogController extends Stage  implements Initializable {

    @FXML
    private Text dialogContent;
    @FXML
    private Text dialogHeader;
    @FXML
    private Button ok;
    @FXML
    private Button cancel;
    
    private String header, content;
    
    private DialogResult result = DialogResult.NONE;
    
	public CustomDialogController(String title, String header, String content, Stage ownerStage) {
		setTitle(title);
		this.header = header;
		this.content = content;
		getIcons().add(new Image("file:resources/images/art-icon-32.png"));
		setResizable(false);
		initModality(Modality.WINDOW_MODAL);
		initOwner(ownerStage);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CustomDialog.fxml"));
		fxmlLoader.setController(this);
		try {
			Scene scene = new Scene((Parent) fxmlLoader.load());
			setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dialogHeader.setText(header);
		dialogContent.setText(content);
	}

    @FXML
    void onOkButtonClicked(ActionEvent event) {
    	result = DialogResult.OK;
    	close();
    }

    @FXML
    void onCancelButtonClicked(ActionEvent event) {
    	result = DialogResult.CANCEL;
    	close();
    }
    
    public DialogResult getResult() {
    	return result;
    }
    
    static enum DialogResult {
    	NONE, CANCEL, OK;
    }

}
