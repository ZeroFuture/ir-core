package org.zeroqu.ircore.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeroqu.ircore.collection.QueryCollection;
import org.zeroqu.ircore.collection.RecordCollection;
import org.zeroqu.ircore.collection.TokenizerCollection;
import org.zeroqu.ircore.model.ResultRecord;
import org.zeroqu.ircore.ranker.Ranker;
import org.zeroqu.ircore.ranker.RankerFactory;
import org.zeroqu.ircore.ranker.RankerType;
import org.zeroqu.ircore.repository.QueryRepository;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class P10GraphPlotter {
    private static final Logger logger = LoggerFactory.getLogger(P10GraphPlotter.class.getName());

    @Autowired
    public P10GraphPlotter(RecordCollection recordCollection, TokenizerCollection tokenizerCollection,
                           QueryCollection queryCollection) throws IOException {
        logger.info("msg=\"Start generating P@10 histogram plot...\"");

        Ranker ranker = RankerFactory.buildRanker(RankerType.TfIdfRanker, tokenizerCollection.getTokenizer(),
                recordCollection.getDocumentInvertedIndexRepository(),
                recordCollection.getRecordRepository());

        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);

        QueryRepository queryRepository = queryCollection.getQueryRepository();

        Double[] points = queryRepository.getQueries().values().stream()
                .map(query -> {
                    String queryText = query.getQueryText();
                    List<ResultRecord> resultRecords = ranker.rank(queryText);
                    Set<Integer> relevantRecords = query.getRecords()
                            .stream()
                            .map(item -> Integer.parseInt(item.getRecordNum()))
                            .collect(Collectors.toSet());
                    int numberOfRetrievedAndRelevant = 0;

                    for (int numberOfRetrieved = 1; numberOfRetrieved <= 10; numberOfRetrieved++) {
                        ResultRecord resultRecord = resultRecords.get(numberOfRetrieved - 1);
                        int recordNum = Integer.parseInt(resultRecord.getRecord().getRecordNum());
                        if (relevantRecords.contains(recordNum)) numberOfRetrievedAndRelevant++;
                    }
                    return numberOfRetrievedAndRelevant / 10.0;
                }).toArray(Double[]::new);

        double sum = 0.0;
        double[] data = new double[points.length];
        for (int i = 0; i < points.length; i++) {
            data[i] = points[i];
            sum += points[i];
        }

        dataset.addSeries("Precision@10", data, data.length);

        JFreeChart chart = ChartFactory.createHistogram(
                "Precision@10 Histogram",
                "precision",
                "relative frequency",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        logger.info(String.format("msg=\"Successfully generated P@10 histogram plot\" avgP10=%f", sum / data.length));

        String currentPath = System.getProperty("user.dir");
        File file = new File(currentPath + "/p10-graph.png");
        ChartUtilities.saveChartAsPNG(file, chart, 500, 500);
        logger.info(String.format("msg=\"Successfully saved P@10 histogram plot\" filename=%s", file.getName()));
    }
}
