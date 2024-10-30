package com.comet.opik.domain;

import com.comet.opik.api.DatasetItem;
import com.comet.opik.api.DatasetItemSource;
import com.comet.opik.api.ExperimentItem;
import com.comet.opik.api.FeedbackScore;
import com.comet.opik.api.ScoreSource;
import com.comet.opik.utils.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.comet.opik.api.DatasetItem.DatasetItemPage.Column;
import static com.comet.opik.api.DatasetItem.DatasetItemPage.Column.ColumnType;
import static com.comet.opik.utils.ValidationUtils.CLICKHOUSE_FIXED_STRING_UUID_FIELD_NULL_VALUE;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

class DatasetItemResultMapper {

    private DatasetItemResultMapper() {
    }

    static List<ExperimentItem> getExperimentItems(List[] experimentItemsArrays) {
        if (ArrayUtils.isEmpty(experimentItemsArrays)) {
            return null;
        }

        var experimentItems = Arrays.stream(experimentItemsArrays)
                .filter(experimentItem -> CollectionUtils.isNotEmpty(experimentItem) &&
                        !CLICKHOUSE_FIXED_STRING_UUID_FIELD_NULL_VALUE.equals(experimentItem.get(2).toString()))
                .map(experimentItem -> ExperimentItem.builder()
                        .id(UUID.fromString(experimentItem.get(0).toString()))
                        .experimentId(UUID.fromString(experimentItem.get(1).toString()))
                        .datasetItemId(UUID.fromString(experimentItem.get(2).toString()))
                        .traceId(UUID.fromString(experimentItem.get(3).toString()))
                        .input(getJsonNodeOrNull(experimentItem.get(4)))
                        .output(getJsonNodeOrNull(experimentItem.get(5)))
                        .feedbackScores(getFeedbackScores(experimentItem.get(6)))
                        .createdAt(Instant.parse(experimentItem.get(7).toString()))
                        .lastUpdatedAt(Instant.parse(experimentItem.get(8).toString()))
                        .createdBy(experimentItem.get(9).toString())
                        .lastUpdatedBy(experimentItem.get(10).toString())
                        .build())
                .toList();

        return experimentItems.isEmpty() ? null : experimentItems;
    }

    static JsonNode getJsonNodeOrNull(Object field) {
        if (null == field || StringUtils.isBlank(field.toString())) {
            return null;
        }
        return JsonUtils.getJsonNodeFromString(field.toString());
    }

    private static List<FeedbackScore> getFeedbackScores(Object feedbackScoresRaw) {
        if (feedbackScoresRaw instanceof List[] feedbackScoresArray) {
            var feedbackScores = Arrays.stream(feedbackScoresArray)
                    .filter(feedbackScore -> CollectionUtils.isNotEmpty(feedbackScore) &&
                            !CLICKHOUSE_FIXED_STRING_UUID_FIELD_NULL_VALUE.equals(feedbackScore.getFirst().toString()))
                    .map(feedbackScore -> FeedbackScore.builder()
                            .name(feedbackScore.get(1).toString())
                            .categoryName(Optional.ofNullable(feedbackScore.get(2)).map(Object::toString)
                                    .filter(StringUtils::isNotEmpty).orElse(null))
                            .value(new BigDecimal(feedbackScore.get(3).toString()))
                            .reason(Optional.ofNullable(feedbackScore.get(4)).map(Object::toString)
                                    .filter(StringUtils::isNotEmpty).orElse(null))
                            .source(ScoreSource.fromString(feedbackScore.get(5).toString()))
                            .build())
                    .toList();
            return feedbackScores.isEmpty() ? null : feedbackScores;
        }
        return null;
    }

    static Map.Entry<Long, Set<Column>> groupResults(Map.Entry<Long, Set<Column>> result1,
            Map.Entry<Long, Set<Column>> result2) {

        return Map.entry(result1.getKey() + result2.getKey(), Sets.union(result1.getValue(), result2.getValue()));
    }

    private static Set<Column> mapColumnsField(Map<String, String[]> row) {
        return Optional.ofNullable(row).orElse(Map.of())
                .entrySet()
                .stream()
                .map(columnArray -> new Column(columnArray.getKey(),
                        Set.of(mapColumnType(columnArray.getValue()))))
                .collect(Collectors.toSet());
    }

