package org.example.teamcity.api.requests;

import io.restassured.specification.RequestSpecification;
import org.example.teamcity.api.enums.Endpoint;

import javax.xml.stream.events.EndDocument;

public class Request {
    /**
     * Request - это класс, описывающий меняющиеся параметры запроса, такие как:
     * спецификация, эндпойнт (relative URL, model)
     */
    private final RequestSpecification spec;
    private final Endpoint endpoint;

    public Request(RequestSpecification spec, Endpoint endpoint) {
        this.spec = spec;
        this.endpoint = endpoint;
    }
}