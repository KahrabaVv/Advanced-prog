package javaapplication1;
import  java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class API{
    private ArrayList<Field> fields = new ArrayList<Field>();
    private String name;
    private String httpOperation ;
    private String requestUrl ;
    
    public  API(String name,String httpOperation,String requestUrl){
        this(name,httpOperation,requestUrl, new ArrayList<Field>());
    }

    public API(String name,String httpOperation,String requestUrl,  ArrayList<Field> fields) {
        this.name = name;
        this.fields= fields;
        this.httpOperation= httpOperation;
        this.requestUrl= requestUrl;
    }


    public String getName(){
        return name;
    }


    public ArrayList<Field> getFields(){
        return fields;
    }

    public void createFields( ArrayList<Field> fields){
        this.fields = fields;
    }
    public void addfield(Field field){
        this.fields.add(field) ;
    }
    


}

class Field{
    private String name;
    private String allowedValues;
    private String mandatory;
    private char type;
    private String Parent;

    public Field(char type,String name,String allowedValues,String mandatory){
        this(type,name,allowedValues,mandatory,null);

    }
    public Field(char type,String name,String allowedValues,String mandatory ,String Parent){
        this.name = name;
        this.allowedValues = allowedValues;
        this.mandatory = mandatory;
        this.type=type;
        this.Parent=Parent;
    }
    public String getName(){
        return name;
    }
    public String getAllowedValues(){
        return allowedValues;
    }
    public String getMandatory(){
        return mandatory;
    }
    public char getType(){
        return type;
    }
    public String getParent(){
        return Parent;
    }
}

class Obj extends Field{

    private ArrayList<Field> fields = new ArrayList<Field>();
    public Obj(char type,String name,String allowedValues,String mandatory,String Parent){
        this(type,name, allowedValues, mandatory,Parent, new ArrayList<Field>());

    }
    public Obj(char type,String objName,String allowedValues,String mandatory,String Parent, ArrayList<Field> fields){
        super(type,objName, allowedValues, mandatory,Parent);
        this.fields = fields;

    }


    public ArrayList<Field> getFields() {
        return fields;
    }

    public void addfield(Field field){
        this.fields.add(field) ;
    }


}




public class MainFunction {
    public  static Field getField(XSSFRow tempRow){
        // we are getting each element of that row given from the excel sheet in the I/O fields
        char type = tempRow.getCell(0).toString().charAt(0);
        String fieldType = tempRow.getCell(2).toString();
        String allowedValues = tempRow.getCell(3).toString();
        String mandatory = tempRow.getCell(4).toString();
        String[] fields = tempRow.getCell(1).toString().split("/").clone();
        String name = fields[fields.length -1];

        //Checking if the allowedValues in that row is given,if not we set it to ALL
        if (allowedValues.isEmpty()) {
            allowedValues = "All";
        }

        String Parent = null;
        //if there is only 1 element in the FieldName in that row then this element will go to the API List to be viewed in the GUI
        if(fields.length > 1){
             Parent = fields[fields.length -2];
        }
        Field field = null;
        //Check if the element in the field name is Field or Object
        if (fieldType.contains("string")){
            field = new Field(type,name,allowedValues,mandatory,Parent);
        }
        else {
            field = new Obj(type,name,allowedValues,mandatory,Parent);

        }
        return  field;
    }
    public static ArrayList<API> getApisFromExcel(String ExcelPath)  throws FileNotFoundException, IOException{
        // initialize Sheet
        // only Access First Sheet
        // get the max number of rows in a given sheet
        XSSFWorkbook wb= new XSSFWorkbook(new FileInputStream(ExcelPath));
        String firstSheetName =   wb.getSheetName(0);
        XSSFSheet firstSheet = wb.getSheet(firstSheetName);
        int lastRow = firstSheet.getLastRowNum();

        // Get I/O and Http RestApi and store their indices in their given arrays
        ArrayList<Integer> io = new ArrayList<Integer>();
        ArrayList<Integer> http = new ArrayList<Integer>();
        for (int i = 0; i <= lastRow; i++) {
            XSSFRow tempRow = firstSheet.getRow(i);
            if (tempRow == null){
                continue;
            }
            XSSFCell tempCell = tempRow.getCell(0);
            if (tempCell == null){
                continue;
            }

            String temp = tempCell.toString();
            if (temp.contains("HTTP Operation"))
            {
                http.add(i);
            }
            else if (temp.contains("I/o")){
                io.add(i);
            }
            continue;


        }
        // initializing the list of API given in the excell sheet
        ArrayList<API> apis = new ArrayList<API>();
        //loop over each api in given excel sheet using the http length since it contains all names above its indices
        for (int i = 0 ; i < http.size(); i++) {
            //getting the http for each element in the field name and sending it to its current API
            String apiName = firstSheet.getRow(http.get(i)-1).getCell(0).toString();
            String httpOperationName = firstSheet.getRow(http.get(i)+1).getCell(0).toString();
            String requestUrlName = firstSheet.getRow(http.get(i)+1).getCell(1).toString();
            API tempAPI = new API(apiName,httpOperationName,requestUrlName);
            ArrayList<Field> tempFields = new ArrayList<Field>();

            // Loop till Row Empty to stop that current API's I/O &@$*($@(&$)@_()@&)(*#*)@(&#!_($&!)$&)@&)$^*)@$*$@^@$

            //loop and Create Fields with Parents name  Array [object1,object2,Field9 ,Field1,Field2]
            for (int j = io.get(i) + 1; true ; j++) {
                XSSFRow tempRow = firstSheet.getRow(j);
                 if (tempRow == null ||  tempRow.getCell(0) == null || (tempRow.getCell(0) != null && tempRow.getCell(1) == null)){
                    break;
                }
                Field field = getField(tempRow);
                tempFields.add(field);

            }
            //loop API Field

            for (int j = 0; j < tempFields.size() ; j++) {


                Field tempField = tempFields.get(j);
                //Check FieldName not have Parent and Put it in TempApi --- Object1 ,Field12 in Api as Main Fields
                if (tempField.getParent().isEmpty()){
                    tempAPI.addfield(tempField);
                    continue;
                }
                // Get ParentName of Current Field and Actual Parent Reference and Add The Field in it
                String tempFieldParentName = tempField.getParent();
                Field Parent =  tempFields.stream()
                        .filter(t->t.getName().equals(tempFieldParentName))
                        .findFirst().get();
                if (Parent instanceof  Obj){
                    ((Obj)Parent).addfield(tempField);
                }
            }
            // Add tempAPI in Apis
            apis.add(tempAPI);



        }
        return  apis;
    }
    public static void main(String[] args) throws  IOException {
        ArrayList<API> apis = getApisFromExcel("E:\\Junior CSE\\Advanced Computer Programming\\Final Project\\project After change\\Example.xlsx");
        System.out.println("1");
    }
}


