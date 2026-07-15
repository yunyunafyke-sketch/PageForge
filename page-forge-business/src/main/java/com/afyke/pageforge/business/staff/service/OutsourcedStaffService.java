package com.afyke.pageforge.business.staff.service;

import com.afyke.pageforge.business.staff.request.OutsourcedStaffCreateRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffPageRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffStatusRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffUpdateRequest;
import com.afyke.pageforge.business.staff.vo.OutsourcedStaffVO;
import com.afyke.pageforge.common.model.PageResult;

import java.util.List;

/** 外包人员服务。 */
public interface OutsourcedStaffService {

    PageResult<OutsourcedStaffVO> page(OutsourcedStaffPageRequest request);

    OutsourcedStaffVO detail(Long id);

    Long create(OutsourcedStaffCreateRequest request);

    void update(OutsourcedStaffUpdateRequest request);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    void changeStatus(OutsourcedStaffStatusRequest request);
}
