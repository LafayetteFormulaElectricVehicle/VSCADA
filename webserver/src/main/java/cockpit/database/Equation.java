package cockpit.database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by CraigLombardo on 4/13/17.
 */
public class Equation {

    public String destination;
    public String equation;
    public ArrayList<String> tags;
    public ArrayList<EquationOperation> operations;

    public Equation(String dest, String equation) {
        destination = dest;
        this.equation = equation;
        tags = new ArrayList<>();
        operations = new ArrayList<>();

        parseEquation(equation);
    }


//    public static void server.main(String[] args) {
//        HashMap<String, Double> map = new HashMap<>();
//        map.put("v1", 20.0);
//        map.put("v2", 25.0);
//        map.put("a1", 6.0);
//        map.put("a2", 4.0);
//        map.put("random", 2.0);
//        Equation e;
//
//        e = new Equation("p1", "v1*a1");
////        System.out.println(e.evaluate(map));
//        map.put(e.destination, e.evaluate(map));
//
//        e = new Equation("p2", "a2*v2");
////        System.out.println(e.evaluate(map));
//        map.put(e.destination, e.evaluate(map));
//
//        e = new Equation("avg_power", "p1+p2/2");
////        System.out.println(e.evaluate(map));
//        map.put(e.destination, e.evaluate(map));
//
//        e = new Equation("2*avg_power", "random*avg_power");
////        System.out.println(e.evaluate(map));
//        map.put(e.destination, e.evaluate(map));
//
//        System.out.println(map);
//    }

    //HashMap<String, Double> map
    public Double evaluate(HashMap<String, Sensor> map) {
        if (map == null) return null;
        int size = tags.size();
        if (size == 0) return null;

        Double output = 0.0;
        Double value;
        Sensor s;

        for (int i = 0; i < size; i++) {
            s = map.get(tags.get(i));
            if (s == null) {
                try {
                    value = Double.parseDouble(tags.get(i));
                } catch (Exception e) {
//                    System.out.println(tags.get(i));
                    return null;
                }
            } else try {
                value = Double.parseDouble(s.getCalibValue());
            } catch (Exception e) {
//                System.out.println(s.getTag());
                return null;
            }

            if (i == 0) output += value;
            else switch (operations.get(i - 1)) {
                case ADD:
                    output += value;
                    break;
                case SUBTRACT:
                    output -= value;
                    break;
                case DIVIDE:
                    output /= value;
                    break;
                case MULTIPLY:
                    output *= value;
                    break;
                default:
                    break;
            }
        }

        return Math.round(100.0 * output) / 100.0;

    }

    public void parseEquation(String equation) {
        int start = 0;
        boolean match;

        for (int end = 0; end < equation.length() - 1; end++) {
            String c = equation.substring(end, end + 1);
            match = false;
            if (c.equals("+")) {
                operations.add(EquationOperation.ADD);
                match = true;
            } else if (c.equals("-")) {
                operations.add(EquationOperation.SUBTRACT);
                match = true;
            } else if (c.equals("/")) {
                operations.add(EquationOperation.DIVIDE);
                match = true;
            } else if (c.equals("*")) {
                operations.add(EquationOperation.MULTIPLY);
                match = true;
            }
            if (match) {
                tags.add(equation.substring(start, end));
                start = end + 1;
            }
        }
        tags.add(equation.substring(start));

    }

    public enum EquationOperation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

}
