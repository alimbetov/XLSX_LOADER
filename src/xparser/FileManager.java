
package xparser;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.filechooser.FileSystemView;
public  class FileManager implements DAO{


  public  List<File>  scanFolder (String folder){
        List<File> scanFiles = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(p->  scanFiles.add( new File(String.valueOf(p))));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scanFiles;
        }

  public String  get_SerchedBox(){
      String result="C:\\"+"Search_BOX";
      File directory = new File(result);
      if (! directory.exists()){
          directory.mkdir();
      }
      return result;
  }
  
  public String  get_validateBox(){
        System.out.print(get_SerchedBox());
      String result="C:\\"+"Search_BOX"+"\\"+"VALIDATED";
      File directory = new File(result);
      if (! directory.exists()){
          directory.mkdir();
      }
      return result;
  }

    public String  get_PARSED(){
        System.out.print(get_SerchedBox());
        String result="C:\\"+"Search_BOX"+"\\"+"PARSED";
        File directory = new File(result);
        if (! directory.exists()){
            directory.mkdir();
        }
        return result;
    }

    public String  get_LogDir(){
        System.out.print(get_SerchedBox());
        String result="C:\\"+"Search_BOX"+"\\"+"LOG";
        File directory = new File(result);
        if (! directory.exists()){
            directory.mkdir();
        }
        return result;
    }

