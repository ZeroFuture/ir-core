package org.zeroqu.ircore.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PRGraphPlotter {
    private static final Logger logger = LoggerFactory.getLogger(PRGraphPlotter.class.getName());

    @Autowired
    public PRGraphPlotter(RecordCollection recordCollection, TokenizerCollection tokenizerCollection,
                          QueryCollection queryCollection) throws IOException {
        Ranker ranker = RankerFactory.buildRanker(RankerType.TfIdfRanker, tokenizerCollection.getTokenizer(),
                recordCollection.getInvertedIndexRepository(), recordCollection.getRecordRepository());
        logger.info("msg=\"Start generating PR plot...\"");

        XYSeries series = new XYSeries("PR curve");
        QueryRepository queryRepository = queryCollection.getQueryRepository();

        List<List<double[]>> allPoints = queryRepository.getQueries().values().stream()
                .map(query -> {
                    String queryText = query.getQueryText();
                    List<ResultRecord> resultRecords = ranker.rank(queryText);
                    int numberOfRelevant = query.getResults();
                    Set<Integer> relevantRecords = query.getRecords()
                            .stream()
                            .map(item -> Integer.parseInt(item.getRecordNum()))
                            .collect(Collectors.toSet());
                    int numberOfRetrievedAndRelevant = 0;

                    List<double[]> points = new ArrayList<>();
                    for (int numberOfRetrieved = 1; numberOfRetrieved <= resultRecords.size(); numberOfRetrieved++) {
                        ResultRecord resultRecord = resultRecords.get(numberOfRetrieved - 1);
                        int recordNum = Integer.parseInt(resultRecord.getRecord().getRecordNum());
                        if (relevantRecords.contains(recordNum)) numberOfRetrievedAndRelevant++;
                        double precision = numberOfRetrievedAndRelevant * 1.0 / numberOfRetrieved;
                        double recall = numberOfRetrievedAndRelevant * 1.0 / numberOfRelevant;
                        points.add(new double[] {recall, precision});
                    }
                    return points;
                }).collect(Collectors.toList());

        boolean terminate = false;
        int k = 0;
        while (!terminate) {
            int finalK = k;
            List<double[]> pointsAtK = allPoints.stream()
                    .map(points -> {
                        try {
                           return points.get(finalK);
                        } catch (Exception e) {
                           return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (pointsAtK.size() == 0) {
                terminate = true;
            } else {
                int n = pointsAtK.size();
                double[] sumAtK = pointsAtK.stream()
                        .reduce(new double[] {0.0, 0.0}, (p1, p2) -> new double[] {p1[0] + p2[0], p1[1] + p2[1]});
                double[] avgAtK = new double[] {sumAtK[0] / n, sumAtK[1] / n};
                series.add(avgAtK[0], avgAtK[1]);
            }
            k++;
        }

        XYSeriesCollection data = new XYSeriesCollection(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "PR Graph",
                "Recall",
                "Precision",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        logger.info(String.format("msg=\"Successfully generated PR plot\""));

        String currentPath = System.getProperty("user.dir");
        File file = new File(currentPath + "/pr-graph.png");
        ChartUtilities.saveChartAsPNG(file, chart, 500, 500);
        logger.info(String.format("msg=\"Successfully saved PR plot...\" filename=%s", file.getName()));
    }
}
