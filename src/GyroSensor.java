import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.filter.AbstractFilter;

public class GyroSensor extends AbstractFilter {
    private float[] sample;

    public GyroSensor(SampleProvider source) {
        super(source);
        sample = new float[sampleSize];
    }

    public float getAngle() {
        super.fetchSample(sample, 0);
        return sample[0];
    }

    public void close() {
        ((EV3GyroSensor) super.source).close();
    }
}
