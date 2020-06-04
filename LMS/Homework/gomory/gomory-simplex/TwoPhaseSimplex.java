import java.io.FileInputStream;
import java.util.Scanner;

public class TwoPhaseSimplex {
	private Fraction[][] tableau; // Simplex Tableaux (last row is objective function, last column is value)
	private int[] b;
	private int m; // No constraints
	private int n; // No variables

	public TwoPhaseSimplex(Fraction[][] tableau) {
		m = tableau.length - 1;
		n = tableau[0].length - 1;
		this.tableau = new Fraction[m + 1][n + 1];
		this.b = new int[m + 1];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (tableau[i][j] == null) {
					System.out.println(i + " " + j);
				}
				this.tableau[i][j] = new Fraction(tableau[i][j]);
			}
		}
	}
	
	public Fraction[][] gettableau() {
		return tableau;
	}

	public int[] getB() {
		return b;
	}

	// Find a basic feasible solution
	public boolean findBFS() {
		// Simplex tabular of problem with slack variables
		Fraction[][] tableau = new Fraction[m + 1][n + m + 1];
		int[] b = new int[m]; // Base of constraint

		// Coefficients of actual variables
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				tableau[i][j] = new Fraction(this.tableau[i][j]);
			}
		}

		for (int i = 0; i < m; i++) {
			for (int j = n; j < n + m; j++) {
				tableau[i][j] = new Fraction(0, 1);
			}
			// Coefficients of slack variables
			tableau[i][n + i] = new Fraction(1, 1);
			// Values of equalities
			tableau[i][n + m] = new Fraction(this.tableau[i][n]);
			// Coefficients of objective funtion
			tableau[i][n + i] = new Fraction(1, 1);
			// Base of constraint is slack variable
			b[i] = n + i;
		}

		for (int j = 0; j < n; j++) {
			tableau[m][j] = new Fraction(0);
		}

		for (int j = n; j < m + n; j++) {
			tableau[m][j] = new Fraction(-1, 1);
		}

		tableau[m][n + m] = new Fraction(0);

		System.out.println("====> Fake BFS with slack variables");
		printTableau(tableau, b);

		Simplex ss = new Simplex(tableau, b);
		if (!ss.solve()) {
			return false;
		}

		b = ss.getB();
		tableau = ss.gettableau();

		System.out.println("====> BFS with slack variables");
		printTableau(tableau, b);

		if (tableau[m][n + m].compare(0) != 0) {
			System.out.println("BFS is not valid!");
			return false;
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				this.tableau[i][j] = tableau[i][j];
			}
			this.tableau[i][n] = tableau[i][n + m];
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
					if ((tableau[i][j].compare(0) != 0) && (!flag[j])) {
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

		System.out.println("==> Finding ini BFS");

		if (!findBFS()) {
			System.out.println("Can't find any BFS!");
			return false;
		}

		System.out.println("==> Finding LP opt sol");

		System.out.println("====> BFS with only origin variables");
		printTableau(tableau, b);

		Simplex solver = new Simplex(this.tableau, this.b);
		if (!solver.solve()) {
			System.out.println("Can't solve LP (phase 2)!");
			return false;
		}

		Fraction[][] tableau = solver.gettableau();
		int[] b = solver.getB();
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				this.tableau[i][j] = tableau[i][j];
			}
			this.tableau[i][n] = tableau[i][n];
		}
		for (int i = 0; i < m; i++) {
			this.b[i] = b[i];
		}

		System.out.println("====> LP opt sol");
		printTableau();

		return true;
	}

	public void printTableau() {
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(tableau[i][j] + "\t");
			}
			System.out.println(" | " + tableau[i][n] + "\tBase: " + b[i]);
		}

		for (int j = 0; j < n; j++) {
			System.out.print(tableau[m][j] + "\t");
		}
		System.out.println(" | " + tableau[m][n]);
		System.out.println("======");
	}

	public void printTableau(Fraction[][] tableau, int[] b) {
		int m = tableau.length - 1;
		int n = tableau[0].length - 1;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(tableau[i][j] + "\t");
			}
			System.out.println(" | " + tableau[i][n] + "\tBase: " + b[i]);
		}

		for (int j = 0; j < n; j++) {
			System.out.print(tableau[m][j] + "\t");
		}
		System.out.println(" | " + tableau[m][n]);
		System.out.println("======");
	}


	
	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("data/tp_01.txt");
			Scanner s = new Scanner(fis);
			int m = s.nextInt(); // No constraints
			int n = s.nextInt(); // No variables

			Fraction[][] tableau = new Fraction[m + 1][n + 1];
			// Simplex Tableau
			// Last row: objective function
			// Last column: value
			for (int i = 0; i < m; i++) {
				for (int j = 0; j <= n; j++) {
					tableau[i][j] = new Fraction(s.nextDouble());
				}
			}

			for (int j = 0; j < n; j++) {
				tableau[m][j] = new Fraction(s.nextDouble());
			}

			tableau[m][n] = new Fraction(0);
			TwoPhaseSimplex solver = new TwoPhaseSimplex(tableau);
			solver.solve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}