/*
 * Copyright 2017 Jean-Louis Pasturel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
*/
package org.jlp.logfouineur.util;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// TODO: Auto-generated Javadoc
/**
 * The Class AlertDialog.
 */
public class AlertDialog extends Stage {

    /** The width default. */
    private final int WIDTH_DEFAULT = 300;

    /** The Constant ICON_INFO. */
    public static final int ICON_INFO = 0;
    
    /** The Constant ICON_ERROR. */
    public static final int ICON_ERROR = 1;    

    /**
     * Instantiates a new alert dialog.
     *
     * @param owner the owner
     * @param msg the msg
     * @param type the type
     */
    public AlertDialog(Stage owner, String msg, int type) {
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.TRANSPARENT);

        Label label = new Label(msg);
        label.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        label.setWrapText(true);
        label.setGraphicTextGap(20);
        label.setGraphic(new ImageView(getImage(type)));

        Button button = new Button("OK");
        button.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        button.setPrefSize(80, 30);
        button.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0) {
                AlertDialog.this.close();
            }
        });

        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add(getClass().getResource("alert.css").toExternalForm());        
        borderPane.setTop(label);

        HBox hbox2 = new HBox();
        hbox2.setAlignment(Pos.CENTER);
        hbox2.getChildren().add(button);
        borderPane.setBottom(hbox2);

        // calculate width of string
        final Text text = new Text(msg);
        text.setFont(Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 20));
        text.snapshot(null, null);
        // + 20 because there is padding 10 left and right
        int width = (int) text.getLayoutBounds().getWidth() + 40;
 
        if (width < WIDTH_DEFAULT)
            width = WIDTH_DEFAULT;
 
        int height = 150;
 
       

        final Scene scene = new Scene(borderPane, width, height);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);

        // make sure this stage is centered on top of its owner
        setX(owner.getX() + (owner.getWidth() / 2 - width / 2));
        setY(owner.getY() + (owner.getHeight() / 2 - height / 2));
    }

    /**
     * Gets the image.
     *
     * @param type the type
     * @return the image
     */
    private Image getImage(int type) {
        if (type == ICON_ERROR)
            return new Image(getClass().getResourceAsStream("/images/error.png"));
        else
            return new Image(getClass().getResourceAsStream("/images/info.png"));
    }

}