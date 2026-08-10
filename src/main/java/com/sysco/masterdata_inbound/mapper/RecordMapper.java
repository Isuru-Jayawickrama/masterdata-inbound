package com.sysco.masterdata_inbound.mapper;

import com.sysco.masterdata_inbound.config.FieldMapping;
import com.sysco.masterdata_inbound.config.MasterDataConfig;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RecordMapper {

    public Map<String, Object> map(
            Map<String, Object> source,
            MasterDataConfig config
    ) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        for (FieldMapping field :
                config.getFieldMappings()) {

            result.put(
                    field.getTarget(),
                    source.get(field.getSource())
            );
        }

        return result;
    }
}