import java.io.*;
import java.util.*;

public class Main {
    static int K, M, maxRoundScore;
    static StringBuilder sb = new StringBuilder();
    static List<Integer> scoreList;
    static Queue<Integer> spare;
    static boolean[][] visited;
    static int[][] map;
    static List<int[]> list, temp;
    final static int SIZE = 5;
    static int[] dx = {-1, 0, 1, 0};
	static int[] dy = {0, 1, 0, -1};

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        map = new int[SIZE][SIZE];
        visited = new boolean[SIZE][SIZE];
        spare = new ArrayDeque<>();
        for (int i = 0; i < SIZE; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            for (int j = 0; j < SIZE; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine(), " ");
        while (st.hasMoreTokens()) {
            spare.offer(Integer.parseInt(st.nextToken()));
        }

        simulation();

        for (int value : scoreList) {
            sb.append(value + " ");
        }
        sb.append("\n");

        System.out.println(sb);
    }

    private static void simulation() {
        scoreList = new ArrayList<>();
        for (int t = 0; t < K; t++) {
            maxRoundScore = 0;
            list = new ArrayList<>();
            temp = new ArrayList<>();
            int[] expect = getExpectPosition();

            if (list.size() == 0) {
                return;
            }

            map = rotate(expect[0], expect[1], expect[2]);

            for (int[] pos : list) {
                removeItem(pos[0], pos[1]);
            }

            fillItem();

            while (true) {
                visited = new boolean[SIZE][SIZE];
                int count = 0;
                temp.clear();
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        count += bfs(i, j, map);
                    }
                }
                if (count == 0) break;

                visited = new boolean[SIZE][SIZE];
                for (int[] pos : temp) {
                    removeItem(pos[0], pos[1]);
                }
                fillItem();
                maxRoundScore += count;
            }
            scoreList.add(maxRoundScore);
        }
    }
    private static void removeItem(int x, int y) {
        visited = new boolean[SIZE][SIZE];
        visited[x][y] = true;
        int base = map[x][y];
        map[x][y] = 0;
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{x, y});

        while (!q.isEmpty()) {
            int[] curr = q.poll();
            for (int i = 0; i < 4; i++) {
                int nx = curr[0] + dx[i];
                int ny = curr[1] + dy[i];

                if (!isInRange(nx, ny)) continue;

                if (!visited[nx][ny] && map[nx][ny] == base) {
                    visited[nx][ny] = true;
                    map[nx][ny] = 0;
                    q.offer(new int[]{nx, ny});
                }
            }
        }
    }

    private static void fillItem() {
        for (int j = 0; j < SIZE; j++) {
            for (int i = 4; i >= 0; i--) {
                if (map[i][j] == 0) {
                    map[i][j] = spare.poll();
                }
            }
        }
    }

    private static int[] getExpectPosition() {
        int max = 0;
        int rx = -1;
        int ry = -1;
        int rd = -1;

        for (int d = 1; d < 4; d++) {
            for (int j = 1; j < 4; j++) {
                for (int i = 1; i < 4; i++) {
                    int[][] rotateMap = rotate(i, j, d);
                    visited = new boolean[SIZE][SIZE];
                    temp.clear();

                    int score = 0;
                    for (int r = 0; r < 3; r++) {
                        for (int c = 0; c < 3; c++) {
                            if (!visited[r + i - 1][c + j - 1]) {
                                score += bfs(r + i - 1, c + j - 1, rotateMap);
                            }
                        }
                    }
                    if (score > max) {
                        list.clear();
                        list.addAll(temp);
                        rx = i;
                        ry = j;
                        rd = d;
                        max = score;
                    }
                }
            }
        }
        maxRoundScore += max;
        return new int[]{rx, ry, rd};
    }

    private static int bfs(int x, int y, int[][] map) {
        int count = 1;

        visited[x][y] = true;
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[]{x, y});

        while (!q.isEmpty()) {
            int[] curr = q.poll();

            for (int i = 0; i < 4; i++) {
                int nx = curr[0] + dx[i];
                int ny = curr[1] + dy[i];
                if (!isInRange(nx, ny)) continue;

                if (!visited[nx][ny] && map[nx][ny] == map[x][y]) {
                    ++count;
                    visited[nx][ny] = true;
                    q.offer(new int[]{nx, ny});
                }
            }
        }

        if (count > 2) {
            temp.add(new int[]{x, y});
            return count;
        }
        return 0;
    }

    private static int[][] rotate(int x, int y, int d) {
        int[][] copy = new int[3][3];
        int[][] rotateMap = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                rotateMap[i][j] = map[i][j];
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (d == 1) {
                    copy[i][j] = map[3 - j + x - 2][i + y - 1];
                } else if (d == 2) {
                    copy[i][j] = map[3 - i + x - 2][3 - j + y - 2];
                } else {
                    copy[i][j] = map[j + x - 1][3 - i + y - 2];
                }
            }
        }
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rotateMap[i + x - 1][j + y - 1] = copy[i][j];
            }
        }
        return rotateMap;
    }

    private static boolean isInRange(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return false;
        return true;
    }
}