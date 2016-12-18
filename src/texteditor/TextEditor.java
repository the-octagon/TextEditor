 /*
 * This file is part of TextEditor.
 * 
 * TextEditor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * TextEditor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with TextEditor.  If not, see <http://www.gnu.org/licenses/>.
 */
package texteditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.IndexRange;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;

public class TextEditor extends Application {

    private static Stage stage;
    private boolean changed = false;
    private String currentFile = "Untitled";
    private FileChooser dialog = new FileChooser();
    private File file;
    private BorderPane root = new BorderPane();
    private TextArea textArea = new TextArea();
    private Scene scene = new Scene(root, 640, 480);
    private static TextEditor t = new TextEditor();
    protected static Class tc = t.getClass();
    private String clipboardText = "";
    private final Clipboard clipboard = Clipboard.getSystemClipboard();
    private final ClipboardContent clipboardContent = new ClipboardContent();
    

    
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        /*String[][] menuSource = {
            {"File", "New","Open","Save","Close"},
            {"Edit", "Cut","Copy","Paste"},
            {"View"},
            {"Help","About","License","Contact"}
        };
        MyMenuBar newMyMenuBar = new MyMenuBar(menuSource);
//        myMenuBar.prefWidthProperty().bind(tc.getStage().widthProperty());        */


        //create menubar and contents
        MenuBar menuBar = new MenuBar();
        
        final Menu fileMenu = new Menu("File");
        final Menu editMenu = new Menu("Edit");
        final Menu viewMenu = new Menu("View");
        final Menu helpMenu = new Menu("Help");
        
        //create menuitems for each menu
        MenuItem fileNew = new MenuItem("New");
        MenuItem fileOpen = new MenuItem("Open");
        MenuItem fileSave = new MenuItem("Save");

        MenuItem editCut = new MenuItem("Cut");
        MenuItem editCopy = new MenuItem("Copy");
        MenuItem editPaste = new MenuItem("Paste");
        
        CheckMenuItem viewWordWrap = new CheckMenuItem("Word Wrap");
            viewWordWrap.setSelected(true);
        MenuItem viewZoomIn = new MenuItem("Zoom In");
        MenuItem viewZoomOut = new MenuItem("Zoom Out");

        MenuItem helpAbout = new MenuItem("About");
        MenuItem helpLicense = new MenuItem("License");
        MenuItem helpContact = new MenuItem("Contact");

        //add menuitems to menus
        fileMenu.getItems().addAll(fileNew, fileOpen, fileSave);
        
        editMenu.getItems().addAll(editCut, editCopy, editPaste);
        
        viewMenu.getItems().addAll(viewWordWrap, viewZoomIn, viewZoomOut);

        helpMenu.getItems().addAll(helpAbout, helpLicense, helpContact);
        
        //stretch menubar accross stage and add menus to menubar
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(editMenu);
        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(helpMenu);

