import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.utility.Delay;

import java.util.ArrayList;
import java.util.List;

public class V3Control {

    TouchSensor touch;
    UltrasonicSensor ultrasonic;
    ColorSensor colorSensor;
    GyroSensor gyroSensor;
    MovePilot pilot;
    OdometryPoseProvider posePro;
    Mapping map;
    Pose initialPose;

    public static void main(String[] args) {
        V3Control v3Control = new V3Control();
        v3Control.run();
        v3Control.cleanup();
    }

    public V3Control() {
        pilot = getPilot();
        Brick brick = BrickFinder.getDefault();
        posePro = new OdometryPoseProvider(pilot);
        map = new Mapping(50);

        // Touch sensor config
        Port s1 = brick.getPort("S1");
        EV3TouchSensor tSensor = new EV3TouchSensor(s1);
        touch = new TouchSensor(tSensor);

        // Ultrasonic sensor config
        Port s2 = brick.getPort("S2");
        NXTUltrasonicSensor uSensor = new NXTUltrasonicSensor(s2);
        ultrasonic = new UltrasonicSensor(uSensor.getMode("Distance"));

        // Color sensor config
        Port s3 = brick.getPort("S3");
        EV3ColorSensor cSensor = new EV3ColorSensor(s3);
        colorSensor = new ColorSensor(cSensor.getRGBMode());

        // Gyro sensor config
        Port s4 = brick.getPort("S4");
        EV3GyroSensor gSensor = new EV3GyroSensor(s4);
        gyroSensor = new GyroSensor(gSensor.getAngleMode());
    }

    public void run() {
        // Record initial position
        initialPose = posePro.getPose();

        // Start mapping logic
        pilot.forward();
        while (Button.ESCAPE.isUp() || posePro.getPose() == initialPose) {
            Delay.msDelay(1000);
            map.logPosition(posePro.getPose());

            if (ultrasonic.distance() < 0.3) {
                pilot.stop();
                Delay.msDelay(1000);
                map.logObservation(posePro.getPose(), ultrasonic);
                pilot.rotate(-90);
                pilot.forward();
            }
            if (touch.pressed()) {
                pilot.stop();
                pilot.travel(-50);
                pilot.rotate(-90);
                pilot.forward();
            }
            if (colorSensor.isStickyNote()) {
                map.logObservation(posePro.getPose(), colorSensor);
            }
        }

        // Return to initial position
        navigateTo(initialPose);
        @SuppressWarnings("unused")
        SaveData save = new SaveData(map.getRoute());

        // Show menu and navigate to sticky note based on user selection
        showMenuAndNavigate();
    }

    private void navigateTo(Pose targetPose) {
        Pose currentPose = posePro.getPose();
        float targetX = targetPose.getX();
        float targetY = targetPose.getY();
        float currentX = currentPose.getX();
        float currentY = currentPose.getY();

        float deltaX = targetX - currentX;
        float deltaY = targetY - currentY;
        float distance = (float) Math.hypot(deltaX, deltaY);
        float angle = (float) Math.toDegrees(Math.atan2(deltaY, deltaX)) - currentPose.getHeading();

        pilot.rotate(angle);
        pilot.travel(distance);
        if(ultrasonic.distance() < 0.3)
        {
        	pilot.stop();
        	pilot.rotate(-30);
        	pilot.travel(-30);
        	navigateTo(targetPose);
        }
    }

    private void showMenuAndNavigate() {
        int selectedOption = 0;
        String[] colors = {"Yellow", "Red", "Green", "Blue"};

        while (true) {
            LCD.clear();
            LCD.drawString("Select Color", 0, 0);
            for (int i = 0; i < colors.length; i++) {
                if (i == selectedOption) {
                    LCD.drawString("> " + colors[i], 0, i + 1);
                } else {
                    LCD.drawString("  " + colors[i], 0, i + 1);
                }
            }

            int button = Button.waitForAnyPress();
            if (button == Button.ID_DOWN) {
                selectedOption = (selectedOption + 1) % colors.length;
            } else if (button == Button.ID_UP) {
                selectedOption = (selectedOption - 1 + colors.length) % colors.length;
            } else if (button == Button.ID_ENTER) {
                navigateToStickyNoteByColor(colors[selectedOption]);
                break;
            } else if (button == Button.ID_ESCAPE) {
                break;
            }
        }
    }

    private void navigateToStickyNoteByColor(String color) {
        List<Observation> route = map.getRoute();
        for (Observation obs : route) {
            if (obs.getColor().equalsIgnoreCase(color)) {
                navigateTo(new Pose(obs.getX(), obs.getY(), obs.getHeading()));
                if(colorSensor.getDetectedColor() != color) {
                	LCD.clear();
                	LCD.drawString("Error, object has moved position", 0, 0);
                }
                else {
                	Sound.beep();
                }
                break;
            }
        }
    }

    public void cleanup() {
        touch.close();
        ultrasonic.close();
        colorSensor.close();
        gyroSensor.close();
        System.exit(0);
    }

    public MovePilot getPilot() {
        Wheel wheelL = WheeledChassis.modelWheel(Motor.A, 43.2).offset(68.4);
        Wheel wheelR = WheeledChassis.modelWheel(Motor.B, 43.2).offset(-68.4);
        Chassis chassis = new WheeledChassis(new Wheel[]{wheelL, wheelR}, WheeledChassis.TYPE_DIFFERENTIAL);
        return pilotConfig(new MovePilot(chassis));
    }

    public MovePilot pilotConfig(MovePilot pilot) {
        pilot.setLinearSpeed(100);
        pilot.setLinearAcceleration(100);
        pilot.setAngularSpeed(100);
        pilot.setAngularAcceleration(100);
        return pilot;
    }
}
