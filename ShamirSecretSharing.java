import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShamirSecretSharing {

    public static void main(String[] args) throws Exception {
        // Step 1: Read JSON file
        String content = new String(Files.readAllBytes(Paths.get("input.json")));
        JSONObject jsonObject = new JSONObject(content);

        // Step 2: Parse number of roots 'n' and minimum number of roots 'k'
        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Step 3: Parse x, y points from the input and decode the y-values from different bases
        List<Point> points = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (jsonObject.has(String.valueOf(i))) {
                JSONObject pointData = jsonObject.getJSONObject(String.valueOf(i));
                int base = Integer.parseInt(pointData.getString("base"));
                String valueStr = pointData.getString("value");
                long decodedY = decodeValue(valueStr, base);  // Decode the y-value
                points.add(new Point(i, decodedY));  // Assuming x values are indices (1, 2, 3,...)
            }
        }

        // Step 4: Apply Lagrange Interpolation to find the constant term 'c'
        long constantTerm = lagrangeInterpolation(points, 0);
        System.out.println("The constant term 'c' is: " + constantTerm);
    }

    // Function to decode a value from a specific base
    private static long decodeValue(String value, int base) {
        return Long.parseLong(value, base);  // Convert the value from the given base
    }

    // Class to represent a point (x, y)
    static class Point {
        public int x;
        public long y;

        public Point(int x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    // Lagrange Interpolation to find the value at x = 0 (constant term)
    private static long lagrangeInterpolation(List<Point> points, int x) {
        int k = points.size();
        long result = 0;

        for (int i = 0; i < k; i++) {
            long term = points.get(i).y;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    term *= (x - points.get(j).x);
                    term /= (points.get(i).x - points.get(j).x);
                }
            }
            result += term;
        }

        return result;
    }
}
