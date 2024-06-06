import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

public class ColorSensor extends AbstractFilter {
    private float[] sample;

    public ColorSensor(SampleProvider source) {
        super(source);
        sample = new float[sampleSize];
    }

    public float[] getColor() {
        super.fetchSample(sample, 0);
        return sample;
    }

    public boolean isStickyNote() {
        String detectedColor = getDetectedColor();
        return detectedColor.equals("Yellow") || detectedColor.equals("Red") || detectedColor.equals("Green") || detectedColor.equals("Blue");
    }

    public String getDetectedColor() {
        float[] color = getColor();

        if (color[0] > 0.9 && color[1] > 0.9 && color[2] < 0.1) {
            return "Yellow";
        } else if (color[0] > 0.9 && color[1] < 0.1 && color[2] < 0.1) {
            return "Red";
        } else if (color[0] < 0.1 && color[1] > 0.9 && color[2] < 0.1) {
            return "Green";
        } else if (color[0] < 0.1 && color[1] < 0.1 && color[2] > 0.9) {
            return "Blue";
        }
        return "Unknown";
    }

    public void close() {
        ((EV3ColorSensor) super.source).close();
    }
}
