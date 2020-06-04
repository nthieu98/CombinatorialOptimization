import java.io.FileInputStream;
import java.util.Scanner;

public class TwoPhaseSimplex {
	private Fraction[][] tableaux;	// Simplex Tableaux (last row is objective function, last column is value)
	private int[] b;
	private int m;	// No constraints
	private int n;	// No variables

	public TwoPhaseSimplex(Fraction[][] tableaux) {
		m = tableaux.length - 1;
		n = tableaux[0].length - 1;
		this.tableaux = new Fraction[m + 1][n + 1];
		this.b = new int[m + 1];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (tableaux[i][j] == null) {
					System.out.println(i + " " + j);
				}
				this.tableaux[i][j] = new Fraction(tableaux[i][j]);
			}
		}
	}
	
	public Fraction[][] getTableaux() {
		return tableaux;
	}

	public int[] getB() {
		return b;
	}

	// Find a basic feasible solution
	public boolean findBFS() {
		// Simplex tabular of problem 
		Fraction[][] tableaux = new Fraction[m+1][n+m+1];
		int[] b = new int[m];	// Base of constraint

		// Coeffs of actual variables
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				tableaux[i][j] = new Fraction(this.tableaux[i][j]);
			}
		}

		for (int i = 0; i < m; i++) {
			for (int j = n; j < n + m; j++) {
				tableaux[i][j] = new Fraction(0, 1);
			}
			// Coefficients of slack variables
			tableaux[i][n + i] = new Fraction(1, 1);
			// Values of equalities
			tableaux[i][n + m] = new Fraction(this.tableaux[i][n]);
			// Coeffs of objective function
			tableaux[i][n + i] = new Fraction(1, 1);
			b[i] = n + i;
		}

		for (int j = 0; j < n; j++) {
			tableaux[m][j] = new Fraction(0);
		}

		for (int j = n; j < m + n; j++) {
			tableaux[m][j] = new Fraction(-1, 1);
		}

		tableaux[m][n + m] = new Fraction(0);

		printTableaux(tableaux, b);

		Simplex ss = new Simplex(tableaux, b);
		if (!ss.solve()) {
			return false;
		}
		
		b = ss.getB();
		tableaux = ss.getTableaux();

		printTableaux(tableaux, b);

		if (tableaux[m][n + m].compare(0) != 0) {
			System.out.println("BFS is not valid!");
			return false;
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				this.tableaux[i][j] = tableaux[i][j];
			}
			this.tableaux[i][n] = tableaux[i][n + m];
		}

		boolean[] flag = new boolean[n];
		for (int i = 0; i < n; i++) {
			flag[i] = false;
		}
		for (int i = 0; i < m; i++) {
			if (b[i] < n) {
				this.b[i] = b[i];
				flag[b[i]] = true;
			}
		}
		for (int i = 0; i < m; i++) {
			if (b[i] >= n) {
				for (int j = 0; j < n; j++) {
					if (!flag[j]) {
						this.b[i] = j;
						flag[j] = true;
						break;
					}
				}
			}
		}

		return true;
	}

	public boolean solve() {

		System.out.println("==> Finding init BFS");

		if (!findBFS()) {
			System.out.println("Can not find any BFS!");
			return false;
		}

		System.out.println("==> Finding LP optimal solution");

		System.out.println("==> BFS with only origin variables");
		printTableaux(tableaux, b);
	
		Simplex solver = new Simplex(this.tableaux, this.b);
		if (!solver.solve()) {
			System.out.println("Can not solve LP problem (phase 2)!");
			return false;
		}

		Fraction[][] tableaux = solver.getTableaux();
		int[] b = solver.getB();
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				this.tableaux[i][j] = tableaux[i][j];
			}
			this.tableaux[i][n] = tableaux[i][n];
		}
		for (int i = 0; i < m; i++) {
			this.b[i] = b[i];
		}

		System.out.println("==> LP optimum solution");
		printTableaux();
		
		return true;
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
		System.out.println("======");
	}

	public void printTableaux(Fraction[][] tableaux, int[] b) {
		int m = tableaux.length - 1;
		int n = tableaux[0].length - 1;
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
		System.out.println("======");
	}

	public static void main(String[] args) {
		try {
			FileInputStream fi = new FileInputStream("data/tp_01.txt");
			Scanner s = new Scanner(fi);
			int m = s.nextInt();	// No constraints
			int n = s.nextInt();	// No variables
			// Simplex Tableaux 
			// Last row: objective function
			// Last column: value
			Fraction[][] tableaux = new Fraction[m+1][n+1];  
			for (int i = 0; i < m; i++) {
				for (int j = 0; j <= n; j++) {
					tableaux[i][j] = new Fraction(s.nextDouble());
				}
			}

			for (int j = 0; j < n; j++) {
				tableaux[m][j] = new Fraction(s.nextDouble());
			}
			tableaux[m][n] = new Fraction(0);
			TwoPhaseSimplex solver = new TwoPhaseSimplex(tableaux);
			solver.solve();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	

	
}