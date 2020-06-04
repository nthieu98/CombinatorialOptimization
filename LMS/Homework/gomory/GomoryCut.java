import java.io.FileInputStream;
import java.util.Scanner;

public class GomoryCut {
	private Fraction[][] tableaux;
	private boolean[] integral;  // = true if i-th variable must be integral
	private int n;
	private int m;
	private Fraction[] result;
	private int N;
	private int M;

	public GomoryCut(Fraction[][] tableaux, boolean[] integral) {
		M = m = tableaux.length - 1;
		N = n = tableaux[0].length - 1;
		this.tableaux = new Fraction[m + 1][n + 1];
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				this.tableaux[i][j] = new Fraction(tableaux[i][j]);
			}
		}
		
        this.result = new Fraction[n];
		
		this.integral = new boolean[n];
		for (int j = 0; j < n; j++) {
            this.integral[j] = integral[j];
        }
	}
	
	public Fraction[] getResult() {
		return result;
	}
	
	public Fraction[][] getTableaux() {
		return tableaux;
	}

	public void printTableaux() {
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(tableaux[i][j] + "\t");
			}
			System.out.println(" | " + tableaux[i][n]);
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

	public boolean solve() {
		int count = 0;
		while (true) {
			count++;
			System.out.println("Loop " + count + ":");
			System.out.println("==> init");
			printTableaux();
			TwoPhaseSimplex solver = new TwoPhaseSimplex(tableaux);
			if (!solver.solve()) {
				System.out.println("Can not solve LP problem");
				return false;
			}
			Fraction[][] tableaux = solver.getTableaux();
			int[] b = solver.getB();

			for (int i = 0; i < N; i++) {
				result[i] = new Fraction(0);
			}

			int p = -1;
			for (int i = 0; i < m; i++) {
				if (b[i] >= N) {
					continue;
				}
				result[b[i]] = tableaux[i][n].divide(tableaux[i][b[i]]);
				if ((integral[b[i]] == true) && (result[b[i]].getDenominator() != 1)) {
					p = i;
					break;
				}
			}
			if (p == -1) {
				break;
			}

			Fraction[][] tmp_tableaux = new Fraction[m + 2][n + 2];
			tmp_tableaux[m + 1][n] = new Fraction(0);
			tmp_tableaux[m + 1][n + 1] = new Fraction(this.tableaux[m][n]);
			for (int j = 0; j < n; j++) {
				tmp_tableaux[m + 1][j] = new Fraction(this.tableaux[m][j]);
			}

			tmp_tableaux[m][n] = new Fraction(-1, 1);
			tmp_tableaux[m][n + 1] = tableaux[p][n].minus(tableaux[p][n].floor());
			for (int j = 0; j < n; j++) {
				tmp_tableaux[m][j] = tableaux[p][j].minus(tableaux[p][j].floor());
			}

			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					tmp_tableaux[i][j] = new Fraction(tableaux[i][j]);
				}
				tmp_tableaux[i][n] = new Fraction(0);
				tmp_tableaux[i][n + 1] = new Fraction(tableaux[i][n]);
			}
			this.tableaux = tmp_tableaux;
			m += 1;
			n += 1;
			System.out.println("End loop " + count + "!");
		}
		return true;
	}

	public static void main(String[] args) {
		try {
			FileInputStream fis = new FileInputStream("data/g_03.txt");
			Scanner s = new Scanner(fis);
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
			boolean[] integral = new boolean[n];
			for (int j = 0; j < n; j++) {
                integral[j] = false;
            }
            int ni = s.nextInt();
            for (int i = 0; i < ni; i++) {
                integral[s.nextInt()] = true;
            }
		
			GomoryCut solver = new GomoryCut(tableaux, integral);
			if (solver.solve()) {
				Fraction[] result = solver.getResult();
				for (int i = 0; i < n; i++) {
					System.out.println("x[" + i + "] = " + result[i]);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}