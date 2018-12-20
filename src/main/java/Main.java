import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends Application {

    private Pane root = new Pane();
    private File selectedFile;


    @Override public void start(Stage stage) {
        stage.setTitle("Java projekt A W");
        root.setPrefSize(800, 600); // wymiary glownego okna

        final NumberAxis xAxis = new NumberAxis(); // ustawienie osi x jako numerycznej
        final NumberAxis yAxis = new NumberAxis();  // to samo dla osi y

        /*final LogarithmicAxis xAxis = new LogarithmicAxis(); // ustawienie osi x jako numerycznej
        final LogarithmicAxis yAxis = new LogarithmicAxis();*/

        xAxis.setLabel("Oś X"); // nazwy
        yAxis.setLabel("Oś Y");

        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis); // wykres liniowy - numeryczny, na dwoch osiach


        lineChart.setCreateSymbols(false);

        //XYChart.Series series = new XYChart.Series(); // seria danych x i y dla pojedynczego wykresu

        FileChooser fileChooser = new FileChooser(); // file choose do oblsugi okienka wyboru pliku z danymi do wczytania
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")); // filter ktory pokazuje tylko pliki .txt podczas wyboru

        Button wybierzPlik = new Button("Wybierz plik"); // przycisk wyboru pliku
        wybierzPlik.setTranslateX(100); // lokacja przycisku w osi x
        wybierzPlik.setTranslateY(550); // - || - w osi y
        wybierzPlik.setOnAction(event -> { // akcja po kliknieciu
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null) {
                System.out.println("Kliknieto Anuluj");

            } else {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

                    String line;
                    int x = 0;
                    while ((line = reader.readLine()) != null){
                        x++;
                        System.out.println(x);
                        System.out.println(line); // linia przed konwersja
                        XYChart.Series series = new XYChart.Series(); // nowa linia tekstu = nowa seria danych
                       // ObservableList foo = series.getData();

                        series.setName("Wykres " +x);
                        List<Integer> intList = Stream // konwersja pojedynczej linii na liste
                                .of(line.split(";")) // rozdzielenie na ";"
                                .map(Integer::valueOf)
                                .collect(Collectors.toList());
                        System.out.println(intList);  // wszystkie elementy listy [1, 2, 3]
                        for (int i = 0 ; i < intList.size() ; i+=2){
                            // wypelnianie serii danych danymi z pliku ( ktore sa teraz w liscie intList)
                            series.getData().add(new XYChart.Data(intList.get(i), intList.get(i+1)));
                        }
                        lineChart.getData().add(series); // dodanie serii danych do wykresu
//                        series.getNode().setOnMouseClicked(event1 -> {
//                            //System.out.println(event1.toString());
//                        });
                        lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                            String boo = n.toString().split("'")[1];
                            //System.out.println(boo);
                            //n.toString()
                            ;
                            lineChart.getData().filtered(s -> s.getName().equals(boo))
                                .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
//                            lineChart.lookupAll(".series0").forEach(foo->{
//                                foo.setVisible(!foo.isVisible());
//                                System.out.println(foo);
//                            });
                            //lineChart.lookupAll(".chart-legend-item-symbol").forEach(nn->nn.setVisible(true));

                            //n.setVisible(false);
                        }));

                    }

                } catch (IOException e){
                    System.out.println("Błąd podczas wczytywania pliku");
                }

            }

        });
        root.getChildren().add(wybierzPlik); // dodanie przycisku do glownego panelu Pane


        lineChart.setTitle("Wykresy");

        XYChart.Series series1 = new XYChart.Series(); // seria danych x i y dla pojedynczego wykresu
        series1.setName("Wykres 1");
        String hoo = getClass().getResource("styleFile.css").toExternalForm();
        root.getStylesheets().add(hoo);

        lineChart.setPrefSize(750,500);

        root.getChildren().add(lineChart);

        Button zamknijApp = new Button("Wyjście");
        zamknijApp.setTranslateX(450);
        zamknijApp.setTranslateY(550);
        zamknijApp.setOnAction(event -> {
            stage.close();
        });
        root.getChildren().add(zamknijApp);

        stage.setScene(new Scene(root));
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
