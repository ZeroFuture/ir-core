package org.zeroqu.ircore.repository;

import lombok.Getter;
import org.zeroqu.ircore.model.Queries;
import org.zeroqu.ircore.model.Query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class QueryRepository implements Serializable {
    private final Map<String, Query> queries;

    private QueryRepository() {
        this.queries = new HashMap<>();
    }

    public static QueryRepository build(Queries queries) {
        QueryRepository queryRepository = new QueryRepository();
        queryRepository.addAllQueries(queries.getQueries());
        return queryRepository;
    }

    public static QueryRepository build(List<Queries> queriesList) {
        QueryRepository queryRepository = new QueryRepository();
        queriesList.forEach(queries -> queryRepository.addAllQueries(queries.getQueries()));
        return queryRepository;
    }

    public static QueryRepository build() {
        return new QueryRepository();
    }

    public void addQuery(Query query) {
        queries.put(query.getQueryNumber(), query);
    }

    public void addAllQueries(List<Query> queries) {
        queries.forEach(this::addQuery);
    }
}
