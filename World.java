package automata;

import java.util.Random;

public class World {

    public static void main(String... args) {
       new World().populate().print().run(10);
    }

    static Random random = new Random();

    public static int random1or2() {
        return (Math.random() < 0.5) ? 1 : 2;
    }

    // -- // -- // -- // -- // -- // -- // -- // -- //

    final byte size = 10, creatures = 25;

    int day = 0;

    Creature[][] grid = new Creature[size][size];

    World populate() {
        for (int i = 0; i < creatures; i++)
            grid[random.nextInt(size)][random.nextInt(size)] = new Creature();

        return this;
    }

    void run(int days){
        while(days > 0){
            days--;
            passTime();
            print();
        }
    }

    World print() {
        System.out.println("Day " + day);
        for (int x = 0; x < grid.length; x++) {
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] == null) {
                    System.out.print("[           ]");
                } else {
                    System.out.print("[" + grid[x][y] + "]");
                }
            }
            System.out.println();
        }
        return this;
    }

    World passTime() {
        day++;
        for (int x = 0; x < grid.length; x++)
            for (int y = 0; y < grid[x].length; y++) {
                if (grid[x][y] != null) {

                    // A creature can't live the same day more than once
                    if (grid[x][y].momentInWorld == day)
                        continue;

                    grid[x][y].momentInWorld = day;

                    Creature creature = grid[x][y];
                    creature.age++;

                    // Creature dies of age
                    if (creature.age >= creature.life) {
                        if (creature.child == null)
                            creature = null;
                        else
                            creature = creature.child;

                        grid[x][y] = creature;

                        // Skips the rest of the day for this creature: IT DED!
                        continue;
                    }

                    // Creature attempts to move (horrible conditionals not elegant at all but this is Java and this code is bad)
                    int newX = range(x - 1, x + 1);
                    if (newX >= size)
                        newX = 0;
                    if (newX < 0)
                        newX = size - 1;

                    int newY = range(y - 1, y + 1);
                    if (newY >= size)
                        newY = 0;
                    if (newY < 0)
                        newY = size - 1;

                    // Move to free space
                    if (grid[newX][newY] == null) {
                        grid[newX][newY] = creature;
                        grid[x][y] = null;
                    } else {//Space is un use
                        // Same gender fight
                        if (grid[newX][newY].gender == creature.gender) {
                            // The attacker wins on draws
                            if (creature.life - creature.age >= grid[newX][newY].life - grid[newX][newY].age) {
                                grid[newX][newY] = creature;
                                grid[x][y] = null;
                            } else {
                                creature = null;
                                grid[x][y] = creature;
                            }

                        } else { //Different gender try to reproduce
                            // The "Other" always get the creature egg/spawn (a weird specie of monster indeed)
                            if (grid[newX][newY].child == null) {
                                int averageLife = (int) ((creature.life + grid[newX][newY].life) * .5);
                                if (range(2, 8) == 5) // Mutate child sometimes
                                    averageLife = range(averageLife - 4, averageLife + 4);
                                grid[newX][newY].child = new Creature(averageLife, 0);
                            }
                        }
                    }
                }
            }
        return this;
    }

    World passTime(int days) {
        while (days > 0) {
            days--;
            passTime();
        }
        return this;
    }

    public static char nameHead = 'a';

    class Creature {

        char name;
        int life, age, momentInWorld, gender = World.random1or2();
        Creature child = null;

        Creature(int life, int age) {
            name = nameHead;
            nameHead++;
            this.life = life;
            this.age = age;
        }

        Creature() {
            name = nameHead;
            nameHead++;
            this.life = range(15,40);
            this.age = 0;
        }

        @Override
        public String toString() {
            return String.format("%c %02d/%02d %d %s", name, age, life, gender, child == null ? "_" : "x");
        }
    }

    int range(int minvalue, int maxValue) {
        return minvalue + random.nextInt(maxValue - minvalue + 1);
    }

}
