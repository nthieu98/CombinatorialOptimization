import java.io.FileInputStream;
import java.util.Scanner;

public class GomoryDualSimplex extends GomoryTwoPhaseSimplex {
	public GomoryDualSimplex(Fraction[][] tableau, boolean[] intgr) {
		super(tableau, intgr);
	}

	@Override
	public boolean solve() {
		int count = 0;
		int res = twoPhaseSimplex(count);
		if (res == -1) {
			return false;
		} else if (res == 1) {
			return true;
		}

		while (true) {
			count++;
			res = dualSimplex(count);
			if (res == -1) {
				return false;
			} else if (res == 1) {
				break;
			}
		}
		return true;
	}

	public int dualSimplex(int count) {
		System.out.println("Loop " + count + ":");
		System.out.println("==> initial");
		printTableau();
		DualSimplex solver = new DualSimplex(tableau, b);
		if (!solver.solve()) {
			System.out.println("Can't solve LP problem");
			return -1;
		}
		Fraction[][] tableau = solver.gettableau();
		this.b = solver.getB();

		for (int i = 0; i < N; i++) {
			res[i] = new Fraction(0);
		}

		int p = -1;
		for (int i = 0; i < m; i++) {
			if (b[i] >= N) {
				continue;
			}
			res[b[i]] = tableau[i][n].divide(tableau[i][b[i]]);
			if ((intgr[b[i]] == true) && (res[b[i]].getDenominator() != 1)) {
				p = i;
				break;
			}
		}
		if (p == -1) {
			return 1;
		}

		Fraction[][] tmp_tableau = new Fraction[m + 2][n + 2];

		tmp_tableau[m + 1][n] = new Fraction(0);
		tmp_tableau[m + 1][n + 1] = new Fraction(this.tableau[m][n]);
		for (int j = 0; j < n; j++) {
			tmp_tableau[m + 1][j] = new Fraction(this.tableau[m][j]);
		}

		tmp_tableau[m][n] = new Fraction(-1, 1);
		tmp_tableau[m][n + 1] = tableau[p][n].minus(tableau[p][n].floor());

		for (int j = 0; j < n; j++) {
			tmp_tableau[m][j] = tableau[p][j].minus(tableau[p][j].floor());
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				tmp_tableau[i][j] = new Fraction(tableau[i][j]);
			}
			tmp_tableau[i][n] = new Fraction(0);
			tmp_tableau[i][n + 1] = new Fraction(tableau[i][n]);
		}

		int[] tmp_b = new int[m + 1];
		for (int i = 0; i < b.length; i++) {
			tmp_b[i] = b[i];
		}
		tmp_b[m] = n;

		this.tableau = tmp_tableau;
		this.b = tmp_b;
		m += 1;
		n += 1;

		System.out.println("End loop " + count + "!");
		return 0;
	}
	
	public static void main(String[] args) {
		String fileName = "";
		if (args.length > 0) {
			fileName = args[0];
		} else {
			fileName = "data/g_02.txt";
		}
		try {
			FileInputStream fis = new FileInputStream(fileName);
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

			boolean[] intgr = new boolean[n];

			for (int j = 0; j < n; j++) {
				intgr[j] = false;
			}

			int ni = s.nextInt();
			for (int i = 0; i < ni; i++) {
				intgr[s.nextInt()] = true;
			}

			GomoryDualSimplex solver = new GomoryDualSimplex(tableau, intgr);
			if (solver.solve()) {
				Fraction[] res = solver.getRes();
				for (int i = 0; i < n; i++) {
					System.out.println("x[" + i + "] = " + res[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
