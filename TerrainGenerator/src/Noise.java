import java.util.Random;

//Noise Generation Class
public final class Noise {

    //Generates a basic completely random map of doubles
    public static double[][] generateWhiteNoise(int width, int height, long seed){
        Random random = new Random(seed);
        double[][] noise = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                noise[x][y] = random.nextDouble() % 1;
            }
        }

        return noise;
    }

    //Smooths a 2D array of doubles
    private static double[][] generateSmoothedNoise(double[][] baseNoise, int octave){
        int w = baseNoise.length;
        int h = baseNoise[0].length;
        double[][] smoothedNoise = new double[w][h];

        int samplePeriod = 1 << octave;
        double sampleFrequency = 1.0f / samplePeriod;

        for (int x = 0; x < w; x++) {

            //Samples for the X axis
            int sample_x0 = (x / samplePeriod) * samplePeriod;
            int sample_x1 = (sample_x0 + samplePeriod) % w;
            //Value of blend
            double horizontal_blend = (x - sample_x0) * sampleFrequency;

            for (int y = 0; y < h; y++) {

                //Samples for the Y axis
                int sample_y0 = (y / samplePeriod) * samplePeriod;
                int sample_y1 = (sample_y0 + samplePeriod) % h;
                //Value of Blend
                double vertical_blend = (y - sample_y0) * sampleFrequency;

                double top = interpolate(baseNoise[sample_x0][sample_y0], baseNoise[sample_x1][sample_y0], horizontal_blend);
                double bottom = interpolate(baseNoise[sample_x0][sample_y1], baseNoise[sample_x1][sample_y1], horizontal_blend);

                smoothedNoise[x][y] = interpolate(top, bottom, vertical_blend);
                //Interpolates from four corners
            }
        }
        return smoothedNoise;
    }

    //Smooths a baseNoise array over a number of octaves with a certain level of persistance
    public static double[][] generateNoiseMap(double[][] baseNoise, int octaveCount, float persistance){
        int w = baseNoise.length;
        int h = baseNoise[0].length;

        double [][][] smoothNoise = new double[octaveCount][][];

        for (int i = 0; i < octaveCount; i++) {
            smoothNoise[i] = generateSmoothedNoise(baseNoise, i);
        }

        double[][] perlinNoise = new double[w][h];
        double amplitude = 1.0;
        double totalAmplitude = 0.0d;

        for (int o = octaveCount - 1; o >= 0; o--) {
            amplitude *= persistance;
            totalAmplitude += amplitude;

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    //Times by amplitude to go from double between 1 and 0 to double between 0 and amplitude
                    perlinNoise[x][y] += smoothNoise[o][x][y] * amplitude;
                }
            }
        }

        //This normalises the heights back to between 1 and 0
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                perlinNoise[x][y] /= totalAmplitude;
            }
        }
        return perlinNoise;
    }

    //Standard interpolation function
    private static double interpolate(double a, double b, double t){
        return a + (b - a) * t;
    }
}
