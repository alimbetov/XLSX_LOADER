/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xparser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 *
 * @author Ruslan.Alimbetov
 */
public class FXMLDocumentController implements Initializable {
    FlowManager flmng= new FlowManager();

    @FXML
    private Label label;

    @FXML
    private Hyperlink link1;

    @FXML
    private CheckBox checkBoxDel;
    
    @FXML
    private TextArea msgbox;
    
    @FXML
    private TextField textFDDerictory;
    
    @FXML
    private TextField textFDFileSearch;
    
    @FXML
    private Button bt_search;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @FXML
    private void handleButtonDirectory(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog((Stage) textFDDerictory.getScene().getWindow());
        if(selectedDirectory == null){
        }else{
            textFDDerictory.setText(selectedDirectory.getAbsolutePath());  
        }
    }
    
    @FXML
    private void handleButtonSearchDirectory(ActionEvent event) {
         List<File> result= new ArrayList<>();
 
     if (textFDDerictory.getText().length()>0 && textFDFileSearch.getText().length()>0){ 
         FileManager fm = new FileManager();
         result=(List<File>) fm.scanFolder(textFDDerictory.getText())
                 .stream()
                 .filter(f-> f.getName().contains(".xlsx"))
                 .filter(f-> f.getName().contains(textFDFileSearch.getText()))
                 .collect(Collectors.toList());
             
         if (result.size()>0) {
         bt_search.setText("Detected "+result.size());
         
         msgbox.setText(" By Folder "+textFDDerictory.getText() + " contain in name *"+textFDFileSearch.getText()+" Detected " + result.size() + " Files");
         
          
         } else {
         msgbox.setText(" By Folder "+textFDDerictory.getText() + " contain in name *"+textFDFileSearch.getText()+" Files Not Detected ");
         
         }
     }
    }
    
    
      @FXML
    private void handleButtonCopytoBox(ActionEvent event) {
         List<File> result= new ArrayList<>();

     if (textFDDerictory.getText().length()>0 && textFDFileSearch.getText().length()>0){
         add_ErrorMessage("Начало переноса");

                   System.out.println(textFDDerictory.getText());
                   System.out.println(textFDFileSearch.getText());
         
         FileManager fm = new FileManager();
         result=(List<File>) fm.scanFolder(textFDDerictory.getText())
                 .stream()
                 .filter(f-> f.getName().contains(".xlsx"))
                 .filter(f-> f.getName().contains(textFDFileSearch.getText()))
                 .collect(Collectors.toList());

         add_ErrorMessage("Обнаружено  "+  result.size() + " файлов" );
         if (result.size()>0) {

         fm.movefiles_toSearch_BOX(result, checkBoxDel.selectedProperty().getValue());
             add_ErrorMessage("Перенесено  "+  result.size() + " файлов" );


         }
     }
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        textFDFileSearch.setText("_KT_");
        textFDDerictory.setText("C:\\test\\May_2020");
        checkBoxDel.setSelected(false);


    }


    public void add_ErrorMessage(String msg){
        if (msg.length()>0)  this.msgbox.setText(this.msgbox.getText() + "\n"+ msg);
    }
    
@FXML
private void handleButtonVALIDATE(ActionEvent event) {
System.out.println("You clicked ButtonVALIDATE!");

    List<File> result;
    FileManager fm = new FileManager();
    List<String> ermsg = new ArrayList<>();
    result=(List<File>) fm.scanFolder(fm.get_SerchedBox())
            .stream()
            .filter(f-> f.getName().contains(".xlsx"))
            .filter(f-> !f.getAbsolutePath().contains("VALIDATED"))
            .collect(Collectors.toList());


    msgbox.setText("Start Validation "+ result.size()+ " files");



    result.stream().parallel().forEach( f ->
            ermsg.add(fm.movefiles_from_Search_BOX_to_VALIDATE(f.getName())));
    ermsg.stream().filter(e-> e.length()>0).forEach(
            m-> add_ErrorMessage(m));


    add_ErrorMessage("End Validation");
}
   

@FXML
private void handleButtonPARS(ActionEvent event) {
System.out.println("You clicked ButtonPARS!");
    msgbox.setText("Start Parsing");
    List<File> result;
    FileManager fm = new FileManager();

    String fileLogpath=fm.get_LogDir()+"\\"+"validate.txt";

    fm.chekmkdirFile(fileLogpath,true);
    fm.chekmkdirFile(fileLogpath,false);

    List<String> ermsg = new ArrayList<>();
    result=(List<File>) fm.scanFolder(fm.get_validateBox())
            .stream()
            .filter(f-> f.getName().contains(".xlsx"))
            .filter(f-> f.getAbsolutePath().contains("VALIDATED"))
            .filter(f-> ! fm.csvFileisExists(f))
            .collect(Collectors.toList());
    msgbox.setText(msgbox.getText()+" Detected "+ result.size() );
    flmng.flowGenerate(result);

    ermsg=fm.getLogData(new File(fileLogpath));
    ermsg.stream().forEach(e-> add_ErrorMessage(e));

    add_ErrorMessage("Parsing  Finished");
}



    @FXML
    private void handleButtonPARSover(ActionEvent event) {
        System.out.println("You clicked ButtonPARS!");


        msgbox.setText("Start Parsing");
        List<File> result;
        FileManager fm = new FileManager();
        String fileLogpath=fm.get_LogDir()+"\\"+"validate.txt";
        fm.chekmkdirFile(fileLogpath,true);
        fm.chekmkdirFile(fileLogpath,false);

        List<String> ermsg = new ArrayList<>();
        result=(List<File>) fm.scanFolder(fm.get_validateBox())
                .stream()
                .filter(f-> f.getName().contains(".xlsx"))
                .filter(f-> f.getAbsolutePath().contains("VALIDATED"))
                .collect(Collectors.toList());

        msgbox.setText(msgbox.getText()+" Detected "+ result.size() );
        flmng.flowGenerate(result);

        ermsg=fm.getLogData(new File(fileLogpath));
        ermsg.stream().forEach(e-> add_ErrorMessage(e));

        add_ErrorMessage("Parsing  Finished");
    }


@FXML
private void handleButtonLOAD(ActionEvent event) {
    System.out.println("You clicked ButtonLOAD!");

    msgbox.setText("Start DB LOAD");
    List<File> result;
    FileManager fm = new FileManager();
    String fileLogpath=fm.get_LogDir()+"\\"+"jdbc_log.txt";

    fm.chekmkdirFile(fileLogpath,true);
    fm.chekmkdirFile(fileLogpath,false);

    List<String> ermsg = new ArrayList<>();
    result=(List<File>) fm.scanFolder(fm.get_PARSED())
            .stream()
            .filter(f-> f.getName().contains(".csv"))
            .filter(f-> f.getAbsolutePath().contains("PARSED"))
            .collect(Collectors.toList());
    msgbox.setText(msgbox.getText()+" Detected "+ result.size() );
    flmng.jflowGenerate(result);
    ermsg=fm.getLogData(new File(fileLogpath));
    ermsg.stream().forEach(e-> add_ErrorMessage(e));
    add_ErrorMessage("Load  Finished");


}
     
    
}
