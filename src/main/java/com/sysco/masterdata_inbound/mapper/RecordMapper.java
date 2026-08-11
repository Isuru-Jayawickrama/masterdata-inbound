package com.sysco.masterdata_inbound.mapper;

import com.sysco.masterdata_inbound.config.FieldMapping;
import com.sysco.masterdata_inbound.config.MasterDataConfig;
import com.sysco.masterdata_inbound.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RecordMapper {

    public Map<String, Object> map(Map<String, Object> source, MasterDataConfig config) {

        Map<String, Object> result = new LinkedHashMap<>();

        for (FieldMapping field : config.getFieldMappings()) {
            if (!source.containsKey(field.getSource())) {

                throw new ValidationException("Source field not found: " + field.getSource());
            }

            result.put(field.getTarget(), source.get(field.getSource()));
        }

        return result;
    }
}