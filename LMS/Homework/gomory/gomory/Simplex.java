import java.io.FileInputStream;
import java.util.Scanner;

public class Simplex {

    private Fraction [][] tableaux; // Simplex Tableaux
    private int[] b; // Base of i-th constraint
    private int m;  // No constraints
    private int n;  // No variables

    public Fraction[][] getTableaux() {
        return tableaux;
    }

    public int[] getB() {
        return b;
    }
    
    public Simplex (Fraction[][] tableaux, int[] b) {
        m = tableaux.length - 1;
        n = tableaux[0].length - 1;
        this.tableaux = new Fraction[m + 1][n + 1];
        this.b = new int[m];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                this.tableaux[i][j] = new Fraction(tableaux[i][j]);
            }
        }
        for (int i = 0; i < m; i++) {
            this.b[i] = b[i];
        }
    }

    public void pivot(int p, int q) {
        b[p] = q;
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i != p && j != q) {
                    tableaux[i][j] = tableaux[i][j].minus(tableaux[p][j].multiply(tableaux[i][q]).divide(tableaux[p][q]));
                }
            }
        }
        for (int i = 0; i <= m; i++) {
            if (i != p) {
                tableaux[i][q] = new Fraction(0, 1);
            }
        }
        for (int j = 0; j <= n; j++) {
            if (j != q) {
                tableaux[p][j] = tableaux[p][j].divide(tableaux[p][q]);
            }
        }
        tableaux[p][q] = new Fraction(1, 1);
    }

    public boolean solve() {
        this.standardize();
        boolean solvable;
        while (true) {
            int p, q;
            for (q = 0; q < n; q++) {
                if (tableaux[m][q].compare(0) > 0) {
                    break;
                }
            }
            if (q >= n) {
                solvable = true;
                break;
            }

            for (p = 0; p < m; p++) {
                if (tableaux[p][q].compare(0) > 0) {
                    break;
                }
            }
            
            if (p >= m) {
                solvable = false;
                break;
            }
            for (int i= p + 1; i < m; i++) {
                if (tableaux[i][q].compare(0) > 0) {
                    if (tableaux[i][n].divide(tableaux[i][q]).compare(tableaux[p][n].divide(tableaux[p][q])) < 0) {
                        p = i;
                    }
                }
            }
            pivot(p, q);
        }
        
        return solvable;
    }

    public void standardize() {

        // RHS > 0
        for (int i = 0; i < m; i++) {
            if (tableaux[i][n].compare(0) < 0) {
                for (int j = 0; j <= n; j++) {
                    tableaux[i][j] = tableaux[i][j].negative();
                }
            }
        }

        // Basic coefficient = 1
        for (int i = 0; i < m; i++) {
            // System.out.println("Check: " + i + " " + b[i] + " " + n);
            Fraction c = tableaux[i][b[i]];
            for (int j = 0; j <= n; j++) {
                tableaux[i][j] = tableaux[i][j].divide(c);
            }
        }

        // Initialize value of basic variables
        Fraction[] v = new Fraction[n];
        for (int j = 0; j < n; j++) {
            v[j] = new Fraction(0);
        }
        for (int i = 0; i < m; i++) {
            v[b[i]] = tableaux[i][n];   // All non-basic variables equal 0
        }

        for (int i = 0; i < m; i++) {
            Fraction c = tableaux[m][b[i]].divide(tableaux[i][b[i]]);
            for (int j = 0; j <= n; j++) {
                tableaux[m][j] = tableaux[m][j].minus(c.multiply(tableaux[i][j]));
            }
        }
        System.out.println("====> Standardized");
        printTableaux();

    }
    
    public void printTableaux() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(tableaux[i][j] + "\t");
            }
            System.out.println(" | " + tableaux[i][n] + "\tBase: " + b[i]);
        }

        for (int j = 0; j < n; j++) {
            System.out.print(tableaux[m][j] + "\t");
        }
        System.out.println(" | " + tableaux[m][n]);
        System.out.println();
    }

    public static void main(String[] args) {
        try {
            FileInputStream fi = new FileInputStream("data/s_01.txt");
            Scanner s = new Scanner(fi);
            int m = s.nextInt();    // No constraints
            int n = s.nextInt();    // No variables
            // Simplex Tableaux 
            // Last row: objective function
            // Last column: value
            Fraction[][] tableaux = new Fraction[m+1][n+1]; 
            int[] b = new int[m];              // basic variable of i-th constraint
            for (int i = 0; i < m; i++) {
                for (int j = 0; j <= n; j++) {
                    tableaux[i][j] = new Fraction(s.nextDouble());
                }
                b[i] = s.nextInt();
            }


            for (int j = 0; j < n; j++) {
                tableaux[m][j] = new Fraction(s.nextDouble());
            }

            tableaux[m][n] = new Fraction(0);
        
            Simplex solver = new Simplex(tableaux, b);
            solver.solve();
            System.out.println(solver.getTableaux()[m][n]);
        }
        catch(Exception e) {
            e.printStackTrace();
        }  
    }


}