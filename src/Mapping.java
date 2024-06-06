import lejos.robotics.navigation.Pose;

import java.util.ArrayList;
import java.util.List;

public class Mapping {
    int mapRes;
    List<Observation> mapData;

    public Mapping(int resolution) {
        mapRes = resolution;
        mapData = new ArrayList<>();
    }

    public void logPosition(Pose pose) {
        mapData.add(new Observation(pose.getX(), pose.getY(), pose.getHeading(), "None"));
    }

    public void logObservation(Pose pose, UltrasonicSensor sensor) {
        float x = pose.getX();
        float y = pose.getY();
        float heading = pose.getHeading();

        x += (float) Math.cos(Math.toRadians(heading)) * sensor.distance() * 1000;
        y += (float) Math.sin(Math.toRadians(heading)) * sensor.distance() * 1000;
        mapData.add(new Observation(x, y, heading, "Obstacle"));
    }

    public void logObservation(Pose pose, ColorSensor sensor) {
        float x = pose.getX();
        float y = pose.getY();
        float heading = pose.getHeading();
        String color = sensor.getDetectedColor();

        x += (float) Math.cos(Math.toRadians(heading)) * 10; // Assuming the sensor is 10cm ahead of the robot
        y += (float) Math.sin(Math.toRadians(heading)) * 10;
        mapData.add(new Observation(x, y, heading, color));
    }

    public List<Observation> getRoute() {
        return mapData;
    }
}
