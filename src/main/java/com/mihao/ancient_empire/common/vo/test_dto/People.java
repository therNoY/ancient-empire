package com.mihao.ancient_empire.common.vo.test_dto;


import java.util.List;

public class People {

    public People() {
    }

    public People(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public People(Integer id, String name, List<Dog> dogList) {
        this.id = id;
        this.name = name;
        this.dogList = dogList;
    }

    private Integer id;
    private String name;
    private List<Dog> dogList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Dog> getDogList() {
        return dogList;
    }

    public void setDogList(List<Dog> dogList) {
        this.dogList = dogList;
    }
}
