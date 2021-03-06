package pers.mihao.ancient_empire.startup.java_base;

import org.junit.Test;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.common.vo.test_dto.Dog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListStreamTest {

    @Test
    public void name() {
        List<Dog> dogList = Dog.getList();
        List newDog = dogList.stream().filter(dog -> dog.getId() == 2).collect(Collectors.toList());
        System.out.println(newDog.size());
    }

    @Test
    public void distinctTest() {List<Dog> dogList = Dog.getList();
        Position position = new Position(1, 2);
        Position position1 = new Position(1, 2);
        Position position2 = new Position(2, 3);
        Position position3 = new Position(2, 3);
        Position position4 = new Position(2, 3);
        Position position5 = new Position(3, 4);
        List<Position> positions = new ArrayList<>();
        positions.add(position);
        positions.add(position1);
        positions.add(position2);
        positions.add(position3);
        positions.add(position4);
        positions.add(position5);

        positions.stream().distinct().forEach(p -> {
            System.out.println(p.getRow() + " " + p.getColumn());
        });
    }

    public static void main(String[] args) {
        System.out.println("hello");
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        strings.add("4");
    }
}
