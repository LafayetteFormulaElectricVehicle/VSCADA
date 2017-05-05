package cockpit.database;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <h1>Equation</h1>
 * This class serves to house the information about all of the Equations. Remember, the equations can be either
 * tag based, number/decimal based, or both. Order of operations if <b>Left to right <i>not</i> PEMDAS</b>
 *
 * @author Craig Lombardo
 * @version 1.0
 * @since 2017-04-13
 */
public class Equation {

    public String destination; /** The destination of the Equation (tag) */
    public String equation; /** The equation to be evaluated during execution*/
    private ArrayList<String> tags;
    private ArrayList<EquationOperation> operations;

    /**
     * The constructor for creating a new Equation
     * @param dest The destination (as a tag) that you would like to store the evaluated result into the DB with.
     * @param equation The equation to be evaluated by the device during runtime.
     */
    public Equation(String dest, String equation) {
        destination = dest;
        this.equation = equation;
        tags = new ArrayList<>();
        operations = new ArrayList<>();

        parseEquation(equation);
    }

    /**
     * This method will evaluate the equation with the map information it is provided with.
     * @param map A map containing Strings (tags) as it's keys and Sensors as it's values.
     * @return The value of the evaluated expression
     */
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

    private void parseEquation(String equation) {
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

    /**
     * The enumerations for Equation operations.
     */
    public enum EquationOperation {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

}
