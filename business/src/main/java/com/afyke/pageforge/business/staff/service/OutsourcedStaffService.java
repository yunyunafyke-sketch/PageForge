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

    boolean update(OutsourcedStaffUpdateRequest request);

    boolean delete(Long id);

    boolean batchDelete(List<Long> ids);

    boolean changeStatus(OutsourcedStaffStatusRequest request);
}
