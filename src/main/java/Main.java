import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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

public class Main extends Application  {

    private Pane root = new Pane();
    private File selectedFile;
    private List<Double> dataList = new ArrayList<>();
    private List<Double> allDataList = new ArrayList<>();
    private boolean lineScaleX = true;
    private boolean lineScaleY = true;
    private XYChart.Series series;
    private boolean logXScaleAva = true;
    private boolean logYScaleAva = true;


    @Override public void start(Stage stage) {
        stage.setTitle("Java projekt A W");
        root.setPrefSize(1000, 600); // wymiary glownego okna

        final NumberAxis xAxis = new NumberAxis(); // ustawienie osi x jako numerycznej
        final NumberAxis yAxis = new NumberAxis();  // to samo dla osi y

        xAxis.setLabel("Oś X"); // nazwy
        yAxis.setLabel("Oś Y");

        final LineChart<Number,Number> lineChart =
                new LineChart<Number,Number>(xAxis,yAxis); // wykres liniowy - numeryczny, na dwoch osiach

        lineChart.setCreateSymbols(false);

        FileChooser fileChooser = new FileChooser(); // file choose do oblsugi okienka wyboru pliku z danymi do wczytania
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")); // filter ktory pokazuje tylko pliki .txt podczas wyboru

        Button wybierzPlik = new Button("Wybierz plik"); // przycisk wyboru pliku
        wybierzPlik.setTranslateX(50); // lokacja przycisku w osi x
        wybierzPlik.setTranslateY(500); // - || - w osi y
        wybierzPlik.setOnAction(event -> { // akcja po kliknieciu
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null) {
                System.out.println("Kliknieto Anuluj");
            } else {
                lineChart.getData().clear(); // podczas otwierania nowych danych usuwa poprzednie wykresy
                allDataList.clear(); // usuwa zawartosc listy
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

                    String line;
                    int x = 0;
                    while ((line = reader.readLine()) != null){
                        x++;
                        series = new XYChart.Series(); // nowa linia tekstu = nowa seria danych

                        series.setName("Wykres " + x);
                        dataList = Stream // konwersja pojedynczej linii na liste
                                .of(line.split(";")) // rozdzielenie na ";"
                                .map(Double::valueOf)
                                .collect(Collectors.toList());
                        if (dataList.size() > 20) { // Jeżeli w 1 linii znajduje sie wiecej niz 10 par danych to okienko error
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Plik jest niepoprawny - zawiera więcej niż 10 par danych", ButtonType.OK);
                            alert.showAndWait();
                            if (alert.getResult() == ButtonType.OK) {
                            }
                        } else {
                            allDataList.addAll(dataList);
                            //System.out.println(intList);  // wszystkie elementy listy [1, 2, 3]
                            for (int i = 0 ; i < dataList.size() ; i+=2){
                                if (dataList.get(i) < 0){   // Jezeli wartosc ktoregos z X'ow jest ujemna to nie jest mozlwia konwersja na log
                                    logXScaleAva = false;
                                }
                                if (dataList.get(i+1) < 0){ // Jezeli wartosc ktoregos z Y'ow jest ujemna to nie jest mozlwia konwersja na log
                                    logYScaleAva = false;
                                }
                                // wypelnianie serii danych danymi z pliku ( ktore sa teraz w liscie intList)
                                series.getData().add(new XYChart.Data(dataList.get(i), dataList.get(i+1)));
                            }
                            lineChart.getData().add(series); // dodanie serii danych do wykresu
                            lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                                String boo = n.toString().split("'")[1];
                                lineChart.getData().filtered(s -> s.getName().equals(boo))
                                        .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                            }));
                        }
                    }
                } catch (IOException e){
                    System.out.println("Błąd podczas wczytywania pliku");
                }
                lineScaleX = true;
                lineScaleY = true;
            }
        });
        root.getChildren().add(wybierzPlik); // dodanie przycisku do glownego panelu Pane

        Button toLogAxisX = new Button("Skala logarytmiczna X");
        toLogAxisX.setTranslateX(410); // lokacja przycisku w osi x
        toLogAxisX.setTranslateY(510); // - || - w osi y
        toLogAxisX.setOnAction(event -> {
            int nr = 0;
            if (!lineScaleX) return; // jezeli jest juz skala log to return

            if (!logXScaleAva){ // jezeli nie jest mozliwa konwersja to error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Z powodu ujemnych wartosci na osi X konwersja na skale logarytmiczna nie jest możliwa", ButtonType.OK);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                }
            } else {
                int k =0;
                nr = 1;
                lineChart.getData().clear();
                for (int j = 0; j < allDataList.size(); j+=20){
                    XYChart.Series series2 = new XYChart.Series();
                    for (k = j; k < j+20; k+=2){
                        System.out.println("X: " + Math.log10(allDataList.get(k)) + " Y: " + allDataList.get(k+1));
                        series2.getData().add(new XYChart.Data(Math.log10(allDataList.get(k)), allDataList.get(k+1)));
                    }
                    series2.setName("Wykres " + nr);
                    lineChart.getData().add(series2);
                    nr++;
                    lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                        String boo = n.toString().split("'")[1];
                        lineChart.getData().filtered(s -> s.getName().equals(boo))
                                .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                    }));
                }
                lineScaleX = false;
            }
        });
        root.getChildren().add(toLogAxisX);

        Button toLineAxisX = new Button("Skala liniowa X");
        toLineAxisX.setTranslateX(550); // lokacja przycisku w osi x
        toLineAxisX.setTranslateY(510); // - || - w osi y
        toLineAxisX.setOnAction(event -> {
            int nr = 0;
            if (lineScaleX) return; // jezeli jest juz skala liniowa to return

                int k =0;
                nr = 1;
                lineChart.getData().clear();
                for (int j = 0; j < allDataList.size(); j+=20){
                    XYChart.Series series2 = new XYChart.Series();
                    for (k = j; k < j+20; k+=2){
                        series2.getData().add(new XYChart.Data(allDataList.get(k), allDataList.get(k+1)));
                    }
                    series2.setName("Wykres " + nr);
                    lineChart.getData().add(series2);
                    nr++;
                    lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                        String boo = n.toString().split("'")[1];
                        lineChart.getData().filtered(s -> s.getName().equals(boo))
                                .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                    }));
                }
                lineScaleX = true;
        });
        root.getChildren().add(toLineAxisX);

        Button toLogAxisY = new Button("Skala logarytmiczna Y");
        toLogAxisY.setTranslateX(30); // lokacja przycisku w osi x
        toLogAxisY.setTranslateY(200); // - || - w osi y
        toLogAxisY.setOnAction(event -> {
            int nr = 0;
            if (!lineScaleY) return; // jezeli jest juz skala log to return

            if (!logYScaleAva){ // jezeli nie jest mozliwa konwersja to error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Z powodu ujemnych wartosci na osi Y konwersja na skale logarytmiczna nie jest możliwa", ButtonType.OK);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.OK) {
                }
            } else {
                int k =0;
                nr = 1;
                lineChart.getData().clear();
                for (int j = 0; j < allDataList.size(); j+=20){
                    XYChart.Series series2 = new XYChart.Series();
                    for (k = j; k < j+20; k+=2){
                        series2.getData().add(new XYChart.Data(allDataList.get(k), Math.log10(allDataList.get(k+1))));
                    }
                    series2.setName("Wykres " + nr);
                    lineChart.getData().add(series2);
                    nr++;
                    lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                        String boo = n.toString().split("'")[1];
                        lineChart.getData().filtered(s -> s.getName().equals(boo))
                                .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                    }));
                }
                lineScaleY = false;
            }
        });
        root.getChildren().add(toLogAxisY);

        Button toLineAxisY = new Button("Skala liniowa Y");
        toLineAxisY.setTranslateX(30); // lokacja przycisku w osi x
        toLineAxisY.setTranslateY(235); // - || - w osi y
        toLineAxisY.setOnAction(event -> {
            int nr = 0;
            if (lineScaleY) return; // jezeli jest juz skala log to return
                int k =0;
                nr = 1;
                lineChart.getData().clear();
                for (int j = 0; j < allDataList.size(); j+=20){
                    XYChart.Series series2 = new XYChart.Series();
                    for (k = j; k < j+20; k+=2){
                        series2.getData().add(new XYChart.Data(allDataList.get(k), Math.log10(allDataList.get(k+1))));
                    }
                    series2.setName("Wykres " + nr);
                    lineChart.getData().add(series2);
                    nr++;
                    lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                        String boo = n.toString().split("'")[1];
                        lineChart.getData().filtered(s -> s.getName().equals(boo))
                                .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                    }));
                }
                lineScaleY = true;
        });
        root.getChildren().add(toLineAxisY);

        lineChart.setTitle("Wykresy");

        XYChart.Series series1 = new XYChart.Series(); // seria danych x i y dla pojedynczego wykresu
        series1.setName("Wykres 1");
        String hoo = getClass().getResource("styleFile.css").toExternalForm(); // plik z stylami linii
        root.getStylesheets().add(hoo); // pobranie stylow

        lineChart.setPrefSize(750,500);
        lineChart.setTranslateX(150);

        root.getChildren().add(lineChart);

        Button zamknijApp = new Button("Wyjście");
        zamknijApp.setTranslateX(50);
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
