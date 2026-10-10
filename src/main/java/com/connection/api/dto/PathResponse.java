package com.connection.api.dto;
import java.util.List;

import com.connection.api.entity.Person;

public record PathResponse( boolean connected, Integer hops, List<Person> path){}