  public void movefiles_toSearch_BOX (List<File> files , boolean cleanBefore){
          String pathdestination=get_SerchedBox()+"\\";

       if (cleanBefore)this.cleanBox();
       
        files.stream().parallel().forEach(
                f->
                {
                    try {
                        Files.copy(f.toPath(),
                                (new File(pathdestination + f.getName())).toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

    }
  

  
    public void cleanBox(){
      scanFolder(this.get_SerchedBox()).stream()
                .forEach(file -> file.delete());
    }

    public void cleanValidateBox(){
      scanFolder(this.get_validateBox()).stream()
                .forEach(file -> file.delete());
    }


    public String  movefiles_from_Search_BOX_to_VALIDATE ( String fileName){
        String result="";
        String pathfrom=get_SerchedBox()+"\\";
        String pathdestination=get_validateBox()+"\\";

        File sfile= new File(pathfrom+fileName);

        if (sfile.exists()){

            int validateparam=(fileValidate(sfile.getAbsolutePath()));
            if (validateparam==9){
                result=fileName;
            }else {
                result=fileName + "  "+ validateparam +" is NOT Validated;";
            }

            if (result.equals(fileName)) {
                result="";
                try {

                    System.out.println("copy proces");
                    Files.copy(sfile.toPath(),(new File(pathdestination + fileName)).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                File tfile= new File(pathdestination+fileName);

                if (tfile.exists()){sfile.delete();}

               tfile = null;
            }
            sfile = null;
            System.gc();

        }

        return  result;
    }

public boolean csvFileisExists(File file){
    return  new File(getCsvFilefromXLS(file)).exists();
}

public String getCsvFilefromXLS(File parFile) {
      return this.get_PARSED() + "/" +  parFile.getName().substring(0, parFile.getName().lastIndexOf('.')) + ".csv";
}

   public String  parsingFile(File parFile){
      String result="";
       String targetAbsolutePath=getCsvFilefromXLS(parFile);
       File csvf = new File(targetAbsolutePath);
       if (csvf.exists()){csvf.delete();}
       try {
           csvf.createNewFile();
          if (csv_write(parFile.getAbsolutePath(),parFile.getName(),targetAbsolutePath)!=1){
              result="PARS error -> "+ parFile.getName();
          };
       } catch (IOException e) {
           e.printStackTrace();
       }

return result;
   }

    public int fileValidate(String pathname) {

      System.out.println("fileValidate start path "+ pathname);
        int result=0;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        try {
            try(
                    InputStream is = new FileInputStream(new File(pathname));
                    // Workbook wb = StreamingReader.builder().open(is);
                    Workbook wb = StreamingReader.builder()
                            .rowCacheSize(10)    // number of rows to keep in memory (defaults to 10)
                            .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                            .open(is)
            ) {
                List<List<Cell>> obj = new ArrayList<>();
                for (Row r : wb.getSheetAt(0)) {
                    List<Cell> o = new ArrayList<>();
                    for (Cell c : r) {
                        o.add(c);
                    }
                    obj.add(o);
                    if (r.getRowNum()==11) {break;};
                }

                List<Cell> row;
                //set header position

                int controlsum=0;
                row = obj.get(5);
                for (Cell c : row) {
                    if (c.getColumnIndex()==0 && c.getStringCellValue().contains("Calling Date")){controlsum++;}
                    if (c.getColumnIndex()==1 && c.getStringCellValue().contains("Calling Time")){controlsum++;}
                    if (c.getColumnIndex()==2 && c.getStringCellValue().contains("Calling Number")){controlsum++;}
                    if (c.getColumnIndex()==3 && c.getStringCellValue().contains("Direction")){controlsum++;}
                    if (c.getColumnIndex()==4 && c.getStringCellValue().contains("Called Number")){controlsum++;}
                    if (c.getColumnIndex()==5 && c.getStringCellValue().contains("Call Duration")){controlsum++;}
                    if (c.getColumnIndex()==6 && c.getStringCellValue().contains("Tarif")){controlsum++;}
                    if (c.getColumnIndex()==7 && c.getStringCellValue().contains("Cost")){controlsum++;}
                }
                row = obj.get(6);
                for (Cell c : row) {
                    if (c.getColumnIndex()==0 && c.getStringCellValue().contains(".")){controlsum++;};
                }
                result=controlsum;
                LogMessage(pathname +"controlsum" + controlsum,"validate.txt");
          /*
                for (int r=1; r<=10; r++){
                    row = obj.get(r);
                    for (Cell c : row) {
                        System.out.print(c.getStringCellValue()+";");
                    }

                }*/

            }
        } catch (IOException e) {
            LogMessage(pathname +"IOException" + e.getMessage(),"validate.txt");
            e.printStackTrace();
        }


        return result;
    }

    public int csv_write(String srcFile, String fileNme, String targetFilename) {
        int result=0;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        try {
            try(
                    InputStream is = new FileInputStream(new File(srcFile));
                    // Workbook wb = StreamingReader.builder().open(is);
                    Workbook wb = StreamingReader.builder()
                            .rowCacheSize(100000)    // number of rows to keep in memory (defaults to 10)
                            .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                            .open(is)
            ) {
                List<String> rl = new ArrayList<>();
                for (Row r : wb.getSheetAt(0)) {
                    if (r.getRowNum()>=6) {
                        String rowcsv="";
                        for (Cell c : r) {
                            if (r.getCell(0).getStringCellValue().contains(".")  )     {
                                if (c.getColumnIndex()>=0 && c.getColumnIndex()<=8) {
                                    rowcsv=rowcsv+";"+c.getStringCellValue();
                                }
                            }
                        }
                        if (rowcsv.length()>0)  rl.add(fileNme+rowcsv+"\n");
                    }
                }


                try {
                    FileWriter fw = new FileWriter(targetFilename, true); //the true will append the new data
                    rl.stream().parallel().forEach(e -> {
                        try {
                            fw.write(e);
                        } catch (IOException ioException) {
                            LogMessage(targetFilename +"ClassNotFoundException" + ioException.getMessage(),"csv_write.txt");
                            ioException.printStackTrace();
                        }
                    });
                    result=1;
                    fw.close();
                    System.gc();
                } catch (IOException ioe) {
                    LogMessage(targetFilename +"IOException" + ioe.getMessage(),"csv_write.txt");
                    System.err.println("IOException: " + ioe.getMessage());
                }



            }
        } catch (IOException e) {
            LogMessage(targetFilename +"IOException" + e.getMessage(),"csv_write.txt");
            e.printStackTrace();
        }


        return result;
    }


    public int[]  executeSqlBatches ( List<String> sqllist, File file) {
        int[] result=null;
        List<String> sql_comlist = sqllist;
        Connection connection = null;
        Statement statement = null;
        if (sql_comlist.size() > 1) {
            try {
                Class.forName(DRIVER);
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                connection.setAutoCommit(false);
                statement = connection.createStatement();
                for (String sql : sql_comlist) {
                    statement.addBatch(sql);
                }
                int[] updateCounts = statement.executeBatch();
                result=updateCounts;
                connection.commit();
                connection.setAutoCommit(true);
                file.delete();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                LogMessage("ClassNotFoundException" + e.getMessage(),"jdbc_log.txt");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LogMessage("SQLException" + throwables.getMessage(),"jdbc_log.txt");

            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) { /* ignored */}
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) { /* ignored */}
                }
            }
        }
        return  result;
    }



    public List<String>getSqlPacks(File file){

        String filePath=file.getAbsolutePath();
        String table="DWH_ADM.test_kt_xlsx";
        String fileName="x";
        List<String> result = new ArrayList<>();
        List<String> finalreult=new ArrayList<>();
        String COMMA_DELIMITER=";";
        try (
                BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(COMMA_DELIMITER);
                fileName=line.split(COMMA_DELIMITER)[0].toUpperCase();
                result.add(getSQLINSERT(line.split(COMMA_DELIMITER),table));
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        String firstSQLComand="delete from "+ table +" where xlsx_filename='"+fileName.toUpperCase()+"'";
        finalreult.add(firstSQLComand);
        result.stream().forEach(s-> finalreult.add(s));
        return finalreult;
    }



    public  String getSQLINSERT( String[] values, String tableName){
        String result="";
        if (values.length==9){
            result="insert into " + tableName + " "+
                    "values\n" +
                    "('"+ values[0].toUpperCase() +"', to_date('"+values[1]+"','dd.mm.yyyy')  ," +
                    " TO_DSINTERVAL('0 "+values[2]+"'), '"+values[3]+ "', '"+values[4]+ "', '"+values[5]+"', '"+values[6].replace(".",",")+"', '"+values[7].replace(".",",")+"', '"+values[8].replace(".",",")+"')";


        }
        return result;
    }

public void chekmkdirFile(String filePath,boolean del){
    File myfile = new File(filePath);
    try {
        myfile.createNewFile(); // if file already exists will do nothing
    } catch (IOException e) {
        e.printStackTrace();
    }
    if (del){ myfile.delete(); }
}



    public String  movefiles_from ( String fileName){
        String result="";
        String pathfrom=get_SerchedBox()+"\\";
        String pathdestination=get_validateBox()+"\\";

        File sfile= new File(pathfrom+fileName);

        if (sfile.exists()){

            int validateparam=(fileValidate(sfile.getAbsolutePath()));
            if (validateparam==9){
                result=fileName;
            }else {
                result=fileName + "  "+ validateparam +" is NOT Validated;";
            }

            if (result.equals(fileName)) {
                result="";
                try {

                    System.out.println("copy proces");
                    Files.copy(sfile.toPath(),(new File(pathdestination + fileName)).toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                File tfile= new File(pathdestination+fileName);

                if (tfile.exists()){sfile.delete();}

                tfile = null;
            }
            sfile = null;
            System.gc();

        }

        return  result;
    }





    public void LogMessage(String ermsg, String logFileName){
      String logdir=get_LogDir();
      String targetFilename=logdir + "\\"+logFileName;
        chekmkdirFile(targetFilename,false);
        FileWriter fw=null;
        try {
            try {
                fw = new FileWriter(targetFilename, true); //the true will append the new data
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fw.write(ermsg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } finally {
            if (fw!=null){
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public List<String>getLogData(File file){
        List<String> result = new ArrayList<>();
        try (
                BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.add(line+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }




}
