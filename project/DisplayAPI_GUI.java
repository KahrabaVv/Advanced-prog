/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication1;

/**
 *
 * @author andre
 */
import java.io.IOException;
import java.util.ArrayList;
import static javaapplication1.MainFunction.getApisFromExcel;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
/**
 *
 * @author andrew
 */
public class DisplayAPI_GUI extends Application{
     
    
     @Override
    public void start(Stage primaryStage) throws IOException{
        
                // Reload services (APIs)
            JavaApplication1FX x =new JavaApplication1FX();
            x.start(new Stage());
           ArrayList<API> APIList = getApisFromExcel(x.getPath());
        // prepare the container of all project
        VBox pane = new VBox();
        pane.setFillWidth(true);
        pane.setSpacing(10);
        
        // Initialize Data / first loop on the APIs
        for (API A : APIList){
            VBox apiVBox = new VBox();
            apiVBox.setSpacing(10);
            apiVBox.getChildren().add(new APIHeader(A.getName()));
            // set Horizontal Json Objects
            JsonsRow requestBox = new JsonsRow();
            JsonsRow responseBox = new JsonsRow();
            ArrayList<Field> OrphanFieldList = getField(A.getFields());
            
            loopOnList(A.getFields(),requestBox, responseBox);
            
            // loop on the Orphan Fields
            for (Field field : OrphanFieldList) {
                if (field.getType() == 'I') {
                    requestBox.getChildren().addAll(new OrphanBox(field.getName()));
                }else{
                    responseBox.getChildren().addAll(new OrphanBox(field.getName()));
                }
            }
            // add All requestBox and responseBox to API Container
            apiVBox.getChildren().addAll(new JsonTypeHeader("Requests"),requestBox,
                    new JsonTypeHeader("Responses"),responseBox);
            pane.getChildren().add(apiVBox);
        }
        
        // enable Scrolling
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(pane);
        scrollPane.setFitToWidth(true);
        // Play the Stage
        Scene scene = new Scene(scrollPane,600  ,700);
        primaryStage.setTitle("Project");
        primaryStage.setScene(scene);
        primaryStage.show();
        


        
    }
    // get fields inside each object or Api and put in inside container of requests or responses
    public void loopOnList(ArrayList<Field> list, JsonsRow requestBox, JsonsRow responseBox){
        ArrayList<Obj> ObjList = new ArrayList<>();
        ArrayList<Field> FieldList = new ArrayList<>();

        for (Field field :list){
            if (field instanceof Obj) {
                ObjList.add((Obj)field);
            }else{
                FieldList.add(field);
            }
        }
        
        for (Obj obj : ObjList){
            if (obj.getType() == 'I') {
                requestBox.getChildren().addAll(new JsonObject(obj));
            }else{
                responseBox.getChildren().addAll(new JsonObject(obj));
            }
            loopOnList(obj.getFields(), requestBox, responseBox);
        }        
        
    }
    
    public ArrayList<Field> getField (ArrayList<Field> list){
        ArrayList<Field> fieldList = new ArrayList<>();
        for (Field listField :list){
            if (!(listField instanceof Obj)) {
                fieldList.add(listField);
            }
        }
        return fieldList;
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    
    // Header of API
    class APIHeader extends HBox{
        public APIHeader(String apiName){
            Text name = new Text(apiName);
            name.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 18));
            getChildren().add(name);
            setStyle("-fx-background-color: Gold");
            setAlignment(Pos.CENTER);
        }
    }
    
    // head of Json
    class JsonHeader extends StackPane{
        public JsonHeader(String nameString){
            Text name = new Text(nameString);
            name.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 20));
            getChildren().add(name);
            setStyle("-fx-border-color: red;");
            setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        }
    }
    
    // prepare Fields of Json
    class FieldPane extends VBox{
        public FieldPane(String nameString, String allowedValues, String mandatory){
            // Arrange Properties of Field
            GridPane gridPane = new GridPane();
            gridPane.addRow(0, new Label("Allowed Values: "), new Label(allowedValues));
            gridPane.addRow(1, new Label("Mandatory: "), new Label(mandatory));
            gridPane.setHgap(5);
            // prepare Field's Name
            Text name = new Text(nameString);
            name.setFont(Font.font("Time New Roman", FontWeight.BOLD, FontPosture.ITALIC, 13));
            getChildren().addAll(name, gridPane);
            //set Properties of Field
            setMargin(gridPane,new Insets(2,2,2,10));
            setPadding(new Insets(2,2,2,5));
            
        }
    }
    
    // Json's Body
    class JsonObject extends BorderPane{
        public JsonObject(Obj obj){
            //set Header of Object
            setTop(new JsonHeader(obj.getName()));
            //set Center -> fields 
            VBox fieldsBox = new VBox();
            for(Field field : obj.getFields()){
                fieldsBox.getChildren().add(new FieldPane(field.getName(), field.getAllowedValues(),
                        field.getMandatory() ));
            }
            setCenter(fieldsBox);
            
            setBorder(Border.stroke(Color.BLACK));

        }
    }
    
    // Orphan's Body
    class OrphanBox extends BorderPane{
        public OrphanBox(String orphanName){
            // Text of Orphan's Name
            Text name = new Text(orphanName);
            name.setFont(Font.font("Courier", FontWeight.BOLD, FontPosture.ITALIC, 18));
            // Arrange Orphan's Body
            setCenter(name);
            setBorder(Border.stroke(Color.BLACK));
            setPadding(new Insets(10));
        }
    }
    
    // Header of each type (Request or Response)
    class JsonTypeHeader extends HBox{
        public JsonTypeHeader(String typeName){
            Text name = new Text(typeName);
            name.setFont(Font.font("Time New Roman", 14));
            getChildren().add(name);
            setStyle("-fx-background-color: rgb(100,100,100)");
            setAlignment(Pos.CENTER);
        }
    }
    
    // Arrange Json and Orphan in Row
    class JsonsRow extends FlowPane{
        public JsonsRow(){
            setAlignment(Pos.CENTER);
            setHgap(20);
            setVgap(20);
        }
    }

}
