package MainPackage;

public class AdjacencyMatrix {
    boolean[][] a;
    int n;

    public AdjacencyMatrix(int n0) {
        n = n0;
        a = new boolean[n][n];
    }

    void addEdge(int i, int j) {
        a[i][j] = true;
    }
    //void removeEdge(int i, int j) {
        //a[i][j] = false;
    //}
    boolean hasEdge(int i, int j) {
        return a[i][j];
    }

    //public boolean[][] getA() {
        //return a;
    //}

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++)
                s.append(a[i][j]?'x':'.');
            s.append("\n");
        }
        return s.toString();
    }
}
