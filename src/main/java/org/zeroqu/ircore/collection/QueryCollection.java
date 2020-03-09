package org.zeroqu.ircore.collection;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroqu.ircore.model.Queries;
import org.zeroqu.ircore.repository.QueryInvertedIndexRepository;
import org.zeroqu.ircore.repository.QueryRepository;
import org.zeroqu.ircore.util.XMLObjectMapper;

import javax.xml.bind.JAXBException;
import java.io.File;

@Getter
@Component
public class QueryCollection {
    private QueryRepository queryRepository;
    private QueryInvertedIndexRepository queryInvertedIndexRepository;

    @Autowired
    public QueryCollection(TokenizerCollection tokenizerCollection) throws JAXBException {
        File file = new File("./src/main/resources/static/cfc-xml/cfquery.xml");
        Queries queries = XMLObjectMapper.map(file, Queries.class);

        this.queryInvertedIndexRepository = QueryInvertedIndexRepository.build(queries, tokenizerCollection.getTokenizer());
        this.queryRepository = QueryRepository.build(queries);
    }
}
