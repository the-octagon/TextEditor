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

/**
 *
 * @author andy
 */
public class TextEditor extends Application {
    private static Stage stage;
    private boolean changed = false;
    private String currentFile = "Untitled";
    private FileChooser dialog = new FileChooser();
    File file;
    private BorderPane root = new BorderPane();
    private TextArea textArea = new TextArea();
    private Scene scene = new Scene(root, 640, 480);
    
    
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
                
        //create File Menu and MenuItems
        final Menu fileMenu = new Menu("File");
            MenuItem fileNew = new MenuItem("New");
                fileNew.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        newFile();
                    }
                });
            MenuItem fileOpen = new MenuItem("Open");
                fileOpen.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        //if existing TextArea not null
                            //save or trash
                        //pick new file
                        openFile();
                    }
                });
            MenuItem fileSave = new MenuItem("Save");
                fileSave.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        saveFile();
                    }
                });
            //add File MenuItems to fileMenu
            fileMenu.getItems().add(fileNew);
            fileMenu.getItems().add(fileOpen);
            fileMenu.getItems().add(fileSave);
            
        //create Edit Menu and MenuItems
        final Menu editMenu = new Menu("Edit");
            MenuItem editCut = new MenuItem("Cut");
            MenuItem editCopy = new MenuItem("Copy");
            MenuItem editPaste = new MenuItem("Paste");
            
            //add edit MenuItems to editMenu
            editMenu.getItems().add(editCut);
            editMenu.getItems().add(editCopy);
            editMenu.getItems().add(editPaste);
            
            
        //create View Menu and MenuItems    
        final Menu viewMenu = new Menu("View");
            //figure out something to put in View menu
        
        //create Help Menu and MenuItems
        final Menu helpMenu = new Menu("Help");
            MenuItem helpMenu1 = new MenuItem("About");
            
                helpMenu1.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent t) {
                        aboutDialog();
                    }
                });
            MenuItem helpMenu2 = new MenuItem("Help");
            MenuItem helpMenu3 = new MenuItem("Contact");
            
            //add help MenuItems to helpMenu
            helpMenu.getItems().add(helpMenu1);
            helpMenu.getItems().add(helpMenu2);
            helpMenu.getItems().add(helpMenu3);
        
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(editMenu);
        menuBar.getMenus().add(viewMenu);
        menuBar.getMenus().add(helpMenu);
     
        textArea.setWrapText(true);
        
        root.setTop(menuBar);
        root.setCenter(textArea);
        
        //make the magic happen
        primaryStage.setMaximized(true);
        primaryStage.setTitle(currentFile + " - TextEditor");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //listeners
        textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                changed = true;
            }
        });
    }
        //methods
        static Stage getStage() { return stage; }
        
        void newFile() {
            if (changed) {
                            confirmClose();
                        }else {
                            textArea.setText("");
                            changed = false;
                        }
        }
        
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
        
        void saveFile() {
            if (currentFile.equals("Untitled")) {
                FileChooser fileChooser = new FileChooser();
                file = fileChooser.showSaveDialog(new Stage());
                writeOut();
                currentFile = file.getName();
                TextEditor.getStage().setTitle(currentFile + " - TextEditor");
            } else {
                writeOut();
            }
        }
        
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
        
        static void aboutDialog() {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("About TextEditor");
            alert.setHeaderText("TextEditor");
            alert.setContentText("(C) 2016 Andrew King\n\nThanks:\nL\nO\nE");

            alert.showAndWait();
        }

    public static void main(String[] args) {
        launch(args);
    }
}
    