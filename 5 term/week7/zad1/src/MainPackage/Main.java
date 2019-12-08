package MainPackage;

import org.jacop.constraints.XneqY;
import org.jacop.core.IntVar;
import org.jacop.core.Store;
import org.jacop.search.*;

public class Main {
    public static void main(String[] args){

        AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(9);
        adjacencyMatrix.addEdge(0,1);
        adjacencyMatrix.addEdge(1,2);
        adjacencyMatrix.addEdge(1,3);
        adjacencyMatrix.addEdge(1,4);
        adjacencyMatrix.addEdge(2,3);
        adjacencyMatrix.addEdge(2,6);
        adjacencyMatrix.addEdge(2,8);
        adjacencyMatrix.addEdge(3,4);
        adjacencyMatrix.addEdge(3,6);
        adjacencyMatrix.addEdge(4,5);
        adjacencyMatrix.addEdge(4,6);
        adjacencyMatrix.addEdge(4,7);
        adjacencyMatrix.addEdge(5,7);
        adjacencyMatrix.addEdge(6,7);
        adjacencyMatrix.addEdge(6,8);
        adjacencyMatrix.addEdge(7,8);


        Store store = new Store();  // define FD store

        int size = 9;
        IntVar[] v = new IntVar[size];
        for (int i=0; i<size; i++)
            v[i] = new IntVar(store, "v"+i, 1, size);
        for(int i=0; i<size;i++){
            for(int j=0;j<size;j++){
                if(adjacencyMatrix.hasEdge(i, j))
                    store.impose(new XneqY(v[i], v[j]));
            }
        }

        Search<IntVar> search = new DepthFirstSearch<IntVar>();
        SelectChoicePoint<IntVar> select =
                new InputOrderSelect<IntVar>(store, v,
                        new IndomainMin<IntVar>());
        boolean result = search.labeling(store, select);

        if ( result )
            System.out.println("Solution: \n" + v[0]+", "+v[1] +", "+
                    v[2] +", "+v[3]);
        else
            System.out.println("*** No");

        //both directions
        for(int i=0; i<size;i++){
            for(int j=0;j<size;j++){
                if(adjacencyMatrix.hasEdge(i, j))
                    adjacencyMatrix.addEdge(j, i);
            }
        }
        System.out.println(adjacencyMatrix);
    }
}
