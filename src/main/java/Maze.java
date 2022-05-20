
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class Maze extends JFrame {

    private int[][] values;
    private boolean[][] visited;
    private int startRow;
    private int startColumn;
    private ArrayList<JButton> buttonList;
    private int rows;
    private int columns;
    private boolean backtracking;
    private int algorithm;
    private int z;
    private int rounds;
    private ArrayList<Integer> oldPositions=new ArrayList<>();



    public Maze(int algorithm, int size, int startRow, int startColumn) {
        this.algorithm = algorithm;
        Random random = new Random();
        this.values = new int[size][];
        for (int i = 0; i < values.length; i++) {
            int[] row = new int[size];
            for (int j = 0; j < row.length; j++) {
                if (i > 1 || j > 1) {
                    row[j] = random.nextInt(8) % 7 == 0 ? Definitions.OBSTACLE : Definitions.EMPTY;
                } else {
                    row[j] = Definitions.EMPTY;
                }
            }
            values[i] = row;
        }
        values[0][0] = Definitions.EMPTY;
        values[size - 1][size - 1] = Definitions.EMPTY;
        this.visited = new boolean[this.values.length][this.values.length];
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.buttonList = new ArrayList<>();
        this.rows = values.length;
        this.columns = values.length;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setLocationRelativeTo(null);
        GridLayout gridLayout = new GridLayout(rows, columns);
        this.setLayout(gridLayout);
        for (int i = 0; i < rows * columns; i++) {
            int value = values[i / rows][i % columns];
            JButton jButton = new JButton(String.valueOf(i));
            if (value == Definitions.OBSTACLE) {
                jButton.setBackground(Color.BLACK);
            } else {
                jButton.setBackground(Color.WHITE);
            }
            this.buttonList.add(jButton);
            this.add(jButton);
        }
        this.setVisible(true);
        this.setSize(Definitions.WINDOW_WIDTH, Definitions.WINDOW_HEIGHT);
        this.setResizable(false);
    }

    public void checkWayOut() {
        new Thread(() -> {
            boolean result = false;
            switch (this.algorithm) {
                case Definitions.ALGORITHM_DFS:
                    result=dfs(result);
                    break;
                case Definitions.ALGORITHM_BFS:
                    break;
            }
            JOptionPane.showMessageDialog(null,  result ? "FOUND SOLUTION" : "NO SOLUTION FOR THIS MAZE");

        }).start();
    }
    public boolean dfs(boolean result){
        Stack<Integer> myStack=new Stack<>();
        int x=startRow;
        int y=startColumn;
        int[] array;
        myStack.add(startColumn);
        myStack.add(startRow);
        while (!myStack.isEmpty()){
            int currentX=myStack.pop();
            int currentY=myStack.pop();
            if (currentX==(values.length-1)&&currentY==(values.length-1)){
                setSquareAsVisited(currentX,currentY,true);
                result=true;
                break;
            }
            if (currentX==Definitions.NO_WAY_OUT &&currentY==Definitions.NO_WAY_OUT){
                result=false;
                break;
            }
            if (!visited[currentX][currentY]){
                array=getNeighbors(currentX,currentY);
                visited[currentX][currentY]=true;
                setSquareAsVisited(currentX,currentY,true);
                currentX=array[0];
                currentY=array[1];
                myStack.add(currentY);
                myStack.add(currentX);
            }else{
                array=getNeighbors(currentX,currentY);
                currentX=array[0];
                currentY=array[1];
                myStack.add(currentY);
                myStack.add(currentX);
            }
        }
        return result;
    }
    public int[] getNeighbors(int x, int y){
        int[] arrayPosition={x,y};
        int boardSize=this.values.length-1;

        if (x<boardSize && values[x+Definitions.NEIGHBORS_DIST][y]==Definitions.EMPTY && !visited[x+Definitions.NEIGHBORS_DIST][y]){
            this.oldPositions.add(x);
            this.oldPositions.add(y);
            this.rounds+=Definitions.ROUND_VALUE;
            arrayPosition[0]=(x+=Definitions.NEIGHBORS_DIST);
        }
        else if (x>0 && values[x-Definitions.NEIGHBORS_DIST][y]==Definitions.EMPTY && !visited[x-Definitions.NEIGHBORS_DIST][y]){
            this.oldPositions.add(x);
            this.oldPositions.add(y);
            rounds+=Definitions.ROUND_VALUE;
            arrayPosition[0]=(x-=Definitions.NEIGHBORS_DIST);
        }
        else if (y<boardSize && values[x][y+Definitions.NEIGHBORS_DIST]==Definitions.EMPTY && !visited[x][y+Definitions.NEIGHBORS_DIST]){
            this.oldPositions.add(x);
            this.oldPositions.add(y);
            this.rounds+=Definitions.ROUND_VALUE;
            arrayPosition[1]=(y+=Definitions.NEIGHBORS_DIST);
        }
        else if (y>0 && values[x][y-Definitions.NEIGHBORS_DIST]==Definitions.EMPTY && !visited[x][y-Definitions.NEIGHBORS_DIST]){
            this.oldPositions.add(x);
            this.oldPositions.add(y);
            rounds+=Definitions.ROUND_VALUE;
            arrayPosition[1]=(y-=Definitions.NEIGHBORS_DIST);
        }else if (this.rounds>0){
            y=this.oldPositions.get(rounds-1);
            x=this.oldPositions.get(rounds-2);
            this.rounds-=Definitions.ROUND_VALUE;
            arrayPosition[0]=x;
            arrayPosition[1]=y;
            setSquareAsVisited(x,y,false);
        }else{
            arrayPosition[0]=Definitions.NO_WAY_OUT;
            arrayPosition[1]=Definitions.NO_WAY_OUT;
        }
        return arrayPosition;
    }

    public void setSquareAsVisited(int x, int y, boolean visited) {
        try {
            if (visited) {
                if (this.backtracking) {
                    Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE * 5);
                    this.backtracking = false;
                }
                this.visited[x][y] = true;
                for (int i = 0; i < this.visited.length; i++) {
                    for (int j = 0; j < this.visited[i].length; j++) {
                        if (this.visited[i][j]) {
                            if (i == x && y == j) {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.RED);
                            } else {
                                this.buttonList.get(i * this.rows + j).setBackground(Color.BLUE);
                            }
                        }
                    }
                }
            } else {
                this.visited[x][y] = false;
                this.buttonList.get(x * this.columns + y).setBackground(Color.WHITE);
                Thread.sleep(Definitions.PAUSE_BEFORE_BACKTRACK);
                this.backtracking = true;
            }
            if (!visited) {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE / 4);
            } else {
                Thread.sleep(Definitions.PAUSE_BEFORE_NEXT_SQUARE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
