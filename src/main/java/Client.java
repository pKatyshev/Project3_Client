import models.Measurement;
import models.MeasurementResponse;
import models.Sensor;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.IntStream;

public class Client {
    private final RestTemplate restTemplate = new RestTemplate();
    private final Sensor sensor = new Sensor();

    public void registerSensor(String sensorName) {
        sensor.setName(sensorName);

        String urlRegister = "http://localhost:8088/sensors/registration";
        String response = restTemplate.postForObject(urlRegister, sensor, String.class);
        System.out.println("Response: " + response);
    }

    public void work() {
        try {
            for (int i = 0; i < 100; i++) {
                sendMeasurement(getRandomMeasurement());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMeasurement(Measurement measurement) {
        String url = "http://localhost:8088/measurements/add";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(measurement, headers);
        restTemplate.postForObject(url, request, String.class);
        System.out.println("Показания отправлены");
    }

    public Measurement getRandomMeasurement() {
        Random random = new Random();
        Measurement measurement = new Measurement();

        measurement.setValue(getRandomValue());
        measurement.setRaining(random.nextBoolean());
        measurement.setSensor(sensor);

        return measurement;
    }

    public double getRandomValue() {
        double value = (Math.random() * 200) - 100;
        BigDecimal result = new BigDecimal(value);
        return result.setScale(1, RoundingMode.CEILING).doubleValue();
    }

    public void getRainingDaysCount() {
        String url = "http://localhost:8088/measurements/rainyDaysCount";

        int count = restTemplate.getForObject(url, Integer.class);
        System.out.println("Raining days count: " + count);
    }

    public List<Measurement> getAllMeasurements() {
        List<Measurement> result = new ArrayList<>();
        String url = "http://localhost:8088/measurements";
        MeasurementResponse response = restTemplate.getForObject(url, MeasurementResponse.class);

        if (response == null) return result;

        return response.getMeasurements();
    }

    public void chart(List<Measurement> list) {
        List<Double> arr = list.stream().map(Measurement::getValue).toList();
        double[] xData = IntStream.range(0, arr.size()).asDoubleStream().toArray();
        double[] yData = arr.stream().mapToDouble(e -> e).toArray();

        XYChart chart = QuickChart.getChart("MyTemperatureChart", "Days", "Temperature", "line", xData, yData);

        new SwingWrapper(chart).displayChart();
    }
}
