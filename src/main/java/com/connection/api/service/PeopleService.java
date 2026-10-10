package com.connection.api.service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.connection.api.dto.PathResponse;
import com.connection.api.entity.Person;
import com.connection.api.repository.PeopleRepository;

@Service
public class PeopleService {

    private final PeopleRepository repository;

    public PeopleService(PeopleRepository repository) {
        this.repository = repository;
    }

    public Person createPerson(String name) {
        return repository.save(name);
    }

    public void createConnection(int a, int b) {

        if (a == b) {
            throw new IllegalArgumentException("a and b must be different");
        }

        if (repository.findById(a).isEmpty() ) {
            throw new NoSuchElementException(a +" dont exist");
        }
        if ( repository.findById(b).isEmpty()) {
            throw new NoSuchElementException(b + " dont exist");
        }

        repository.addConnection(a, b);
    }

    public List<Person> getContacts(int id) {

        if (repository.findById(id).isEmpty()) {
            throw new NoSuchElementException("no person " + id);
        }

        return repository.findContacts(id);
    }

    public PathResponse findPath(int from, int to) {

        if (repository.findById(from).isEmpty()) {
            throw new NoSuchElementException("no person " + from);
        }

        if (repository.findById(to).isEmpty()) {
            throw new NoSuchElementException("no person " + to);
        }

        if (from == to) {
            return new PathResponse(
                    true,
                    0,
                    List.of(repository.findById(from).get())
            );
        }

        
        Queue<List<Integer>> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(List.of(from));
        visited.add(from);

        while (!queue.isEmpty()) {

            List<Integer> currentPath = queue.poll();

            int current = currentPath.get(currentPath.size() - 1);
            int hops = currentPath.size() - 1;

            
            if (hops == 3) {
                continue;
            }

            for (int neighbour : repository.findConnectionIds(current)) {

                if (visited.contains(neighbour)) {
                    continue;
                }

                List<Integer> newPath =
                        new ArrayList<>(currentPath);

                newPath.add(neighbour);

                if (neighbour == to) {

                    List<Person> people = newPath.stream()
                            .map(repository::findById)
                            .flatMap(Optional::stream)
                            .toList();

                    return new PathResponse(
                            true,
                            newPath.size() - 1,
                            people
                    );
                }

                visited.add(neighbour);
                queue.add(newPath);
            }
        }

        return new PathResponse(false, null, null);
    }

    public Optional<Person> findById(int id) {
        return repository.findById(id);
    }
}