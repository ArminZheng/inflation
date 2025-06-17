package com.arminzheng.datacenter.convert;

import com.arminzheng.datacenter.dto.DataSourceDTO;
import com.arminzheng.datacenter.model.DataSourcePO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DataSourceConverter {

    DataSourceDTO poToDTO(DataSourcePO po);

    List<DataSourceDTO> poToDTO(List<DataSourcePO> po);

    DataSourcePO dtoToPO(DataSourceDTO dto);

    List<DataSourcePO> dtoToPO(List<DataSourceDTO> dto);

}
