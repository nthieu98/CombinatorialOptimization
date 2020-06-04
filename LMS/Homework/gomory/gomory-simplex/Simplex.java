import java.io.FileInputStream;
import java.util.Scanner;

public class Simplex {

	protected Fraction[][] tableau; // Simplex Tableau
	protected int[] b; // Base of i-th constraint
	protected int m; // No constraints
	protected int n; // No variables

	public Fraction[][] gettableau() {
		return tableau;
	}

	public int[] getB() {
		return b;
	}

	public Simplex(Fraction[][] tableau, int[] b) {
		m = tableau.length - 1;
		n = tableau[0].length - 1;
		this.tableau = new Fraction[m + 1][n + 1];
		this.b = new int[m];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				this.tableau[i][j] = new Fraction(tableau[i][j]);
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
					tableau[i][j] = tableau[i][j].minus(tableau[p][j].multiply(tableau[i][q]).divide(tableau[p][q]));
				}
			}
		}
		for (int i = 0; i <= m; i++) {
			if (i != p) {
				tableau[i][q] = new Fraction(0, 1);
			}
		}
		for (int j = 0; j <= n; j++) {
			if (j != q) {
				tableau[p][j] = tableau[p][j].divide(tableau[p][q]);
			}
		}
		tableau[p][q] = new Fraction(1, 1);
	}

	public boolean solve() {
		this.standardize();
		boolean solvable;
		while (true) {
			int p, q;
			for (q = 0; q < n; q++) {
				if (tableau[m][q].compare(0) > 0) {
					break;
				}
			}
			if (q >= n) {
				solvable = true;
				break;
			}

			for (p = 0; p < m; p++) {
				if (tableau[p][q].compare(0) > 0) {
					break;
				}
			}
			if (p >= m) {
				solvable = false;
				break;
			}
			for (int i = p + 1; i < m; i++) {
				if (tableau[i][q].compare(0) > 0) {
					if (tableau[i][n].divide(tableau[i][q]).compare(tableau[p][n].divide(tableau[p][q])) < 0) {
						p = i;
					}
				}
			}
			pivot(p, q);
		}

		return solvable;
	}

	public void standardize() {

		// Basic coeff = 1
		for (int i = 0; i < m; i++) {
			Fraction c = tableau[i][b[i]];
			for (int j = 0; j <= n; j++) {
				tableau[i][j] = tableau[i][j].divide(c);
			}
		}

		for (int i = 0; i < m; i++) {
			Fraction c = tableau[m][b[i]].divide(tableau[i][b[i]]);
			for (int j = 0; j <= n; j++) {
				tableau[m][j] = tableau[m][j].minus(c.multiply(tableau[i][j]));
			}
		}
		System.out.println("====> Standardized");
		printTableau();
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
		System.out.println();
	}

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("data/s_01.txt");
			Scanner s = new Scanner(fis);
			int m = s.nextInt(); // No constraints
			int n = s.nextInt(); // No variables

			Fraction[][] tableau = new Fraction[m + 1][n + 1];
			// Simplex Tableau
			// Last row: objective function
			// Last column: value
			int[] b = new int[m]; // basic variable of i-th constraint
			for (int i = 0; i < m; i++) {
				for (int j = 0; j <= n; j++) {
					tableau[i][j] = new Fraction(s.nextDouble());
				}
				b[i] = s.nextInt();
			}

			for (int j = 0; j < n; j++) {
				tableau[m][j] = new Fraction(s.nextDouble());
			}

			tableau[m][n] = new Fraction(0);

			Simplex solver = new Simplex(tableau, b);
			solver.solve();
			System.out.println(solver.gettableau()[m][n]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}