package com.afyke.pageforge.business.staff.service.impl;

import com.afyke.pageforge.business.staff.converter.OutsourcedStaffConverter;
import com.afyke.pageforge.business.staff.entity.OutsourcedStaffEntity;
import com.afyke.pageforge.business.staff.mapper.OutsourcedStaffMapper;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffCreateRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffPageRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffStatusRequest;
import com.afyke.pageforge.business.staff.request.OutsourcedStaffUpdateRequest;
import com.afyke.pageforge.business.staff.service.OutsourcedStaffService;
import com.afyke.pageforge.business.staff.vo.OutsourcedStaffVO;
import com.afyke.pageforge.common.exception.BusinessException;
import com.afyke.pageforge.common.model.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 外包人员服务实现。 */
@Service
@RequiredArgsConstructor
public class OutsourcedStaffServiceImpl implements OutsourcedStaffService {

    /** 外包人员 Mapper。 */
    private final OutsourcedStaffMapper outsourcedStaffMapper;

    /** 外包人员对象转换器。 */
    private final OutsourcedStaffConverter outsourcedStaffConverter;

    @Override
    public PageResult<OutsourcedStaffVO> page(OutsourcedStaffPageRequest request) {
        LambdaQueryWrapper<OutsourcedStaffEntity> query = new LambdaQueryWrapper<>();
        query.like(StringUtils.isNotBlank(request.getName()),
                        OutsourcedStaffEntity::getName, request.getName())
                .eq(request.getStatus() != null,
                        OutsourcedStaffEntity::getStatus, request.getStatus())
                .orderByDesc(OutsourcedStaffEntity::getId);

        Page<OutsourcedStaffEntity> page = outsourcedStaffMapper.selectPage(
                Page.of(request.getPageNum(), request.getPageSize()), query);
        PageResult<OutsourcedStaffVO> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setRecords(outsourcedStaffConverter.toVOList(page.getRecords()));
        return result;
    }

    @Override
    public OutsourcedStaffVO detail(Long id) {
        return outsourcedStaffConverter.toVO(requireEntity(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(OutsourcedStaffCreateRequest request) {
        OutsourcedStaffEntity entity = outsourcedStaffConverter.toEntity(request);
        outsourcedStaffMapper.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OutsourcedStaffUpdateRequest request) {
        OutsourcedStaffEntity entity = requireEntity(request.getId());
        outsourcedStaffConverter.updateEntity(request, entity);
        outsourcedStaffMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireEntity(id);
        outsourcedStaffMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        outsourcedStaffMapper.deleteByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(OutsourcedStaffStatusRequest request) {
        OutsourcedStaffEntity entity = requireEntity(request.getId());
        entity.setStatus(request.getStatus());
        outsourcedStaffMapper.updateById(entity);
    }

    private OutsourcedStaffEntity requireEntity(Long id) {
        OutsourcedStaffEntity entity = outsourcedStaffMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.badRequest(
                    "OUTSOURCED_STAFF_NOT_FOUND", "外包人员不存在");
        }
        return entity;
    }
}
