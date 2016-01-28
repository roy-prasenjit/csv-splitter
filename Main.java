/*
 * CSV Splitter
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author NP00431423
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    static public String SOURCE_FILE_NAME = "./input.csv";
    static public String DESTINATION_FOLDER = "./output\\";
    static public int numberOfOutputFiles = 0;
    static public List<List<String>> outputFileColumnDetails = null;
    static public List<List<String>> fileAsMatrix = null;
    static public int headerLength = 0;
    static public int columnLength = 0;
    
    public static void main(String[] args) {
        //load csv
        //take number of output files required
        //take parameters for each outputfile
        //generate output files
        try{
            loadFile();
        }
        catch(FileNotFoundException fileNotFoundException){
            fileAsMatrix = null;
            System.out.println("fileNotFoundException : " + " Error loading file");
        }
        catch(IOException ioException){
            fileAsMatrix = null;
            System.out.println("IOException : " + " Error reading file");
        }
        // skip if file couldn't be loaded
        if(fileAsMatrix == null) return;
        try {
            //take output details
            loadOutputDetails();
        } catch (IOException ex) {
            System.out.println(" Error reading from command prompt");
        }
        
    }
    public static void loadFile() throws FileNotFoundException, IOException{
        BufferedReader bufferedReader = new BufferedReader(new FileReader(Main.SOURCE_FILE_NAME));
        StringTokenizer  stringTokenizer = null;
        Main.fileAsMatrix = new ArrayList<List<String>>();
        Main.outputFileColumnDetails = new ArrayList<List<String>>();
        String line = "";
        try {
            while((line = bufferedReader.readLine()) != null){
                String[] splitted = line.split(",");
                List<String> dataLine = new ArrayList<String>(splitted.length);
                for (String data : splitted)
                    dataLine.add(data);
                fileAsMatrix.add(dataLine);
                //System.out.println(csvData);
            }
        }finally{
            if (bufferedReader != null)
            bufferedReader.close();
        }
    //System.out.println(fileAsMatrix);
    columnLength = fileAsMatrix.size();
    headerLength = fileAsMatrix.get(0).size();
    }

    private static void loadOutputDetails() throws IOException {
        System.out.println("How many outputfiles are required?");
        System.out.print("Enter Number: ");
        try{
        numberOfOutputFiles = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
        }catch(NumberFormatException numberFormatException){
            // log details
            System.out.println("Error in given number. Reload application");
        }
        if(numberOfOutputFiles == 0) return;
         //load column values
         System.out.println("Enter column values for each output file separated by a comma");
         System.out.println("Exampe: 2,3,4,6,8");boolean fail = false;
         for(int fileNumber = 0; fileNumber < numberOfOutputFiles && !fail ; ++fileNumber){
             boolean confirmColumn = false;
             while(!confirmColumn && !fail){
                 System.out.println("Enter column values for " + (fileNumber + 1) + " file:");
                 String columns = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                 System.out.println("Confirm column list [y/Y] or n/N");
                 System.out.println(columns);
                 String confirmation = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                 if("Y".equalsIgnoreCase(confirmation)){
                     System.out.println("Confirmed");
                     confirmColumn = true;
                     String[] splitted = columns.split(",");
                        List<String> columnList = new ArrayList<String>(splitted.length);
                        columnList.addAll(Arrays.asList(splitted));
                        outputFileColumnDetails.add(columnList);
                 }
                 else{
                     System.out.println("Do you want to continue again? [Y/y]");
                     String reTry = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                     if(!"Y".equalsIgnoreCase(reTry)){
                         System.out.println("Sorry, could not retrive details. Reload Application");
                         fail = !fail;
                     }
                 }
             }
         }
//         System.out.println("Column List");
//         System.out.println(outputFileColumnDetails);
	 if(fail) return;
         writeFiles();
    }
    private static void writeFiles() throws IOException {
        if( numberOfOutputFiles == 0 || outputFileColumnDetails == null || fileAsMatrix == null){
            System.out.println("Sorry, could not retrive one or more required details needed. Reload Application");
            return;
        }
        File outputfile = null;
        List<String> columns = null;
        PrintWriter writer = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        String outputFolder = DESTINATION_FOLDER + dateFormat.format(date);
        System.out.println("output folder:" + outputFolder);
        File outputDir = new File(outputFolder);
        if( outputDir.mkdir() == false){
            System.out.println("Sorry,Could not create outputDirectory. Reload Application");
            return;
        }
        for(int fileNumber = 0; fileNumber < numberOfOutputFiles; ++fileNumber){
            outputfile = new File(outputFolder + "\\" + (fileNumber + 1) + ".csv");
            if (!outputfile.exists()) {
		outputfile.createNewFile();
            }
            try{
                System.out.println("Writing file");
                writer = new PrintWriter(outputfile, "UTF-8");
                columns  = outputFileColumnDetails.get(fileNumber);
                Collections.sort(columns);
                try{
                    if(Integer.parseInt(columns.get(0)) < 1 || Integer.parseInt(columns.get(columns.size() - 1)) > headerLength){
                        System.out.println("Given column list is invalid");                           
                        throw new Exception();
                    }
		    if(Integer.parseInt(columns.get(0)) != 1) columns.add(0,"1");
                    for(int row = 0; row < columnLength; ++row){
                        String line = "";
                        Iterator iterator = columns.iterator();
                        while(iterator.hasNext()){
                          String columnNumber = (String)iterator.next();
                            int col = Integer.parseInt(columnNumber) - 1;
                            line += fileAsMatrix.get(row).get(col);
                            line += ( iterator.hasNext() ? "," : "");
                        }
                        writer.println(line);
                    }
                }catch(Exception exception){
                    outputfile.delete();
                    System.out.println("Something went wrong while trying to write the file: " + (fileNumber + 1));
                    System.out.println("Skipping current file and continuing with the next file");
                }
            }
            finally{
                if(writer != null) writer.close();
            }
        }
    }
}

