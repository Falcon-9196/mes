package com.qcadoo.mes.productionLines.controller.dataProvider;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductionLinesDataProvider {


    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public ProductionLinesResponse getProductionLines(String query) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("Select id as id, number as number, name as name From productionlines_productionline ");
        queryBuilder.append("WHERE number ilike :query LIMIT 10 " );

        Map<String, Object> parameters = Maps.newHashMap();

        String ilikeQuery = "%" + query + "%";
        parameters.put("query", ilikeQuery);
        List<ProductionLineDto> lines =  jdbcTemplate.query(queryBuilder.toString(), parameters,
                new BeanPropertyRowMapper(ProductionLineDto.class));
        ProductionLinesResponse productionLinesResponse = new ProductionLinesResponse();
        productionLinesResponse.setProductionLines(lines);
        return productionLinesResponse;
    }

    public ProductionLinesGridResponse getTechnologiesResponse(int limit, int offset, String sort, String order, String search) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT pl.id, pl.number, pl.name ");
        query.append("FROM productionlines_productionline pl WHERE pl.active = true ");

        StringBuilder queryCount = new StringBuilder();
        queryCount.append("SELECT COUNT(*) ");
        queryCount.append("FROM productionlines_productionline pl WHERE pl.active = true ");

        appendConditions(search, query);
        appendConditions(search, queryCount);

        if(StringUtils.isNotEmpty(sort)) {
            query.append(" ORDER BY " + sort + " " + order);
        }
        query.append(String.format(" LIMIT %d OFFSET %d", limit, offset));

        Map<String, Object> parameters = Maps.newHashMap();

        Integer countRecords = jdbcTemplate.queryForObject(queryCount.toString(), parameters, Long.class).intValue();

        List<ProductionLineDto> lines = jdbcTemplate.query(query.toString(), parameters,
                new BeanPropertyRowMapper(ProductionLineDto.class));

        return new ProductionLinesGridResponse(countRecords, lines);
    }

    private void appendConditions(String search, StringBuilder query) {
        if(StringUtils.isNotEmpty(search)) {
            query.append(" AND (");
            query.append("UPPER(pl.number) LIKE '%").append(search.toUpperCase()).append("%' OR ");
            query.append("UPPER(pl.name) LIKE '%").append(search.toUpperCase()).append("%' ");
            query.append(") ");
        }
    }
}
