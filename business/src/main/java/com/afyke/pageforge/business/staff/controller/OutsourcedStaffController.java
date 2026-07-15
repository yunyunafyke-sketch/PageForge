package com.afyke.pageforge.business.staff.controller;

import com.afyke.pageforge.business.staff.request.OutsourcedStaffCreateRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffPageRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffStatusRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffUpdateRequest;
import com.afyke.pageforge.business.staff.service.OutsourcedStaffService;
import com.afyke.pageforge.business.staff.vo.OutsourcedStaffVO;
import com.afyke.pageforge.common.model.PageResult;
import com.afyke.pageforge.common.model.ResultModel;
import com.afyke.pageforge.common.request.IdRequest;
import com.afyke.pageforge.common.request.IdsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 外包人员接口。 */
@RestController
@RequestMapping("/api/admin/outsourced-staff")
@RequiredArgsConstructor
public class OutsourcedStaffController {

    /** 外包人员服务。 */
    private final OutsourcedStaffService outsourcedStaffService;

    @PostMapping("/page")
    public ResultModel<PageResult<OutsourcedStaffVO>> page(
            @Valid @RequestBody OutsourcedStaffPageRequest request) {
        return ResultModel.success(outsourcedStaffService.page(request));
    }

    @PostMapping("/detail")
    public ResultModel<OutsourcedStaffVO> detail(
            @Valid @RequestBody IdRequest request) {
        return ResultModel.success(outsourcedStaffService.detail(request.getId()));
    }

    @PostMapping("/create")
    public ResultModel<Long> create(
            @Valid @RequestBody OutsourcedStaffCreateRequest request) {
        return ResultModel.success(outsourcedStaffService.create(request));
    }

    @PostMapping("/update")
    public ResultModel<Boolean> update(
            @Valid @RequestBody OutsourcedStaffUpdateRequest request) {
        return ResultModel.success(outsourcedStaffService.update(request));
    }

    @PostMapping("/delete")
    public ResultModel<Boolean> delete(@Valid @RequestBody IdRequest request) {
        return ResultModel.success(outsourcedStaffService.delete(request.getId()));
    }

    @PostMapping("/batch-delete")
    public ResultModel<Boolean> batchDelete(@Valid @RequestBody IdsRequest request) {
        return ResultModel.success(outsourcedStaffService.batchDelete(request.getIds()));
    }

    @PostMapping("/change-status")
    public ResultModel<Boolean> changeStatus(
            @Valid @RequestBody OutsourcedStaffStatusRequest request) {
        return ResultModel.success(outsourcedStaffService.changeStatus(request));
    }
}
