package overlay;

import com.github.jonatino.process.Processes;
import com.guidedhacking.GH_Tools.GHMemory;
import com.guidedhacking.GH_Tools.GHTools;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import memory.Mem;


public class overlay extends Application {
    private static boolean attached = false;
    private static long base;
    public static void main(String[] args) {
        if(GHMemory.openProcess("Dota 2")) {
            attached = true;
            Mem.process = Processes.byName("dota2.exe");
            Mem.engine = Mem.process.findModule("engine2.dll").address();
            Mem.client = Mem.process.findModule("client.dll").address();
            base = Mem.process.readLong(Mem.engine + 0x00577870);
            launch(args);
        }else{
            System.exit(0);
        }
    }
    private final int middle = GHTools.getGameWidth() / 2;
    @Override
    public void start(Stage primaryStage) {
        if(attached) {

            Pane pane = new Pane();
            Scene scene = new Scene(pane, GHTools.getGameWidth(), GHTools.getGameHeight());
            primaryStage.setTitle("Gamingchair - Dota 2");
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            pane.setStyle("-fx-background-color: rgba(255, 255, 255, 0);");
            primaryStage.setAlwaysOnTop(true);
            Label isVisible = new Label("Running");
            pane.getChildren().add(isVisible);
            //Author
            {
                Label auth = new Label("Author: Notorious");
                pane.getChildren().add(auth);
                auth.setLayoutX(30);
                auth.setLayoutY(30);
                auth.setFont(new Font("Verdana", 13));
                auth.setTextFill(Color.RED);
            }
            //Discord
            {
                Label auth = new Label("Discord: Notorious#3692");
                pane.getChildren().add(auth);
                auth.setLayoutX(30);
                auth.setFont(new Font("Verdana", 13));
                auth.setLayoutY(45);
                auth.setTextFill(Color.RED);
            }
            primaryStage.setX(GHTools.getGameXPos());
            primaryStage.setY(GHTools.getGameYPos());
            isVisible.setLayoutX(middle);
            isVisible.setTextFill(Color.WHITE);
            isVisible.setFont(new Font("Verdana", 15));
            isVisible.setLayoutY(+50);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.show();


            //Reading multilevel pointer
            final long[] offsets = { 0x0, 0x28, 0x38, 0x98, 0x170, 0x0, 0x1F4 };
            System.out.println("Base: " + Long.toHexString(base).toUpperCase());





            AnimationTimer update = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if(attached) {
                        primaryStage.setX(GHTools.getGameXPos());
                        primaryStage.setY(GHTools.getGameYPos());
                        isVisible.setLayoutX(middle);
                        //Visible Cheat
                        long visible = base;
                        try{
                            for (long offset : offsets) {
                                visible = Mem.process.readLong(visible + offset);
                            }
                        }catch (Exception e){
                            isVisible.setText("Saliendo");
                            try{
                                Thread.sleep(100);
                            }catch (InterruptedException e1){
                                System.out.println(e1.getMessage());
                            }
                            System.exit(0);
                        }
                        if((int)visible == 6 || (int)visible == 10) {
                            isVisible.setText("Not Visible");
                            isVisible.setTextFill(Color.color(0, 1f, 0, 1f));
                        }
                        else if((int)visible == 14){
                            isVisible.setText("Visible");
                            isVisible.setTextFill(Color.color(1f, 0, 0, 1f));
                        }else{
                            isVisible.setText("Waiting...");
                        }

                    }
                }
            };
            update.start();
        }

    }
}
