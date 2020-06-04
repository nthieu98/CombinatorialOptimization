class DualSimplex extends Simplex {
	public DualSimplex(Fraction[][] tableau, int[] b) {
		super(tableau, b);
	}

	@Override
	public boolean solve() {
		System.out.println("==> Solving Dual:");
		if (!solveDual()) {
			return false;
		}
		System.out.println("====> Dual Sol:");
		printTableau();

		System.out.println("==> Solving Primal:");
		if (!super.solve()) {
			return false;
		}
		System.out.println("====> Primal Sol");
		printTableau();
		return true;
	}

	public static void main(String[] args) {

	}

	public boolean solveDual() {
		this.standardize();
		boolean solvable;
		while (true) {
			int p, q;
			for (p = 0; p < m; p++) {
				if (tableau[p][n].compare(0) < 0) {
					break;
				}
			}
			if (p >= m) {
				solvable = true;
				break;
			}

			for (q = 0; q < n; q++) {
				if (tableau[p][q].compare(0) < 0) {
					break;
				}
			}
			if (q >= n) {
				solvable = false;
				break;
			}
			for (int i = q + 1; i < n; i++) {
				if (tableau[p][i].compare(0) < 0) {
					if (tableau[m][i].divide(tableau[p][i]).compare(tableau[m][q].divide(tableau[p][q])) < 0) {
						q = i;
					}
				}
			}
			pivot(p, q);
		}
		return solvable;
	}
}