package com.afyke.pageforge.business.staff.converter;

import com.afyke.pageforge.business.staff.entity.OutsourcedStaffEntity;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffCreateRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffUpdateRequest;
import com.afyke.pageforge.business.staff.vo.OutsourcedStaffVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/** 外包人员对象转换器。 */
@Mapper(componentModel = "spring")
public interface OutsourcedStaffConverter {

    OutsourcedStaffEntity toEntity(OutsourcedStaffCreateRequest request);

    void updateEntity(OutsourcedStaffUpdateRequest request,
                      @MappingTarget OutsourcedStaffEntity entity);

    OutsourcedStaffVO toVO(OutsourcedStaffEntity entity);

    List<OutsourcedStaffVO> toVOList(List<OutsourcedStaffEntity> entities);
}
