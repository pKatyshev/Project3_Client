
public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        client.registerSensor("sensor_810");
        client.work();
        client.getRainingDaysCount();

        client.chart(client.getAllMeasurements());
    }
}
