package com.connection.api.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.connection.api.entity.Person;

@Repository
public class PeopleRepository {

    private final Map<Integer, Person> people = new HashMap<>();
    private final Map<Integer,Set<Integer>> connections = new HashMap<>();

    private int nextId = 1;

    public Person save(String name) {
        int id = nextId++;

        Person person = new Person(id, name);

        people.put(id, person);
        connections.put(id, new HashSet<>());

        return person;
    }

    public Optional<Person> findById(int id) {
        return Optional.ofNullable(people.get(id));
    }

    public List<Person> findContacts(int id) {
        ArrayList<Person> contacts = new ArrayList<>();
        for (Integer i : connections.getOrDefault(id, Set.of())) {
            contacts.add(people.get(i));
        }
        return contacts;
    }

    public Set<Integer> findConnectionIds(int id) {
        return connections.getOrDefault(id,Set.of());
    }

    public void addConnection(int a, int b) {

        connections.get(a).add(b);
        connections.get(b).add(a);
    }
}