    private static ColumnType[] mapColumnType(String[] values) {
        return Arrays.stream(values)
                .map(value -> switch (value) {
                    case "String" -> ColumnType.STRING;
                    case "Int64", "Float64", "UInt64", "Double" -> ColumnType.NUMBER;
                    case "Object" -> ColumnType.OBJECT;
                    case "Array" -> ColumnType.ARRAY;
                    case "Bool" -> ColumnType.BOOLEAN;
                    case "Null" -> ColumnType.NULL;
                    default -> ColumnType.NULL;
                })
                .toArray(ColumnType[]::new);
    }

    static Publisher<DatasetItem> mapItem(Result results) {
        return results.map((row, rowMetadata) -> {

            Map<String, JsonNode> data = getData(row);

            JsonNode input = getJsonNode(row, data, "input");
            JsonNode expectedOutput = getJsonNode(row, data, "expected_output");
            JsonNode metadata = getJsonNode(row, data, "metadata");

            if (!data.containsKey("input")) {
                data.put("input", input);
            }

            if (!data.containsKey("expected_output")) {
                data.put("expected_output", expectedOutput);
            }

            if (!data.containsKey("metadata")) {
                data.put("metadata", metadata);
            }

            return DatasetItem.builder()
                    .id(row.get("id", UUID.class))
                    .input(input)
                    .data(data)
                    .expectedOutput(expectedOutput)
                    .metadata(metadata)
                    .source(DatasetItemSource.fromString(row.get("source", String.class)))
                    .traceId(Optional.ofNullable(row.get("trace_id", String.class))
                            .filter(s -> !s.isBlank())
                            .map(UUID::fromString)
                            .orElse(null))
                    .spanId(Optional.ofNullable(row.get("span_id", String.class))
                            .filter(s -> !s.isBlank())
                            .map(UUID::fromString)
                            .orElse(null))
                    .experimentItems(getExperimentItems(row.get("experiment_items_array", List[].class)))
                    .lastUpdatedAt(row.get("last_updated_at", Instant.class))
                    .createdAt(row.get("created_at", Instant.class))
                    .createdBy(row.get("created_by", String.class))
                    .lastUpdatedBy(row.get("last_updated_by", String.class))
                    .build();
        });
    }

    private static Map<String, JsonNode> getData(Row row) {
        return Optional.ofNullable(row.get("data", Map.class))
                .filter(s -> !s.isEmpty())
                .map(value -> (Map<String, String>) value)
                .stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(entry -> Map.entry(entry.getKey(), JsonUtils.getJsonNodeFromString(entry.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static JsonNode getJsonNode(Row row, Map<String, JsonNode> data, String key) {
        JsonNode json = null;

        if (data.containsKey(key)) {
            json = data.get(key);
        }

        if (json == null) {
            json = Optional.ofNullable(row.get(key, String.class))
                    .filter(s -> !s.isBlank())
                    .map(JsonUtils::getJsonNodeFromString).orElse(null);
        }

        return json;
    }

    static String getOrDefault(JsonNode jsonNode) {
        return Optional.ofNullable(jsonNode).map(JsonNode::toString).orElse("");
    }

    static Map<String, String> getOrDefault(Map<String, JsonNode> data) {
        return Optional.ofNullable(data)
                .filter(not(Map::isEmpty))
                .stream()
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().toString()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    static String getOrDefault(UUID value) {
        return Optional.ofNullable(value).map(UUID::toString).orElse("");
    }

    static Publisher<Map.Entry<Long, Set<Column>>> mapCountAndColumns(Result result) {
        return result.map((row, rowMetadata) -> {
            Long count = extractCountFromResult(row);
            Map<String, String[]> columnsMap = extractColumnsField(row);
            return Map.entry(count, mapColumnsField(columnsMap));
        });
    }

    private static Long extractCountFromResult(Row row) {
        return row.get("count", Long.class);
    }

    private static Map<String, String[]> extractColumnsField(Row row) {
        return (Map<String, String[]>) row.get("columns", Map.class);
    }

    static Publisher<Long> mapCount(Result result) {
        return result.map((row, rowMetadata) -> extractCountFromResult(row));
    }

    static Mono<Set<Column>> mapColumns(Result result) {
        return Mono.from(result.map((row, rowMetadata) -> {
            Map<String, String[]> columnsMap = extractColumnsField(row);
            return DatasetItemResultMapper.mapColumnsField(columnsMap);
        }));
    }
}