import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.Styler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends Application implements ExampleChart<org.knowm.xchart.XYChart> {

    private Pane root = new Pane();
    private File selectedFile;
    private List<Double> dataList = new ArrayList<>();
    private boolean firstConversion = true;
    private XYChart.Series series;

    @Override public void start(Stage stage) {
        stage.setTitle("Java projekt A W");
        root.setPrefSize(1000, 600); // wymiary glownego okna

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
        wybierzPlik.setTranslateX(50); // lokacja przycisku w osi x
        wybierzPlik.setTranslateY(500); // - || - w osi y
        wybierzPlik.setOnAction(event -> { // akcja po kliknieciu
            selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile == null) {
                System.out.println("Kliknieto Anuluj");
            } else {
                lineChart.getData().clear(); // podczas otwierania nowych danych usuwa poprzednie wykresy
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

                    String line;
                    int x = 0;
                    while ((line = reader.readLine()) != null){
                        x++;
                        System.out.println(x);
                        System.out.println(line); // linia przed konwersja
                        series = new XYChart.Series(); // nowa linia tekstu = nowa seria danych

                        series.setName("Wykres " +x);
                        /*List<Integer> intList = Stream // konwersja pojedynczej linii na liste
                                .of(line.split(";")) // rozdzielenie na ";"
                                .map(Integer::valueOf)
                                .collect(Collectors.toList());*/

                        dataList = Stream // konwersja pojedynczej linii na liste
                                .of(line.split(";")) // rozdzielenie na ";"
                                .map(Double::valueOf)
                                .collect(Collectors.toList());

                        //System.out.println(intList);  // wszystkie elementy listy [1, 2, 3]
                        System.out.println(dataList);
                        for (int i = 0 ; i < dataList.size() ; i+=2){
                            // wypelnianie serii danych danymi z pliku ( ktore sa teraz w liscie intList)
                            //series.getData().add(new XYChart.Data(intList.get(i), intList.get(i+1)));
                            series.getData().add(new XYChart.Data(dataList.get(i), dataList.get(i+1)));
                        }
                        lineChart.getData().add(series); // dodanie serii danych do wykresu

                        /*lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                            String boo = n.toString().split("'")[1];
                            lineChart.getData().filtered(s -> s.getName().equals(boo))
                                .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                        }));*/
                    }
                } catch (IOException e){
                    System.out.println("Błąd podczas wczytywania pliku");
                }
            }
        });
        lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
            String boo = n.toString().split("'")[1];
            lineChart.getData().filtered(s -> s.getName().equals(boo))
                    .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
        }));
        root.getChildren().add(wybierzPlik); // dodanie przycisku do glownego panelu Pane

        Button toLogAxisX = new Button("Skala logarytmiczna X");
        toLogAxisX.setTranslateX(410); // lokacja przycisku w osi x
        toLogAxisX.setTranslateY(510); // - || - w osi y
        toLogAxisX.setOnAction(event -> {
            if (firstConversion = false) return;


            final LineChart<Number,Number> convertedLineChart =
                    new LineChart<Number,Number>(xAxis,yAxis);

            for (int i = 0; i < lineChart.getData().size(); i++){
                for (int j = 0; j < dataList.size(); j++){
                    //series.getData().set(dataList.get(j), dataList)
                }

            }
            //int x =lineChart.getData().size();
            //System.out.println(x);
           // lineChart.getData().clear();


        });
        root.getChildren().add(toLogAxisX);

        Button toLineAxisX = new Button("Skala liniowa X");
        toLineAxisX.setTranslateX(550); // lokacja przycisku w osi x
        toLineAxisX.setTranslateY(510); // - || - w osi y

        root.getChildren().add(toLineAxisX);

        Button toLogAxisY = new Button("Skala logarytmiczna Y");
        toLogAxisY.setTranslateX(30); // lokacja przycisku w osi x
        toLogAxisY.setTranslateY(200); // - || - w osi y

        root.getChildren().add(toLogAxisY);

        Button toLineAxisY = new Button("Skala liniowa Y");
        toLineAxisY.setTranslateX(30); // lokacja przycisku w osi x
        toLineAxisY.setTranslateY(235); // - || - w osi y

        root.getChildren().add(toLineAxisY);

        lineChart.setTitle("Wykresy");

        XYChart.Series series1 = new XYChart.Series(); // seria danych x i y dla pojedynczego wykresu
        series1.setName("Wykres 1");
        String hoo = getClass().getResource("styleFile.css").toExternalForm();
        root.getStylesheets().add(hoo);

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

    public void addNewLine(){

    }
/*    public void readDataFromFile(LineChart lineChart){
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {

            String line;
            int x = 0;
            while ((line = reader.readLine()) != null){
                x++;
                System.out.println(x);
                System.out.println(line); // linia przed konwersja
                XYChart.Series series = new XYChart.Series(); // nowa linia tekstu = nowa seria danych

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

                lineChart.lookupAll("Label.chart-legend-item").forEach(n->n.setOnMouseClicked(event1 -> {
                    String boo = n.toString().split("'")[1];
                    lineChart.getData().filtered(s -> s.getName().equals(boo))
                            .forEach(foo->foo.getNode().setVisible(!foo.getNode().isVisible()));
                }));
            }

        } catch (IOException e){
            System.out.println("Błąd podczas wczytywania pliku");
        }
    }*/

    @Override
    public org.knowm.xchart.XYChart getChart() {
        // generates Log data
        List<Integer> xData = new ArrayList<Integer>();
        List<Double> yData = new ArrayList<Double>();
        for (int i = -3; i <= 3; i++) {
            xData.add(i);
            yData.add(Math.pow(10, i));
        }
        // Create Chart
        org.knowm.xchart.XYChart chart = new XYChartBuilder().width(750).height(500).title("Powers of Ten").xAxisTitle("Power").yAxisTitle("Value").build();

        // Customize Chart
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setYAxisLogarithmic(true);
        chart.getStyler().setXAxisLabelRotation(45);

        // chart.getStyler().setXAxisLabelAlignment(TextAlignment.Right);
        // chart.getStyler().setXAxisLabelRotation(90);
        // chart.getStyler().setXAxisLabelRotation(0);

        // Series
        chart.addSeries("10^x", xData, yData);
        return chart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
