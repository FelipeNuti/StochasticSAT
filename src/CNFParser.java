/**
 * Compilation: javac CNFParser.java
 * Execution: java CNFParser inputName
 * Data file: cnfSatBenchmark
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The {@code CNFParser} class takes a file in DIMACS cnf format and converts the logical expressions into the format used
 * by <em>ComputationGraph</em>.
 *
 * @author Felipe Nuti
 */
public class CNFParser {
    CNFParser(String filename) throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        int vars, clauses;
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty() || line.charAt(0) == 'c') continue;
            else if (line.charAt(0) == 'p') {
                Scanner tempSc = new Scanner(line.substring(6));
                vars = tempSc.nextInt();
                clauses = tempSc.nextInt();
                System.out.println(vars);
            } else {
                Scanner tempSc = new Scanner(line);
                int x = tempSc.nextInt();
                while (tempSc.hasNextInt()) {
                    if (x < 0) System.out.printf("~$%d ", -x);
                    else System.out.printf("$%d ", x);
                    x = tempSc.nextInt();
                    if (x == 0) {
                        System.out.println();
                        break;
                    } else System.out.printf("v ");
                }
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        CNFParser cnfPartser = new CNFParser(args[0]);
    }
}
