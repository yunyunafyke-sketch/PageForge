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
    public ResultModel<Void> update(
            @Valid @RequestBody OutsourcedStaffUpdateRequest request) {
        outsourcedStaffService.update(request);
        return ResultModel.success();
    }

    @PostMapping("/delete")
    public ResultModel<Void> delete(@Valid @RequestBody IdRequest request) {
        outsourcedStaffService.delete(request.getId());
        return ResultModel.success();
    }

    @PostMapping("/batch-delete")
    public ResultModel<Void> batchDelete(@Valid @RequestBody IdsRequest request) {
        outsourcedStaffService.batchDelete(request.getIds());
        return ResultModel.success();
    }

    @PostMapping("/change-status")
    public ResultModel<Void> changeStatus(
            @Valid @RequestBody OutsourcedStaffStatusRequest request) {
        outsourcedStaffService.changeStatus(request);
        return ResultModel.success();
    }
}