        //listeners for menuitems
        fileNew.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { newFile(); }
        });
        fileOpen.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { openFile(); }
        });
        fileSave.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { saveFile(); }
        });
        editCut.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { cutText(); }
        });
        editCopy.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { copyText(); }
        });
        editPaste.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { pasteText(); }
        });
        viewWordWrap.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { setWordWrap(); }
        });
        
        helpAbout.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { aboutDialog(); }
        });
        helpLicense.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { licenseDialog(); }
        });
        helpContact.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) { contactDialog(); }
        });
        
        //if anything is pressed, mark as document as changed
        textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) { changed = true; }
        });
        
        //confirm saving on each exit
        primaryStage.setOnCloseRequest(event -> {
            if (changed){ confirmClose(); }
            if (changed){ event.consume(); }
        });
        
        textArea.setWrapText(true);
        
        root.setTop(menuBar);
        root.setCenter(textArea);
        
        //make the magic happen
        primaryStage.setMaximized(true);
        primaryStage.setTitle(currentFile + " - TextEditor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //END OF START
    
    
    
    
    //misc methods for program actions


    //open dialog with info about program
    static void aboutDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About TextEditor");
        alert.setHeaderText("TextEditor");
        alert.setContentText("(C) 2016 Andrew King\n\nThanks:\nL\nO\nE");

        alert.showAndWait();
    }

    //if window contents are marked as changed, open dialog to confirm changes
    void confirmClose() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Warning");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Choose your option.");

        ButtonType buttonTypeOne = new ButtonType("Save");
        ButtonType buttonTypeTwo = new ButtonType("Discard");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeOne){
            saveFile();
        } else if (result.get() == buttonTypeTwo) {
            changed = false;
            newFile();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }
    
    //open dialog with contact info
    static void contactDialog() {
        File ghFile = new File("./images/GitHub-Mark-64px.png");
        System.out.println(ghFile.isFile());
        URL url = tc.getResource("/GitHub-Mark-64px.png");
        System.out.println(url.toString());
        Image ghImage = new Image(url.toString());
        ImageView ghImageView = new ImageView(ghImage);

        Hyperlink gHLink = new Hyperlink("http://www.github.com/the-octagon");
        gHLink.setText("@the-octagon");
        gHLink.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e)
            {
                Application a = new Application() {

                    @Override
                    public void start(Stage stage)
                    {
                    }
                };
                a.getHostServices().showDocument("http://www.github.com/the-octagon");
            }
        });

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("TextEditor");
        alert.setHeaderText("Contact");

        FlowPane fp = new FlowPane();
        fp.getChildren().addAll(ghImageView, gHLink);
        alert.getDialogPane().contentProperty().set( fp );

        alert.showAndWait();
    }
    
    //copy selected text
    void copyText() {
        if(!textArea.getSelectedText().equals("")) {
            clipboardText = textArea.getSelectedText();
        clipboardContent.putString(clipboardText);
        clipboard.setContent(clipboardContent);
        }
    }

    //copy selected text
    void cutText() {
        if(!textArea.getSelectedText().equals("")) {
            clipboardText = textArea.getSelectedText();
            clipboardContent.putString(clipboardText);
            clipboard.setContent(clipboardContent);
            IndexRange range = textArea.getSelection();
            String origText = textArea.getText();
            String beforeText = origText.substring(0, range.getStart());
            String afterText = origText.substring(range.getEnd(), origText.length());
            textArea.setText( beforeText + afterText );
            textArea.positionCaret( range.getStart() );
        }
    }
    
    //used for updating the window title
    static Stage getStage() { return stage; }

    //open dialog with info about license
    void licenseDialog() {
        Alert alert = new Alert(AlertType.INFORMATION);
        File gplFile = new File("./images/gplv3-88x31.png");
        URL url = tc.getResource("/gplv3-88x31.png");
        Image gplLogo = new Image(url.toString());
        ImageView gplLogoImageView= new ImageView(gplLogo);
        alert.setTitle("License Information");
        alert.setHeaderText("TextEditor - a simple text editor");
        alert.setGraphic(gplLogoImageView);
        alert.getDialogPane().setPrefSize(550, 350);
        alert.setResizable(true);
        alert.setContentText("(C) 2016 Andrew King\n" + 
        "\n" +
        "This program is free software: you can redistribute it and/or modify " +
        "it under the terms of the GNU General Public License as published by " +
        "the Free Software Foundation, either version 3 of the License, or " +
        "(at your option) any later version.\n" +
        "\n" +
        "This program is distributed in the hope that it will be useful, " +
        "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the " +
        "GNU General Public License for more details.\n" +
        "\n" +
        "You should have received a copy of the GNU General Public License " +
        "along with this program.  If not, see <http://www.gnu.org/licenses/>.");

        alert.showAndWait();
    }
    
    //create new file
    void newFile() {
        if (changed) {
            confirmClose();
        }else {
            textArea.setText("");
            changed = false;
        }
    }

    //open existing file
    void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        file = fileChooser.showOpenDialog(new Stage());

        try {
            FileReader fileReader = new FileReader(file.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String inputFile = "";
            String textFieldReadable = bufferedReader.readLine();

            while (textFieldReadable != null){
                inputFile += (textFieldReadable + "\n");
                textFieldReadable = bufferedReader.readLine();
            }
            textArea.setText(inputFile);
            currentFile = file.getName();
            TextEditor.getStage().setTitle(currentFile + " - TextEditor");

        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("File not found.");
            alert.showAndWait();
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("An unknown error .");
            alert.showAndWait();
        }
    }
    
    //paste from clipboard
    void pasteText() {
        if (clipboard.getString() != null) {
            clipboardText = clipboard.getString();
            textArea.insertText(textArea.getCaretPosition(), clipboardText);
        }
    }
    
    //if file is new, create file and writeOut() or just writeOut()
    void saveFile() {
        if (currentFile.equals("Untitled")) {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showSaveDialog(new Stage());
            writeOut();
            currentFile = file.getName();
            TextEditor.getStage().setTitle(currentFile + " - TextEditor");
            changed = false;
        } else {
            writeOut();
        }
    }
    
    //set word wrap on or off
    private void setWordWrap() {
        if (textArea.isWrapText()) {
            textArea.setWrapText(false);
        } else {
            textArea.setWrapText(true);
        }
    }

    //write contents of window to file to file
    void writeOut() {
        try {
            FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
            fw.write(textArea.getText());
            fw.close();
        } catch (FileNotFoundException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("File not found.");
            alert.showAndWait();
        } catch (IOException ex) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("An unknown error has occured.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}