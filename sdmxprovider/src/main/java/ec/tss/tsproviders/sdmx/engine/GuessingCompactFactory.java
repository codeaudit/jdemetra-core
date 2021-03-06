/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package ec.tss.tsproviders.sdmx.engine;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import static ec.tss.tsproviders.sdmx.engine.FluentDom.*;
import ec.tss.tsproviders.sdmx.model.SdmxItem;
import ec.tss.tsproviders.sdmx.model.SdmxSeries;
import ec.tss.tsproviders.sdmx.model.SdmxSource;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tss.tsproviders.utils.OptionalTsData;
import ec.tss.tsproviders.utils.Parsers;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Kristof Bayens
 */
public class GuessingCompactFactory extends AbstractDocumentFactory {

    @Override
    public String getName() {
        return "Guessing compact";
    }

    @Override
    public boolean isValid(Document doc) {
        Optional<Node> dataSetNode = lookupDataSetNode(doc);
        return dataSetNode.isPresent() && !hasKeyFamilyRef(dataSetNode.get());
    }

    @Override
    public SdmxSource create(Document doc) {
        Optional<Node> dataSetNode = lookupDataSetNode(doc);
        return new SdmxSource(SdmxSource.Type.COMPACT, getSdmxItems(dataSetNode.get()));
    }

    private static boolean hasKeyFamilyRef(Node dataSetNode) {
        return childNodes(dataSetNode).anyMatch(IS_KEY_FAMILY_REF);
    }

    private static ImmutableList<SdmxItem> getSdmxItems(Node dataSetNode) {
        ImmutableList.Builder<SdmxItem> items = ImmutableList.builder();
        for (Node seriesNode : lookupSeriesNodes(dataSetNode)) {
            items.add(getSdmxSeries(seriesNode));
        }
        return items.build();
    }

    private static SdmxSeries getSdmxSeries(Node seriesNode) {
        ImmutableList<Map.Entry<String, String>> key = getKey(seriesNode);
        TimeFormat timeFormat = getTimeFormat(seriesNode);
        OptionalTsData data = getData(seriesNode, timeFormat);
        return new SdmxSeries(key, NO_ATTRIBUTES, timeFormat, data);
    }

    private static OptionalTsData getData(Node seriesNode, TimeFormat timeFormat) {
        Parsers.Parser<Date> toPeriod = timeFormat.getParser();
        Parsers.Parser<Number> toValue = DEFAULT_DATA_FORMAT.numberParser();
        OptionalTsData.Builder result = new OptionalTsData.Builder(timeFormat.getFrequency(), timeFormat.getAggregationType());
        for (NamedNodeMap obs : lookupObservations(seriesNode)) {
            Date period = toPeriod.parse(lookupPeriod(obs));
            Number value = period != null ? toValue.parse(lookupValue(obs)) : null;
            result.add(period, value);
        }
        return result.build();
    }

    private static String lookupPeriod(NamedNodeMap obs) {
        return obs.getNamedItem("TIME_PERIOD").getNodeValue();
    }

    private static String lookupValue(NamedNodeMap obs) {
        return obs.getNamedItem("OBS_VALUE").getNodeValue();
    }

    private static ImmutableList<Map.Entry<String, String>> getKey(Node seriesNode) {
        return attributes(seriesNode)
                .filter(Predicates.not(IS_TIME_FORMAT))
                .transform(toMapEntry())
                .toList();
    }

    private static TimeFormat getTimeFormat(Node seriesNode) {
        Node node = seriesNode.getAttributes().getNamedItem("TIME_FORMAT");
        String value = node != null ? node.getNodeValue() : "";
        return Enums.getIfPresent(TimeFormat.class, value).or(TimeFormat.UNDEFINED);
    }

    private static Optional<Node> lookupDataSetNode(Document doc) {
        return childNodes(doc.getDocumentElement()).firstMatch(IS_DATA_SET);
    }

    private static Iterable<Node> lookupSeriesNodes(Node dataSetNode) {
        return childNodes(dataSetNode).filter(IS_SERIES);
    }

    private static Iterable<NamedNodeMap> lookupObservations(Node seriesNode) {
        return childNodes(seriesNode).filter(IS_OBS).transform(toAttributes());
    }

    private static final Predicate<Node> IS_DATA_SET = nodeNameEndsWith("DataSet");
    private static final Predicate<Node> IS_KEY_FAMILY_REF = nodeNameEndsWith("KeyFamilyRef");
    private static final Predicate<Node> IS_SERIES = nodeNameEndsWith("Series");
    private static final Predicate<Node> IS_OBS = nodeNameEndsWith("Obs");
    private static final Predicate<Node> IS_TIME_FORMAT = Predicates.compose(Predicates.equalTo("TIME_FORMAT"), toNodeName());

    private static final DataFormat DEFAULT_DATA_FORMAT = new DataFormat(Locale.ROOT, null, null);

    private static final ImmutableList<Map.Entry<String, String>> NO_ATTRIBUTES = ImmutableList.of();
}
